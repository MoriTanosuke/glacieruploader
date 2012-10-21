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
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import joptsimple.OptionSet;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.kopis.glacier.util.TreeHashCalculator;

public class GlacierUploader {
  private static final Log log = LogFactory.getLog(GlacierUploader.class);
  private static Configuration config;

  public static void main(String[] args) {
    config = new CompositeConfiguration();
    ((CompositeConfiguration) config).addConfiguration(new SystemConfiguration());
    try {
      ((CompositeConfiguration) config).addConfiguration(new PropertiesConfiguration(new File(System
          .getProperty("user.home"), ".glacieruploaderrc")));
    } catch (ConfigurationException e1) {
      log.warn("Can not read configuration", e1);
    }

    final GlacierUploaderOptionParser optionParser = new GlacierUploaderOptionParser(config);
    final OptionSet options = optionParser.parse(args);

    try {
      final File credentialFile = options.valueOf(optionParser.CREDENTIALS);
      final URL endpointUrl = new URL(options.valueOf(optionParser.ENDPOINT));
      final String vaultName = options.valueOf(optionParser.VAULT);

      if (options.has(optionParser.UPLOAD)) {
        log.info("Starting to upload " + options.valueOf(optionParser.UPLOAD) + "...");
        final CommandLineGlacierUploader glacierUploader = new CommandLineGlacierUploader(endpointUrl, credentialFile);
        glacierUploader.upload(vaultName, options.valueOf(optionParser.UPLOAD));
      } else if (options.has(optionParser.INVENTORY_LISTING)) {
        final VaultInventoryLister vaultInventoryLister = new VaultInventoryLister(endpointUrl, credentialFile);
        if (options.hasArgument(optionParser.INVENTORY_LISTING)) {
          vaultInventoryLister.retrieveInventoryListing(endpointUrl, vaultName,
              options.valueOf(optionParser.INVENTORY_LISTING));
        } else {
          log.info("Listing inventory for vault " + vaultName + "...");
          vaultInventoryLister.startInventoryListing(vaultName);
        }
      } else if (options.has(optionParser.DOWNLOAD)) {
        final GlacierArchiveDownloader downloader = new GlacierArchiveDownloader(endpointUrl, credentialFile);
        downloader.download(vaultName, options.valueOf(optionParser.DOWNLOAD),
            options.valueOf(optionParser.TARGET_FILE));
      } else if (options.has(optionParser.DELETE_ARCHIVE)) {
        final GlacierArchiveDeleter deleter = new GlacierArchiveDeleter(endpointUrl, credentialFile);
        deleter.delete(vaultName, options.valueOf(optionParser.DELETE_ARCHIVE));
      } else if (options.has(optionParser.CREATE_VAULT)) {
        final GlacierVaultCreator vaultCreator = new GlacierVaultCreator(endpointUrl, credentialFile);
        vaultCreator.createVault(vaultName);
      } else if (options.has(optionParser.DELETE_VAULT)) {
        final GlacierVaultCreator vaultCreator = new GlacierVaultCreator(endpointUrl, credentialFile);
        vaultCreator.deleteVault(vaultName);
      } else if (options.has(optionParser.CALCULATE_HASH)) {
        System.out.println(TreeHashCalculator.toHex(TreeHashCalculator.computeSHA256TreeHash(options
            .valueOf(optionParser.CALCULATE_HASH))));
      } else {
        log.info("Ooops, can't determine what you want to do. Check your options.");
        try {
          optionParser.printHelpOn(System.err);
        } catch (final IOException e) {
          log.error("Can not print help", e);
        }
      }
    } catch (final IOException e) {
      log.info("Ooops, something is wrong with your setup.");
      log.error("Something is wrong with the system configuration", e);
    } catch (final NoSuchAlgorithmException e) {
      log.info("Ooops, something is wrong with your setup. Can not calculate hashsum.");
      log.error("Something is wrong with the system configuration. Can not calculate hashsum.", e);
    }
  }
}
