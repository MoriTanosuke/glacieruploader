package de.kopis.glacier.commands;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import joptsimple.OptionSet;

import de.kopis.glacier.parsers.GlacierUploaderOptionParser;

public class HelpCommand extends AbstractCommand {

	public HelpCommand() throws IOException {
		super(null, null);
	}
	
	public HelpCommand(final URL endpoint, final File credentials) throws IOException {
		super(endpoint, credentials);
	}

	@Override
	public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
		if (!options.has(optionParser.HELP)) {
			log.info("Ooops, can't determine what you want to do. Check your options.");
		}
		try {
			optionParser.printHelpOn(System.out);
		} catch (final IOException e) {
			log.error("Can not print help", e);
		}
	}

	@Override
	public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
		return options.has(optionParser.HELP);
	}
	
}
