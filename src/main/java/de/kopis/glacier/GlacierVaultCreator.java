package de.kopis.glacier;

/*
 * #%L
 * uploader
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 Carsten Ringe
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
