package de.kopis.glacier;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.amazonaws.services.glacier.model.DeleteArchiveRequest;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;

public class GlacierArchiveDownloader extends AbstractGlacierCommand {

  public GlacierArchiveDownloader(final File credentials) throws IOException {
    super(credentials);
  }

  public void download(final URL endpointUrl, final String vaultName, final String archiveId) {
    System.out.println("Downloading archive " + archiveId + " from vault " + vaultName + "...");

    try {
      final File downloadFile = File.createTempFile("glacier-", ".dl");
      final ArchiveTransferManager atm = new ArchiveTransferManager(client, sqs, sns);
      atm.download(vaultName, archiveId, downloadFile);
      System.out.println("Archive downloaded to " + downloadFile);
    } catch (final IOException e) {
      System.err.println("Can not download archive " + archiveId + " from vault " + vaultName + ".");
      e.printStackTrace();
    }
  }

  public void delete(final URL endpointUrl, final String vaultName, final String archiveId) {
    System.out.println("Deleting archive " + archiveId + " from vault " + vaultName + "...");
    final DeleteArchiveRequest deleteArchiveRequest = new DeleteArchiveRequest(vaultName, archiveId);
    client.deleteArchive(deleteArchiveRequest);
    System.out.println("Archive " + archiveId + " deleted from vault " + vaultName + ".");
  }

}
