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

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.kopis.glacier.commands.CreateVaultCommand;
import de.kopis.glacier.commands.DeleteArchiveCommand;
import de.kopis.glacier.commands.DeleteVaultCommand;
import de.kopis.glacier.commands.DownloadArchiveCommand;
import de.kopis.glacier.commands.RequestArchivesListCommand;
import de.kopis.glacier.commands.TreeHashArchiveCommand;
import de.kopis.glacier.commands.UploadArchiveCommand;
import de.kopis.glacier.commands.UploadMultipartArchiveCommand;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;

public final class GlacierUploader {
  private static final Log log = LogFactory.getLog(GlacierUploader.class);
  private static CompositeConfiguration config;

  public static void main(String[] args) {
    config = new CompositeConfiguration();
    config.addConfiguration(new SystemConfiguration());
    try {
    	config.addConfiguration(
    		new PropertiesConfiguration(
    			new File(System.getProperty("user.home"), ".glacieruploaderrc")
    		)
    	);
    } catch (ConfigurationException e) {
      log.warn("Can not read configuration", e);
    }

    final GlacierUploaderOptionParser optionParser = new GlacierUploaderOptionParser(config);
    final OptionSet options;
    
    try {
    	options =  optionParser.parse(args);
    } catch (Exception e) {
    	System.err.println("Something went wrong parsing the arguments");
		return;
    }
   

    try {
      final File credentialFile = options.valueOf(optionParser.CREDENTIALS);
      final URL endpointUrl = new URL(options.valueOf(optionParser.ENDPOINT));

      if (options.has(optionParser.UPLOAD)) {
        //final UploadArchiveCommand glacierUploader = new UploadArchiveCommand(endpointUrl, credentialFile);
        //glacierUploader.upload(vaultName, options.valueOf(optionParser.UPLOAD));
      } else if (options.has(optionParser.MULTIPARTUPLOAD)) {
          //final UploadMultipartArchiveCommand glacierUploader = new UploadMultipartArchiveCommand(endpointUrl, credentialFile);
          //glacierUploader.upload(vaultName, options.valueOf(optionParser.MULTIPARTUPLOAD));
      } else if (options.has(optionParser.INVENTORY_LISTING)) {
        /*final ListArchivesCommand vaultInventoryLister = new ListArchivesCommand(endpointUrl, credentialFile);
        if (options.hasArgument(optionParser.INVENTORY_LISTING)) {
          vaultInventoryLister.retrieveInventoryListing(endpointUrl, vaultName,
              options.valueOf(optionParser.INVENTORY_LISTING));
        } else {
          log.info("Listing inventory for vault " + vaultName + "...");
          vaultInventoryLister.startInventoryListing(vaultName);
        }*/
      } else if (options.has(optionParser.DOWNLOAD)) {
        //final DownloadArchiveCommand downloader = new DownloadArchiveCommand(endpointUrl, credentialFile);
        //downloader.download(vaultName, options.valueOf(optionParser.DOWNLOAD), options.valueOf(optionParser.TARGET_FILE));
      } else if (options.has(optionParser.DELETE_ARCHIVE)) {
        //final DeleteArchiveCommand deleter = new DeleteArchiveCommand(endpointUrl, credentialFile);
        //deleter.delete(vaultName, options.valueOf(optionParser.DELETE_ARCHIVE));
      } else if (options.has(optionParser.CREATE_VAULT)) {
        //final CreateVaultCommand vaultCreator = new CreateVaultCommand(endpointUrl, credentialFile);
        //vaultCreator.createVault(vaultName);
      } else if (options.has(optionParser.DELETE_VAULT)) {
        //final DeleteVaultCommand vaultCreator = new DeleteVaultCommand(endpointUrl, credentialFile);
        //vaultCreator.deleteVault(vaultName);
      } else if (options.has(optionParser.CALCULATE_HASH)) {
        //final TreeHashArchiveCommand thg = new TreeHashArchiveCommand(endpointUrl, credentialFile);
        //thg.exec(options, optionParser);
      } else {
        log.info("Ooops, can't determine what you want to do. Check your options.");
        try {
          optionParser.printHelpOn(System.out);
        } catch (final IOException e) {
          log.error("Can not print help", e);
        }
      }
    } catch (final IOException e) {
      log.info("Ooops, something is wrong with your setup.");
      log.error("Something is wrong with the system configuration", e);
    }
  }
}
