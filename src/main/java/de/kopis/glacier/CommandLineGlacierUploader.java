package de.kopis.glacier;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;

public class CommandLineGlacierUploader extends AbstractGlacierCommand {

  public CommandLineGlacierUploader(File credentials) throws IOException {
    super(credentials);
  }

  public void upload(URL endpointUrl, String vaultName, File uploadFile) {
    try {
      System.out.println("Using endpoint " + endpointUrl);
      client.setEndpoint(endpointUrl.toString());

      System.out.println("Starting upload of " + uploadFile);
      final ArchiveTransferManager atm = new ArchiveTransferManager(client, credentials);
      final String archiveId = atm.upload(vaultName, uploadFile.getName(), uploadFile).getArchiveId();
      System.out.println("Uploaded archive " + archiveId);
    } catch (IOException e) {
      System.err.println("Something went wrong while uploading " + uploadFile + ".");
      e.printStackTrace();
    }
  }
}
