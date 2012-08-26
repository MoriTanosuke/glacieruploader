package de.kopis.glacier;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.glacier.model.CreateVaultRequest;
import com.amazonaws.services.glacier.model.CreateVaultResult;
import com.amazonaws.services.glacier.model.DeleteVaultRequest;

public class GlacierVaultCreator extends AbstractGlacierCommand {
  public GlacierVaultCreator(final File credentials) throws IOException {
    super(credentials);
  }

  public void createVault(final URL endpoint, final String vaultName) {
    System.out.println("Creating vault " + vaultName + "...");
    final CreateVaultRequest createVaultRequest = new CreateVaultRequest(vaultName);
    try {
      final CreateVaultResult createVaultResult = client.createVault(createVaultRequest);
      System.out.println("Vault " + vaultName + " created.");
      describeVault(vaultName);
    } catch (final AmazonServiceException e) {
      System.err.println("Couldn't create vault.");
      e.printStackTrace();
    } catch (final AmazonClientException e) {
      System.err.println("Couldn't create vault.");
      e.printStackTrace();
    }
  }

  public void deleteVault(final URL endpoint, final String vaultName) {
    System.out.println("Deleting vault " + vaultName + "...");
    DeleteVaultRequest deleteVaultRequest = new DeleteVaultRequest(vaultName);
    // TODO check for notifications first?
    client.deleteVault(deleteVaultRequest);
    System.out.println("Vault " + vaultName + " deleted.");
  }
}
