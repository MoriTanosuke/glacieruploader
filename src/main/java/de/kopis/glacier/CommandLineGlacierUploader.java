package de.kopis.glacier;

import java.io.File;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;

public class CommandLineGlacierUploader {

  public void upload(String endpoint, String vaultName, String file) {
    final String credentialFile = "/aws.properties";

    try {
      final AWSCredentials credentials = new PropertiesCredentials(
          CommandLineGlacierUploader.class.getResourceAsStream(credentialFile));

      final AmazonGlacierClient client = new AmazonGlacierClient(credentials);
      System.out.println("Using endpoint " + endpoint);
      client.setEndpoint(endpoint);

      final File uploadFile = new File(file);
      System.out.println("Starting upload of " + uploadFile);
      final ArchiveTransferManager atm = new ArchiveTransferManager(client, credentials);
      final String archiveId = atm.upload(vaultName, uploadFile.getName(), uploadFile).getArchiveId();
      System.out.println("Uploaded archive " + archiveId);
    } catch (NullPointerException e) {
      System.err.println("Can not read credentials. Check the file " + credentialFile + " for errors.");
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("Something went wrong while uploading " + file + ".");
      e.printStackTrace();
    }
  }
}
