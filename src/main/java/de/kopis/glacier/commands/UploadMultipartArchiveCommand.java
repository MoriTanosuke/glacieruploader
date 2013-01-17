package de.kopis.glacier.commands;

/*
 * #%L
 * uploader
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 Carsten Ringe
 * Copyright (C) 2013 Deux Huit Huit
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.	If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import joptsimple.OptionSet;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.glacier.TreeHashGenerator;
import com.amazonaws.services.glacier.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.glacier.model.CompleteMultipartUploadResult;
import com.amazonaws.services.glacier.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.glacier.model.InitiateMultipartUploadResult;
import com.amazonaws.services.glacier.model.UploadMultipartPartRequest;
import com.amazonaws.services.glacier.model.UploadMultipartPartResult;
import com.amazonaws.util.BinaryUtils;

import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import de.kopis.glacier.printers.HumanReadableSize;

public class UploadMultipartArchiveCommand extends AbstractCommand {

  public UploadMultipartArchiveCommand(final URL endpoint, final File credentials) throws IOException {
    super(endpoint, credentials);
  }

  // from:
  // http://docs.amazonwebservices.com/amazonglacier/latest/dev/uploading-an-archive-mpu-using-java.html
  public void upload(final String vaultName, final File uploadFile, final Integer partSize) {
    final String hPartSize = HumanReadableSize.parse(partSize);
    final String hTotalSize = HumanReadableSize.parse(uploadFile.length());

    log.info(String.format("Multipart uploading %s (%s) to vault %s with part size %s (%s).", uploadFile.getName(), hTotalSize, vaultName, partSize, hPartSize));
    try {
      final String uploadId = this.initiateMultipartUpload(vaultName, partSize, uploadFile.getName());
      final String checksum = this.uploadParts(uploadId, uploadFile, vaultName, partSize);
      final CompleteMultipartUploadResult result = this.completeMultiPartUpload(uploadId, uploadFile, vaultName, checksum);

      log.info("Uploaded Archive ID: " + result.getArchiveId());
      log.info("Local Checksum: " + checksum);
      log.info("Remote Checksum: " + result.getChecksum());
      if (checksum.equals(result.getChecksum())) {
    	  log.info("Checksums are identical, upload succeeded.");
      } else {
    	  log.error("Checksums are different, upload failed.");
      }

    } catch (final IOException e) {
      log.error("Something went wrong while multipart uploading " + uploadFile + "." + e.getLocalizedMessage(), e);
    } catch (AmazonServiceException e) {
      log.error("Something went wrong at Amazon while uploading " + uploadFile + "." + e.getLocalizedMessage(), e);
    } catch (NoSuchAlgorithmException e) {
      log.error("No such algorithm found " + e.getLocalizedMessage(), e);
    } catch (AmazonClientException e) {
      log.error("Something went wrong with the Amazon Client." + e.getLocalizedMessage(), e);
    }
  }

  private String initiateMultipartUpload(final String vaultName, final Integer partSize, final String fileName) {
    // Initiate
    InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest()
    	.withVaultName(vaultName)
        .withArchiveDescription(fileName)
        .withPartSize(partSize.toString());

    InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);

    log.info("Upload ID (token): " + result.getUploadId());

    return result.getUploadId();
  }

  private String uploadParts(String uploadId, File file, final String vaultName, final Integer partSize)
      throws AmazonServiceException, NoSuchAlgorithmException, AmazonClientException, IOException {
    int filePosition = 0;
    long currentPosition = 0;
    byte[] buffer = new byte[partSize];
    List<byte[]> binaryChecksums = new LinkedList<byte[]>();

    FileInputStream fileToUpload = new FileInputStream(file);
    String contentRange;
    int read = 0;
    int counter = 1;
    int total = (int) Math.ceil(file.length() / (double)partSize);
    while (currentPosition < file.length()) {
      read = fileToUpload.read(buffer, filePosition, buffer.length);
      if (read == -1) {
        break;
      }
      byte[] bytesRead = Arrays.copyOf(buffer, read);

      contentRange = String.format("bytes %s-%s/*", currentPosition, currentPosition + read - 1);
      String checksum = TreeHashGenerator.calculateTreeHash(new ByteArrayInputStream(bytesRead));
      byte[] binaryChecksum = BinaryUtils.fromHex(checksum);
      binaryChecksums.add(binaryChecksum);

      // Upload part.
      UploadMultipartPartRequest partRequest = new UploadMultipartPartRequest()
          .withVaultName(vaultName)
          .withBody(new ByteArrayInputStream(bytesRead))
          .withChecksum(checksum)
          .withRange(contentRange)
          .withUploadId(uploadId);

      UploadMultipartPartResult partResult = client.uploadMultipartPart(partRequest);
      log.info(String.format("Part %d/%d (%s) uploaded, checksum: %s", counter, total, contentRange, partResult.getChecksum()));

      currentPosition = currentPosition + read;
      counter++;
    }
    String checksum = TreeHashGenerator.calculateTreeHash(binaryChecksums);
    return checksum;
  }

  private CompleteMultipartUploadResult completeMultiPartUpload(String uploadId, File file, final String vaultName, String checksum)
      throws NoSuchAlgorithmException, IOException {
    CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest().withVaultName(vaultName)
        .withUploadId(uploadId)
        .withChecksum(checksum)
        .withArchiveSize(String.valueOf(file.length()));

    CompleteMultipartUploadResult compResult = client.completeMultipartUpload(compRequest);
    
    return compResult;
  }

  @Override
  public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
    final String vaultName = options.valueOf(optionParser.VAULT);
    final List<File> optionsFiles = options.valuesOf(optionParser.MULTIPARTUPLOAD);
    final Integer partSize = options.valueOf(optionParser.PARTSIZE);
    final List<String> nonOptions = options.nonOptionArguments();
    final ArrayList<File> files = optionParser.mergeNonOptionsFiles(optionsFiles, nonOptions);
    
    for (File uploadFile : files) {
    	this.upload(vaultName, uploadFile, partSize);
    }
  }

  @Override
  public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
    return options.has(optionParser.MULTIPARTUPLOAD) && options.has(optionParser.VAULT);
  }
}
