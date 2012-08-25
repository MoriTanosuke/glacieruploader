package de.kopis.glacier;

import java.io.IOException;

import joptsimple.OptionSet;

public class GlacierUploader {

  public static void main(String[] args) {
    final GlacierUploaderOptionParser optionParser = new GlacierUploaderOptionParser();

    final OptionSet options = optionParser.parse(args);
    if (options.has("upload")) {
      // TODO upload archive
      System.out.println("Starting to upload " + options.valueOf("upload") + "...");
      new CommandLineGlacierUploader().upload(options.valueOf(optionParser.ENDPOINT),
          options.valueOf(optionParser.VAULT), options.valueOf(optionParser.UPLOAD));
    } else if (options.has("list-inventory")) {
      // TODO retrieve inventory listing
    } else if (options.has("download")) {
      // TODO download archive
    } else {
      try {
        System.out.println("Ooops, can't determine what you want to do. Check your options.");
        optionParser.printHelpOn(System.err);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}
