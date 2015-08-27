package de.kopis.glacier;

/*
 * #%L
 * uploader
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 Carsten Ringe
 * Copyright (C) 2013 Deux Huit Huit
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import de.kopis.glacier.commands.*;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import de.kopis.glacier.printers.JsonCommandResultPrinter;
import joptsimple.OptionSet;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

public final class GlacierUploader {

    private static final Log log = LogFactory.getLog(GlacierUploader.class);

    public static void main(String[] args) {
        log.debug("program args: " + Arrays.deepToString(args));
        // Get our options
        final GlacierUploaderOptionParser optionParser = new GlacierUploaderOptionParser(setupConfig());
        final OptionSet options;

        // Parse them
        try {
            options = optionParser.parse(args);
        } catch (Exception e) {
            System.err.println("Something went wrong parsing the arguments");
            return;
        }

        // Launch
        findAndExecCommand(options, optionParser);
    }

    public static CompositeConfiguration setupConfig() {
        final CompositeConfiguration config = new CompositeConfiguration();
        config.addConfiguration(new SystemConfiguration());
        try {
            File configFile = new File(System.getProperty("user.home"), ".glacieruploaderrc");
            addConfigFile(config, configFile);
            File localConfigFile = new File(".", ".glacieruploaderrc");
            addConfigFile(config, localConfigFile);
        } catch (Exception e) {
            log.warn("Can not read configuration", e);
        }
        return config;
    }

    private static void addConfigFile(CompositeConfiguration config, File configFile) throws ConfigurationException, IOException {
        if (configFile.exists() && configFile.canRead()) {
            config.addConfiguration(new PropertiesConfiguration(configFile));
        } else {
            log.warn(String.format("Config file '%s' not found", configFile.getCanonicalPath()));
        }
    }

    private static void findAndExecCommand(OptionSet options, GlacierUploaderOptionParser optionParser) {
        try {

            final File credentials = options.valueOf(optionParser.CREDENTIALS);
            final String string_endpoint = options.valueOf(optionParser.ENDPOINT);
            final String format = options.valueOf(optionParser.FORMAT);

            final URL endpoint = new URL(optionParser.formatEndpointUrl(string_endpoint));

            log.info("Using end point: " + string_endpoint);

            // Add all commands to the factory
            CommandFactory.add(new CreateVaultCommand(endpoint, credentials));
            CommandFactory.add(new ListVaultCommand(endpoint, credentials));
            CommandFactory.add(new DeleteArchiveCommand(endpoint, credentials));
            CommandFactory.add(new DeleteVaultCommand(endpoint, credentials));
            CommandFactory.add(new DownloadArchiveCommand(endpoint, credentials));
            CommandFactory.add(new ReceiveArchivesListCommand(endpoint, credentials));
            CommandFactory.add(new RequestArchivesListCommand(endpoint, credentials));
            CommandFactory.add(new TreeHashArchiveCommand(endpoint, credentials));
            CommandFactory.add(new UploadArchiveCommand(endpoint, credentials));
            CommandFactory.add(new UploadMultipartArchiveCommand(endpoint, credentials));
            CommandFactory.add(new AbortMultipartArchiveUploadCommand(endpoint, credentials));
            // Set default
            CommandFactory.setDefaultCommand(new HelpCommand(endpoint, credentials));

            // Find a valid one
            AbstractCommand command = CommandFactory.get(options, optionParser);

            // Execute it
            Optional<CommandResult> result = command.exec(options, optionParser);
            if (result.isPresent()) {
                // TODO print result using format
                JsonCommandResultPrinter printer;
                switch (format) {
                    default:
                        printer = new JsonCommandResultPrinter();
                        break;
                }
                printer.print(System.out, result.get());
            }

        } catch (final IOException e) {
            log.error("Ooops, something is wrong with the system configuration", e);
        }
    }
}
