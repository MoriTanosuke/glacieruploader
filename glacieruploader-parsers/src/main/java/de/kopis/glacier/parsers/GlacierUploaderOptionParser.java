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
import java.util.List;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import org.apache.commons.configuration.Configuration;

public class GlacierUploaderOptionParser extends OptionParser {

    public final OptionSpec<Void> LIST_VAULT;
    public final OptionSpec<String> LIST_JOBS;
    public final OptionSpec<File> TARGET_FILE;
    public final OptionSpec<String> ENDPOINT;
    public final OptionSpec<String> VAULT;
    public final OptionSpec<File> UPLOAD;
    public final OptionSpec<String> DOWNLOAD;
    public final OptionSpec<String> INVENTORY_LISTING;
    public final OptionSpec<Void> CREATE_VAULT;
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

    public String formatEndpointUrl(String endpoint) {
        if (endpoint == null) {
            return null;
        }
        if (endpoint.startsWith("https://")) {
            return endpoint;
        }
        return String.format("https://glacier.%s.amazonaws.com", endpoint);
    }

    public ArrayList<File> mergeNonOptionsFiles(List<File> optionsFiles, List<String> nonOptions) {
        final ArrayList<File> files = new ArrayList<File>(optionsFiles);

        if (nonOptions.size() > 0) {
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
        ArgumentAcceptingOptionSpec<String> vaultBuilder = acceptsAll(new ArrayList<String>() {
            {
                add("vault");
                add("v");
            }
        }, "name of your vault").withRequiredArg().ofType(String.class);

        if (config.containsKey("vault")) {
            vaultBuilder.defaultsTo(config.getString("vault"));
        }
        return vaultBuilder;
    }

    private ArgumentAcceptingOptionSpec<String> parseEndpoint(final Configuration config) {
        ArgumentAcceptingOptionSpec<String> endpointBuilder = acceptsAll(new ArrayList<String>() {
            {
                add("endpoint");
                add("e");
            }
        }, "URL or Region handle of the amazon AWS endpoint where your vault is").withRequiredArg().ofType(String.class);

        if (config.containsKey("endpoint")) {
            endpointBuilder.defaultsTo(formatEndpointUrl(config.getString("endpoint")));
        }
        return endpointBuilder;
    }

    private ArgumentAcceptingOptionSpec<File> parseUploadFile(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("upload");
                add("u");
            }
        }, "start uploading a new archive").withRequiredArg().ofType(File.class);
    }

    private ArgumentAcceptingOptionSpec<String> parseInventory(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("list-inventory");
                add("l");
            }
        }, "retrieve the inventory listing of a vault or request a listing if no job id is set").withOptionalArg().ofType(String.class);
    }

    private ArgumentAcceptingOptionSpec<String> parseDownload(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("download");
                add("o");
            }
        }, "download an existing archive").withRequiredArg().ofType(String.class);
    }

    private ArgumentAcceptingOptionSpec<File> parseCredentials(final Configuration config) {
        ArgumentAcceptingOptionSpec<File> credentialsBuilder = acceptsAll(new ArrayList<String>() {
            {
                add("credentials");
            }
        }, "path to your aws credentials file").withRequiredArg().ofType(File.class);

        if (config.containsKey("credentials")) {
            credentialsBuilder.defaultsTo(new File(config.getString("credentials")));
        } else {
            credentialsBuilder.defaultsTo(new File(System.getProperty("user.home") + "/aws.properties"));
        }
        return credentialsBuilder;
    }

    private OptionSpecBuilder parseCreateVault(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("create");
                add("c");
            }
        }, "creates a new vault");
    }

    private OptionSpecBuilder parseListVault(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("list-vaults");
                add("s");
            }
        }, "lists all available vaults");
    }

    private ArgumentAcceptingOptionSpec<String> parseListJobs(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("list-jobs");
                add("j");
            }
        }, "lists recent jobs").withRequiredArg().ofType(String.class);
    }

    private OptionSpecBuilder parseDeleteVault(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("delete-vault");
                add("r");
            }
        }, "deletes an existing vault");
    }

    private ArgumentAcceptingOptionSpec<File> parseTargetFile(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("target");
                add("t");
            }
        }, "filename to store downloaded archive").withRequiredArg().ofType(File.class);
    }

    private ArgumentAcceptingOptionSpec<File> parseHashFile(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("calculate");
                add("a");
            }
        }, "calculate hashsum for a file").withRequiredArg().ofType(File.class);
    }

    private ArgumentAcceptingOptionSpec<String> parseDeleteArchive(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("delete");
                add("d");
            }
        }, "deletes an existing archive").withRequiredArg().ofType(String.class);
    }

    private ArgumentAcceptingOptionSpec<File> parseMultipartUploadFile(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("multipartupload");
                add("m");
            }
        }, "start uploading a new archive in chuncks").withRequiredArg().ofType(File.class);
    }

    private ArgumentAcceptingOptionSpec<Long> parsePartSize(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("partsize");
                add("p");
            }
        }, "sets the size of each part for multipart uploads (must be a power of 2)").withRequiredArg().ofType(Long.class).defaultsTo((long) Math.pow(4096, 2));
        // 16 MB.
    }

    private OptionSpecBuilder parseHelp(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("help");
                add("h");
                add("?");
            }
        }, "display the help menu");
    }

    private ArgumentAcceptingOptionSpec<String> parseAbortUpload(final Configuration config) {
        return acceptsAll(new ArrayList<String>() {
            {
                add("abort-upload");
                add("x");
            }
        }, "aborts an existing upload request - requires upload id").withRequiredArg().ofType(String.class);
    }
}
