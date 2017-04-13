package de.kopis.glacier.commands;

/*
 * #%L
 * glacieruploader-command
 * %%
 * Copyright (C) 2012 - 2016 Carsten Ringe
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.TreeHashGenerator;
import com.amazonaws.services.glacier.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.glacier.model.CompleteMultipartUploadResult;
import com.amazonaws.services.glacier.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.glacier.model.InitiateMultipartUploadResult;
import com.amazonaws.services.glacier.model.UploadMultipartPartRequest;
import com.amazonaws.services.glacier.model.UploadMultipartPartResult;
import com.amazonaws.services.s3.internal.InputSubstream;
import com.amazonaws.services.s3.internal.RepeatableFileInputStream;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.util.BinaryUtils;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import de.kopis.glacier.printers.HumanReadableSize;
import joptsimple.OptionSet;

public class UploadMultipartArchiveCommand extends AbstractCommand {

    public UploadMultipartArchiveCommand(AmazonGlacier client, AmazonSQS sqs, AmazonSNS sns) {
        super(client, sqs, sns);
    }

    // from:
    // http://docs.amazonwebservices.com/amazonglacier/latest/dev/uploading-an-archive-mpu-using-java.html
    private void upload(final String vaultName, final File uploadFile, final Long partSize) {
        final String hPartSize = HumanReadableSize.parse(partSize);
        final String hTotalSize = HumanReadableSize.parse(uploadFile.length());

        log.info(String.format("Multipart uploading %s (%s) to vault %s with part size %s (%s).", uploadFile.getName(),
                hTotalSize, vaultName, partSize, hPartSize));
        try {
            final String uploadId = this.initiateMultipartUpload(vaultName, partSize, uploadFile.getName());
            final String checksum = this.uploadParts(uploadId, uploadFile, vaultName, partSize);
            final CompleteMultipartUploadResult result = this.completeMultiPartUpload(uploadId, uploadFile, vaultName,
                    checksum);

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

    private String initiateMultipartUpload(final String vaultName, final Long partSize, final String fileName) {
        // Initiate
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest().withVaultName(vaultName)
                .withArchiveDescription(fileName).withPartSize(partSize.toString());

        InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);

        log.info("Upload ID (token): " + result.getUploadId());

        return result.getUploadId();
    }

    /* This method contains a derivative of work from the following source:
     *
     * https://github.com/aws/aws-sdk-java/blob/master/src/main/java/com/amazonaws/services/glacier/transfer/ArchiveTransferManager.java?source=c
     *
     * from the 1.8.5 tag in the source tree.
     *
     * Copyright 2012-2014 Amazon Technologies, Inc.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at:
     *
     *    http://aws.amazon.com/apache2.0
     */
    private String uploadParts(String uploadId, File file, final String vaultName, final Long partSize)
            throws NoSuchAlgorithmException, AmazonClientException, IOException {
        FileInputStream fileToUpload = null;
        String checksum = "";
        try {
            long currentPosition = 0;
            List<byte[]> binaryChecksums = new LinkedList<byte[]>();
            fileToUpload = new FileInputStream(file);
            int counter = 1;
            int total = (int) Math.ceil(file.length() / (double) partSize);
            while (currentPosition < file.length()) {
                long length = partSize;
                if (currentPosition + partSize > file.length()) {
                    length = file.length() - currentPosition;
                }

                Exception failedException = null;
                boolean completed = false;
                int tries = 0;

                while (!completed && tries < 5) {
                    tries++;
                    try (InputStream inputSubStream = newInputSubstream(file, currentPosition, length)) {
                        inputSubStream.mark(-1);
                        checksum = TreeHashGenerator.calculateTreeHash(inputSubStream);
                        byte[] binaryChecksum = BinaryUtils.fromHex(checksum);
                        inputSubStream.reset();
                        String range = "bytes " + currentPosition + "-" + (currentPosition + length - 1) + "/*";
                        UploadMultipartPartRequest req = new UploadMultipartPartRequest()
                                .withChecksum(checksum)
                                .withBody(inputSubStream)
                                .withRange(range)
                                .withUploadId(uploadId)
                                .withVaultName(vaultName);
                        try {
                            UploadMultipartPartResult partResult = client.uploadMultipartPart(req);
                            log.info(String.format("Part %d/%d (%s) uploaded, checksum: %s", counter, total, range, partResult.getChecksum()));
                            completed = true;
                            binaryChecksums.add(binaryChecksum);
                        } catch (Exception e) {
                            failedException = e;
                        }
                    }
                }
                if (!completed && failedException != null) {
                    throw new AmazonClientException("Failed operation", failedException);
                }
                currentPosition += partSize;
                ++counter;
            }

            checksum = TreeHashGenerator.calculateTreeHash(binaryChecksums);
        } finally {
            if (fileToUpload != null) {
                fileToUpload.close();
            }
        }

        return checksum;
    }

    private InputSubstream newInputSubstream(File file, long startingPosition, long length) {
        InputSubstream in = null;
        try {
            in = new InputSubstream(new RepeatableFileInputStream(file), startingPosition, length, true);
        } catch (FileNotFoundException e) {
            throw new AmazonClientException("Unable to find file '" + file.getAbsolutePath() + "'", e);
        }
        return in;
    }

    private CompleteMultipartUploadResult completeMultiPartUpload(String uploadId, File file, final String vaultName,
                                                                  String checksum) throws NoSuchAlgorithmException, IOException {
        CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest().withVaultName(vaultName)
                .withUploadId(uploadId).withChecksum(checksum).withArchiveSize(String.valueOf(file.length()));

        return client.completeMultipartUpload(compRequest);
    }

    @Override
    public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
        final String vaultName = options.valueOf(optionParser.VAULT);
        final List<File> optionsFiles = options.valuesOf(optionParser.MULTIPARTUPLOAD);
        final Long partSize = options.valueOf(optionParser.PARTSIZE);
        final List<String> nonOptions = options.nonOptionArguments();
        final List<File> files = optionParser.mergeNonOptionsFiles(optionsFiles, nonOptions);

        for (File uploadFile : files) {
            this.upload(vaultName, uploadFile, partSize);
        }
    }

    @Override
    public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
        return options.has(optionParser.MULTIPARTUPLOAD) && options.hasArgument(optionParser.MULTIPARTUPLOAD);
    }
}
