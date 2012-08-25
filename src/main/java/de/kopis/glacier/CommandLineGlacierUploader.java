package de.kopis.glacier;

import java.io.File;
import java.io.IOException;

import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;

public class CommandLineGlacierUploader extends AbstractGlacierCommand {

  public CommandLineGlacierUploader() throws IOException {
    super();
  }

  public void upload(String endpoint, String vaultName, String file) {
    try {
      System.out.println("Using endpoint " + endpoint);
      client.setEndpoint(endpoint);

      final File uploadFile = new File(file);
      System.out.println("Starting upload of " + uploadFile);
      final ArchiveTransferManager atm = new ArchiveTransferManager(client, credentials);
      final String archiveId = atm.upload(vaultName, uploadFile.getName(), uploadFile).getArchiveId();
      System.out.println("Uploaded archive " + archiveId);
    } catch (IOException e) {
      System.err.println("Something went wrong while uploading " + file + ".");
      e.printStackTrace();
    }
  }
}
