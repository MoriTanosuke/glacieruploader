package de.kopis.glacier;

/*
 * #%L
 * glacieruploader-impl
 * %%
 * Copyright (C) 2012 - 2016 Carsten Ringe
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

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import de.kopis.glacier.commands.*;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public final class GlacierUploader {

    private static final Logger LOG = LoggerFactory.getLogger(GlacierUploader.class);

    private GlacierUploader() {
        // do not instantiate
    }

    public static void main(String[] args) {
        // Get our options
        final GlacierUploaderOptionParser optionParser = new GlacierUploaderOptionParser(setupConfig());
        final OptionSet options;

        // Parse them
        try {
            options = optionParser.parse(args);
        } catch (Exception e) {
            LOG.error("Something went wrong parsing the arguments", e);
            return;
        }

        String region = options.valueOf(optionParser.region);
        // check deprecated, but supported config parameters
        if(StringUtils.isBlank(region) && options.has(optionParser.endpoint)) {
            LOG.warn("Option {} is deprecated, please switch to {}", optionParser.endpoint, optionParser.region);
            String endpointUrl = options.valueOf(optionParser.endpoint);
            region = optionParser.parseEndpointToRegion(endpointUrl);
            LOG.debug("Parsed {} from configured endpoint", region , endpointUrl);
        }

        // check deprecated config parameters
        final List<? extends OptionSpec<? extends Serializable>> specs = Arrays.asList(optionParser.credentials);
        for(OptionSpec spec : specs) {
            if (options.has(spec)) {
                LOG.info("Option {} is deprecated, will be ignored", specs);
            }
        }

        // last sanity check
        if(region == null) {
            LOG.error("Region is not configured.");
        }

        // Launch
        findAndExecCommand(region, options, optionParser);
    }

    public static CompositeConfiguration setupConfig() {
        return setupConfig(new File(System.getProperty("user.home"), ".glacieruploaderrc"));
    }

    public static CompositeConfiguration setupConfig(File configFile) {
        final CompositeConfiguration config = new CompositeConfiguration();
        config.addConfiguration(new SystemConfiguration());
        try {
            if (configFile.exists() && configFile.canRead()) {
                config.addConfiguration(new PropertiesConfiguration(configFile));
            } else {
                LOG.debug("Config file '{}' not found", configFile.getCanonicalPath());
            }
        } catch (Exception e) {
            LOG.warn("Can not read configuration", e);
        }
        return config;
    }

    private static void findAndExecCommand(final String region, OptionSet options, GlacierUploaderOptionParser optionParser) {
        Validate.notNull(region, "region can not be NULL");
        LOG.info("Using region: {}", region);
        final Regions eregion = validateRegion(region);

        final DefaultAWSCredentialsProviderChain credentialsProvider = new DefaultAWSCredentialsProviderChain();
        final AmazonGlacier client = AmazonGlacierClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(eregion)
                .build();
        final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(eregion)
                .build();
        final AmazonSNS sns = AmazonSNSClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(eregion)
                .build();

        // Set default command
        CommandFactory.setDefaultCommand(new HelpCommand(client, sqs, sns));
        CommandFactory.add(CommandFactory.getDefaultCommand());

        // Add all commands to the factory
        CommandFactory.add(new CreateVaultCommand(client, sqs, sns));
        CommandFactory.add(new ListVaultCommand(client, sqs, sns));
        CommandFactory.add(new ListJobsCommand(client, sqs, sns));
        CommandFactory.add(new DeleteArchiveCommand(client, sqs, sns));
        CommandFactory.add(new DeleteVaultCommand(client, sqs, sns));
        CommandFactory.add(new DownloadArchiveCommand(client, sqs, sns));
        CommandFactory.add(new ReceiveArchivesListCommand(client, sqs, sns));
        CommandFactory.add(new RequestArchivesListCommand(client, sqs, sns));
        CommandFactory.add(new TreeHashArchiveCommand(client, sqs, sns));
        CommandFactory.add(new UploadArchiveCommand(client, sqs, sns));
        CommandFactory.add(new UploadMultipartArchiveCommand(client, sqs, sns));
        CommandFactory.add(new AbortMultipartArchiveUploadCommand(client, sqs, sns));

        // Find a valid one
        AbstractCommand command = CommandFactory.get(options, optionParser);

        // Execute it
        command.exec(options, optionParser);
    }

    private static Regions validateRegion(String region) {
        // check if region is actually valid
        Regions eregion = null;
        try {
            eregion = Regions.fromName(region);
        } catch (IllegalArgumentException e) {
            // list all available regions if failing to parse
            LOG.info("Available regions: {}", Arrays.toString(Regions.values()));
            throw e;
        }
        return eregion;
    }
}
