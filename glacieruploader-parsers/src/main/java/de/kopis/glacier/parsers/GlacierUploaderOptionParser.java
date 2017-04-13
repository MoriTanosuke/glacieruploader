package de.kopis.glacier.parsers;

/*
 * #%L
 * glacieruploader-parsers
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;

public class GlacierUploaderOptionParser extends OptionParser {
    private static final Logger log = LoggerFactory.getLogger(GlacierUploaderOptionParser.class);
    private static final Pattern REGION_REGEX_PATTERN = Pattern.compile(".*\\.?(?<region>([a-z]{2,}-){2,}\\d+)\\.?.*");

    public final OptionSpec<Void> LIST_VAULT;
    public final OptionSpec<String> LIST_JOBS;
    public final OptionSpec<File> TARGET_FILE;
    @Deprecated
    public final OptionSpec<String> ENDPOINT;
    public final OptionSpec<String> REGION;
    public final OptionSpec<String> VAULT;
    public final OptionSpec<File> UPLOAD;
    public final OptionSpec<String> DOWNLOAD;
    public final OptionSpec<String> INVENTORY_LISTING;
    public final OptionSpec<Void> CREATE_VAULT;
    @Deprecated
    public final OptionSpec<File> CREDENTIALS;
    public final OptionSpec<String> DELETE_ARCHIVE;
    public final OptionSpec<Void> DELETE_VAULT;
    public final OptionSpec<File> CALCULATE_HASH;
    public final OptionSpec<File> MULTIPARTUPLOAD;
    public final OptionSpec<Long> PARTSIZE;
    public final OptionSpec<Void> HELP;
    public final OptionSpec<String> ABORT_UPLOAD;

    public GlacierUploaderOptionParser(final Configuration config) {
        super();
        this.VAULT = this.parseVault(config);
        this.ENDPOINT = this.parseEndpoint(config);
        this.REGION = this.parseRegion(config);
        this.UPLOAD = this.parseUploadFile(config);
        this.INVENTORY_LISTING = this.parseInventory(config);
        this.DOWNLOAD = this.parseDownload(config);
        this.CREDENTIALS = this.parseCredentials(config);
        this.CREATE_VAULT = this.parseCreateVault(config);
        this.LIST_VAULT = this.parseListVault(config);
        this.LIST_JOBS = this.parseListJobs(config);
        this.DELETE_VAULT = this.parseDeleteVault(config);
        this.TARGET_FILE = this.parseTargetFile(config);
        this.CALCULATE_HASH = this.parseHashFile(config);
        this.DELETE_ARCHIVE = this.parseDeleteArchive(config);
        this.MULTIPARTUPLOAD = this.parseMultipartUploadFile(config);
        this.PARTSIZE = this.parsePartSize(config);
        this.HELP = this.parseHelp(config);
        this.ABORT_UPLOAD = this.parseAbortUpload(config);
    }

    public String parseEndpointToRegion(String endpointOptionValue) {
        String region = endpointOptionValue;

        final Matcher matcher = REGION_REGEX_PATTERN.matcher(endpointOptionValue);
        if(matcher.matches()) {
            region = matcher.group("region");
            log.debug("Endpoint parsed: " + region);
        }

        return region;
    }

    public List<File> mergeNonOptionsFiles(List<File> optionsFiles, List<String> nonOptions) {
        final List<File> files = new ArrayList<>(optionsFiles);

        if (!nonOptions.isEmpty()) {
            // Adds non options to the list in order
            // to be able to use * in filenames
            for (String nonOption : nonOptions) {
                File file = new File(nonOption);
                if (file.exists() && file.isFile()) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    private ArgumentAcceptingOptionSpec<String> parseVault(final Configuration config) {
        ArgumentAcceptingOptionSpec<String> vaultBuilder = acceptsAll(Arrays.asList("vault", "v"),
                "name of your vault")
                .withRequiredArg()
                .ofType(String.class);

        if (config.containsKey("vault")) {
            vaultBuilder.defaultsTo(config.getString("vault"));
        }
        return vaultBuilder;
    }

    @Deprecated
    private ArgumentAcceptingOptionSpec<String> parseEndpoint(final Configuration config) {
        ArgumentAcceptingOptionSpec<String> endpointBuilder = acceptsAll(Arrays.asList("endpoint", "e"),
                "URL or Region handle of the amazon AWS endpoint where your vault is")
                .withRequiredArg()
                .ofType(String.class);

        if (config.containsKey("endpoint")) {
            endpointBuilder.defaultsTo(parseEndpointToRegion(config.getString("endpoint")));
        }
        return endpointBuilder;
    }

    private ArgumentAcceptingOptionSpec<String> parseRegion(final Configuration config) {
        ArgumentAcceptingOptionSpec<String> endpointBuilder = acceptsAll(Arrays.asList("region", "g"),
                "Region handle of the amazon AWS endpoint where your vault is")
                .withRequiredArg()
                .ofType(String.class);

        if (config.containsKey("region")) {
            endpointBuilder.defaultsTo(config.getString("region"));
        }
        return endpointBuilder;
    }

    private ArgumentAcceptingOptionSpec<File> parseUploadFile(final Configuration config) {
        return acceptsAll(Arrays.asList("upload", "u"),
                "start uploading a new archive")
                .withRequiredArg()
                .ofType(File.class);
    }

    private ArgumentAcceptingOptionSpec<String> parseInventory(final Configuration config) {
        return acceptsAll(Arrays.asList("list-inventory", "l"),
                "retrieve the inventory listing of a vault or request a listing if no job id is set")
                .withOptionalArg()
                .ofType(String.class);
    }

    private ArgumentAcceptingOptionSpec<String> parseDownload(final Configuration config) {
        return acceptsAll(Arrays.asList("download", "o"),
                "download an existing archive")
                .withRequiredArg()
                .ofType(String.class);
    }

    @Deprecated
    private ArgumentAcceptingOptionSpec<File> parseCredentials(final Configuration config) {
        ArgumentAcceptingOptionSpec<File> credentialsBuilder = acceptsAll(Arrays.asList("credentials"),
                "path to your aws credentials file").withRequiredArg().ofType(File.class);

        if (config.containsKey("credentials")) {
            credentialsBuilder.defaultsTo(new File(config.getString("credentials")));
        } else {
            credentialsBuilder.defaultsTo(new File(System.getProperty("user.home") + "/aws.properties"));
        }
        return credentialsBuilder;
    }

    private OptionSpecBuilder parseCreateVault(final Configuration config) {
        return acceptsAll(Arrays.asList("create", "c"),
                "creates a new vault");
    }

    private OptionSpecBuilder parseListVault(final Configuration config) {
        return acceptsAll(Arrays.asList("list-vaults", "s"),
                "lists all available vaults");
    }

    private ArgumentAcceptingOptionSpec<String> parseListJobs(final Configuration config) {
        return acceptsAll(Arrays.asList("list-jobs", "j"),
                "lists recent jobs")
                .withRequiredArg()
                .ofType(String.class);
    }

    private OptionSpecBuilder parseDeleteVault(final Configuration config) {
        return acceptsAll(Arrays.asList("delete-vault", "r"),
                "deletes an existing vault");
    }

    private ArgumentAcceptingOptionSpec<File> parseTargetFile(final Configuration config) {
        return acceptsAll(Arrays.asList("target", "t"),
                "filename to store downloaded archive")
                .withRequiredArg()
                .ofType(File.class);
    }

    private ArgumentAcceptingOptionSpec<File> parseHashFile(final Configuration config) {
        return acceptsAll(Arrays.asList("calculate", "a"),
                "calculate hashsum for a file")
                .withRequiredArg()
                .ofType(File.class);
    }

    private ArgumentAcceptingOptionSpec<String> parseDeleteArchive(final Configuration config) {
        return acceptsAll(Arrays.asList("delete", "d"),
                "deletes an existing archive")
                .withRequiredArg()
                .ofType(String.class);
    }

    private ArgumentAcceptingOptionSpec<File> parseMultipartUploadFile(final Configuration config) {
        return acceptsAll(Arrays.asList("multipartupload", "m"),
                "start uploading a new archive in chuncks")
                .withRequiredArg()
                .ofType(File.class);
    }

    private ArgumentAcceptingOptionSpec<Long> parsePartSize(final Configuration config) {
        return acceptsAll(Arrays.asList("partsize", "p"),
                "sets the size of each part for multipart uploads (must be a power of 2)")
                .withRequiredArg()
                .ofType(Long.class)
                .defaultsTo((long) Math.pow(4096, 2));
        // 16 MB.
    }

    private OptionSpecBuilder parseHelp(final Configuration config) {
        return acceptsAll(Arrays.asList("help", "h", "?"),
                "display the help menu");
    }

    private ArgumentAcceptingOptionSpec<String> parseAbortUpload(final Configuration config) {
        return acceptsAll(Arrays.asList("abort-upload", "x"),
                "aborts an existing upload request - requires upload id")
                .withRequiredArg()
                .ofType(String.class);
    }
}
