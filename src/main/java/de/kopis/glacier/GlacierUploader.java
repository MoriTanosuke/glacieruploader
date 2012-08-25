package de.kopis.glacier;

import java.io.IOException;

import joptsimple.OptionSet;

public class GlacierUploader {

  public static void main(final String[] args) {
    final GlacierUploaderOptionParser optionParser = new GlacierUploaderOptionParser();

    final OptionSet options = optionParser.parse(args);

    try {
      if (options.has(optionParser.UPLOAD)) {
        // TODO upload archive
        System.out.println("Starting to upload " + options.valueOf(optionParser.UPLOAD) + "...");
        final CommandLineGlacierUploader glacierUploader = new CommandLineGlacierUploader(options.valueOf(optionParser.CREDENTIALS));
        glacierUploader.upload(options.valueOf(optionParser.ENDPOINT),
            options.valueOf(optionParser.VAULT), options.valueOf(optionParser.UPLOAD));
      } else if (options.has(optionParser.INVENTORY_LISTING)) {
        final VaultInventoryLister vaultInventoryLister = new VaultInventoryLister(options.valueOf(optionParser.CREDENTIALS));
        if (options.hasArgument(optionParser.INVENTORY_LISTING)) {
          System.out.println("Retrieving inventory for job id " + options.valueOf(optionParser.INVENTORY_LISTING)
              + "...");
          vaultInventoryLister.retrieveInventoryListing(options.valueOf(optionParser.ENDPOINT),
              options.valueOf(optionParser.VAULT), options.valueOf(optionParser.INVENTORY_LISTING));
        } else {
          System.out.println("Listing inventory for vault " + options.valueOf(optionParser.VAULT) + "...");
          vaultInventoryLister.startInventoryListing(options.valueOf(optionParser.ENDPOINT),
              options.valueOf(optionParser.VAULT));
        }
      } else if (options.has(optionParser.DOWNLOAD)) {
        // TODO download archive
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
