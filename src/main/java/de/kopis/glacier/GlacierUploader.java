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

import joptsimple.OptionSet;

public class GlacierUploader {

  public static void main(final String[] args) {
    final GlacierUploaderOptionParser optionParser = new GlacierUploaderOptionParser();

    final OptionSet options = optionParser.parse(args);

    try {
      final File credentialFile = options.valueOf(optionParser.CREDENTIALS);
      final URL endpointUrl = options.valueOf(optionParser.ENDPOINT);

      if (options.has(optionParser.UPLOAD)) {
        System.out.println("Starting to upload " + options.valueOf(optionParser.UPLOAD) + "...");
        final CommandLineGlacierUploader glacierUploader = new CommandLineGlacierUploader(credentialFile);
        glacierUploader.upload(endpointUrl, options.valueOf(optionParser.VAULT), options.valueOf(optionParser.UPLOAD));
      } else if (options.has(optionParser.INVENTORY_LISTING)) {
        final VaultInventoryLister vaultInventoryLister = new VaultInventoryLister(credentialFile);
        if (options.hasArgument(optionParser.INVENTORY_LISTING)) {
          vaultInventoryLister.retrieveInventoryListing(endpointUrl, options.valueOf(optionParser.VAULT),
              options.valueOf(optionParser.INVENTORY_LISTING));
        } else {
          System.out.println("Listing inventory for vault " + options.valueOf(optionParser.VAULT) + "...");
          vaultInventoryLister.startInventoryListing(endpointUrl, options.valueOf(optionParser.VAULT));
        }
      } else if (options.has(optionParser.DOWNLOAD)) {
        final GlacierArchiveDownloader downloader = new GlacierArchiveDownloader(credentialFile);
        downloader.download(endpointUrl, options.valueOf(optionParser.VAULT), options.valueOf(optionParser.DOWNLOAD));
      } else if (options.has(optionParser.CREATE_VAULT)) {
        final GlacierVaultCreator vaultCreator = new GlacierVaultCreator(credentialFile);
        vaultCreator.createVault(endpointUrl, options.valueOf(optionParser.CREATE_VAULT));
      } else {
        try {
          System.out.println("Ooops, can't determine what you want to do. Check your options.");
          optionParser.printHelpOn(System.err);
        } catch (final IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    } catch (final IOException e) {
      System.out.println("Ooops, something is wrong with your setup.");
      e.printStackTrace();
    }
  }

}
