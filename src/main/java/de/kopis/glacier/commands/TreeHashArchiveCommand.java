package de.kopis.glacier.commands;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import joptsimple.OptionSet;

import com.amazonaws.services.glacier.TreeHashGenerator;

import de.kopis.glacier.parsers.GlacierUploaderOptionParser;

public class TreeHashArchiveCommand extends AbstractCommand {

	public TreeHashArchiveCommand(final URL endpoint, final File credentials) throws IOException {
		super(endpoint, credentials);
	}
	
	public void calculateTreeHash(File file) {
		log.info(TreeHashGenerator.calculateTreeHash(file));
	}

	@Override
	public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
		File file = options.valueOf(optionParser.CALCULATE_HASH);
		this.calculateTreeHash(file);
	}

	@Override
	public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
		return options.has(optionParser.CALCULATE_HASH);
	}
	
}
