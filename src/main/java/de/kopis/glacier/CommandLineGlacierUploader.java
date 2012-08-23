package de.kopis.glacier;

import java.io.File;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;

public class CommandLineGlacierUploader {

  public static void main(final String[] args) throws IOException {
    if (args.length != 1) {
      System.err
          .println("You have to provide a path to an archive file as the only parameter to this application.");
    }

    final File uploadFile = new File(args[0]);

    final AWSCredentials credentials = new PropertiesCredentials(
        CommandLineGlacierUploader.class.getResourceAsStream("/aws.properties"));

    final AmazonGlacierClient client = new AmazonGlacierClient(credentials);
    client.setEndpoint("https://glacier.eu-west-1.amazonaws.com");

    final ArchiveTransferManager atm = new ArchiveTransferManager(client,
        credentials);
    final String archiveId = atm.upload("cr_backup",
        "archive-" + uploadFile.getName(), uploadFile).getArchiveId();
    System.out.println("Uploaded archive " + archiveId);
  }
}
