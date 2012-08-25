package de.kopis.glacier;

import java.io.File;
import java.net.URL;

import joptsimple.OptionParser;
import joptsimple.OptionSpec;

public class GlacierUploaderOptionParser extends OptionParser {

  public final OptionSpec<URL> ENDPOINT;
  public final OptionSpec<String> VAULT;
  public final OptionSpec<File> UPLOAD;
  public final OptionSpec<String> DOWNLOAD;
  public final OptionSpec<String> INVENTORY_LISTING;
  public final OptionSpec<File> CREDENTIALS;

  public GlacierUploaderOptionParser() {
    super();
    VAULT = accepts("vault", "name of your vault").withRequiredArg().ofType(String.class);
    ENDPOINT = accepts("endpoint", "URL of the amazon AWS endpoint where your vault is").withRequiredArg().ofType(URL.class);
    UPLOAD = accepts("upload", "start uploading a new archive").withRequiredArg().ofType(File.class);
    INVENTORY_LISTING = accepts("list-inventory", "retrieve the inventory listing of a vault").withOptionalArg().ofType(String.class);
    DOWNLOAD = accepts("download", "download an existing archive").withRequiredArg().ofType(String.class);
    CREDENTIALS = accepts("credentials", "path to your aws credentials file").withRequiredArg().ofType(File.class).defaultsTo(new File(System.getProperty("user.home") + "/aws.properties"));
  }

}
