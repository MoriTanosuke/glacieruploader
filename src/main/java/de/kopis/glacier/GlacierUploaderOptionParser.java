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
import java.net.URL;

import joptsimple.OptionParser;
import joptsimple.OptionSpec;

public class GlacierUploaderOptionParser extends OptionParser {

  public final OptionSpec<File> TARGET_FILE;
  public final OptionSpec<URL> ENDPOINT;
  public final OptionSpec<String> VAULT;
  public final OptionSpec<File> UPLOAD;
  public final OptionSpec<String> DOWNLOAD;
  public final OptionSpec<String> INVENTORY_LISTING;
  public final OptionSpec<Void> CREATE_VAULT;
  public final OptionSpec<File> CREDENTIALS;
  public final OptionSpec<Void> DELETE_VAULT;
  public final OptionSpec<File> CALCULATE_HASH;

  public GlacierUploaderOptionParser() {
    super();
    VAULT = accepts("vault", "name of your vault").withRequiredArg().ofType(String.class);
    ENDPOINT = accepts("endpoint", "URL of the amazon AWS endpoint where your vault is").withRequiredArg().ofType(
        URL.class);
    UPLOAD = accepts("upload", "start uploading a new archive").withRequiredArg().ofType(File.class);
    INVENTORY_LISTING = accepts("list-inventory", "retrieve the inventory listing of a vault").withOptionalArg()
        .ofType(String.class);
    DOWNLOAD = accepts("download", "download an existing archive").withRequiredArg().ofType(String.class);
    CREDENTIALS = accepts("credentials", "path to your aws credentials file").withRequiredArg().ofType(File.class)
        .defaultsTo(new File(System.getProperty("user.home") + "/aws.properties"));
    CREATE_VAULT = accepts("create", "creates a new vault");
    DELETE_VAULT = accepts("delete", "deletes an existing vault");
    TARGET_FILE = accepts("target", "filename to store downloaded archive").withRequiredArg().ofType(File.class);
    CALCULATE_HASH = accepts("calculate", "calculate hashsum for a file").withRequiredArg().ofType(File.class);
  }
}
