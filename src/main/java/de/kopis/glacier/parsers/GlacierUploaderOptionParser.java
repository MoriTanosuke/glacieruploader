package de.kopis.glacier.parsers;

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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.	If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.File;
import java.util.ArrayList;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;

import org.apache.commons.configuration.Configuration;

public class GlacierUploaderOptionParser extends OptionParser {

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

	public GlacierUploaderOptionParser(Configuration config) {
		super();
		this.VAULT                = this.parseVault(config);
		this.ENDPOINT             = this.parseEndpoint(config);
		this.UPLOAD               = this.parseUploadFile(config);
		this.INVENTORY_LISTING    = this.parseInventory(config);
		this.DOWNLOAD             = this.parseDownload(config);
		this.CREDENTIALS          = this.parseCredentials(config);
		this.CREATE_VAULT         = this.parseCreateVault(config);
		this.DELETE_VAULT         = this.parseDeleteVault(config);
		this.TARGET_FILE          = this.parseTargetFile(config);
		this.CALCULATE_HASH       = this.parseHashFile(config);
		this.DELETE_ARCHIVE       = this.parseDeleteArchive(config);
		this.MULTIPARTUPLOAD      = this.parseMultipartUploadFile(config);
	}
	
	@SuppressWarnings("serial")
	private ArgumentAcceptingOptionSpec<String> parseVault(Configuration config) {
		ArgumentAcceptingOptionSpec<String> vaultBuilder = acceptsAll(
				new ArrayList<String>() {{
					add("vault"); add("v");
				}},
				"name of your vault"
			)
			.withRequiredArg()
			.ofType(String.class);
		if(config.containsKey("vault")) {
			vaultBuilder.defaultsTo(config.getString("vault"));
		}
		return vaultBuilder;
	}
	
	@SuppressWarnings("serial")
	private ArgumentAcceptingOptionSpec<String> parseEndpoint(Configuration config) {
		ArgumentAcceptingOptionSpec<String> endpointBuilder = acceptsAll(
				new ArrayList<String>() {{
					add("endpoint"); add("e");
				}},
				"URL of the amazon AWS endpoint where your vault is"
			)
			.withRequiredArg()
			.ofType(String.class);
		if(config.containsKey("endpoint")) {
			endpointBuilder.defaultsTo(config.getString("endpoint"));
		}
		return endpointBuilder;
	}
	
	@SuppressWarnings("serial")
	private ArgumentAcceptingOptionSpec<File> parseUploadFile(Configuration config) {
		return acceptsAll(
				new ArrayList<String>() {{
					add("upload"); add("u");
				}},
				"start uploading a new archive"
			)
			.withRequiredArg()
			.ofType(File.class);
	}
	
	@SuppressWarnings("serial")
	private ArgumentAcceptingOptionSpec<String> parseInventory(Configuration config) {
		return acceptsAll(
				new ArrayList<String>() {{
					add("list-inventory"); add("l");
				}},
				"retrieve the inventory listing of a vault"
			)
			.withOptionalArg()
			.ofType(String.class);
	}
	
	@SuppressWarnings("serial")
	private ArgumentAcceptingOptionSpec<String> parseDownload(Configuration config) {
		return acceptsAll(
				new ArrayList<String>() {{
					add("download"); add("o");
				}},
				"download an existing archive"
			)
			.withRequiredArg()
			.ofType(String.class);
	}

	@SuppressWarnings("serial")
	private ArgumentAcceptingOptionSpec<File> parseCredentials(Configuration config) {
		ArgumentAcceptingOptionSpec<File> credentialsBuilder = acceptsAll(
				new ArrayList<String>() {{
					add("credentials");
				}},
				"path to your aws credentials file"
			)
			.withRequiredArg()
			.ofType(File.class);
		if(config.containsKey("credentials")) {
			credentialsBuilder.defaultsTo(new File(config.getString("credentials")));
		} else {
			credentialsBuilder.defaultsTo(new File(System.getProperty("user.home") + "/aws.properties"));
		}
		return credentialsBuilder;
	}

	@SuppressWarnings("serial")
	private OptionSpecBuilder parseCreateVault(Configuration config) {
		return acceptsAll(
				new ArrayList<String>() {{
					add("create"); add("c");
				}}, 
				"creates a new vault"
			);
	}
	
	@SuppressWarnings("serial")
	private OptionSpecBuilder parseDeleteVault(Configuration config) {
		return acceptsAll(
				new ArrayList<String>() {{
					add("delete-vault"); add("r");
				}},
				"deletes an existing vault"
			);
	}
	
	@SuppressWarnings("serial")
	private ArgumentAcceptingOptionSpec<File> parseTargetFile(Configuration config) {
		return acceptsAll(
				new ArrayList<String>() {{
					add("target"); add("t");
				}}, 
				"filename to store downloaded archive"
			)
			.withRequiredArg()
			.ofType(File.class);
	}
	
	@SuppressWarnings("serial")
	private ArgumentAcceptingOptionSpec<File> parseHashFile(Configuration config) {
		return acceptsAll(
				new ArrayList<String>() {{
					add("calculate"); add("a");
				}},
				"calculate hashsum for a file"
			)
			.withRequiredArg()
			.ofType(File.class);
	}
	
	@SuppressWarnings("serial")
	private ArgumentAcceptingOptionSpec<String> parseDeleteArchive(Configuration config) {
		return acceptsAll(
				new ArrayList<String>() {{
					add("delete"); add("d");
				}},
				"deletes an existing archive"
			)
			.withOptionalArg()
			.ofType(String.class);
	}
	
	@SuppressWarnings("serial")
	private ArgumentAcceptingOptionSpec<File> parseMultipartUploadFile(Configuration config) {
		return acceptsAll(
				new ArrayList<String>() {{
					add("multipartupload"); add("m");
				}},
				"start uploading a new archive in chuncks"
			)
			.withRequiredArg()
			.ofType(File.class);
	}
	
}
