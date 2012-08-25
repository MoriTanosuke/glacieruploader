package de.kopis.glacier;

import joptsimple.OptionParser;
import joptsimple.OptionSpec;

public class GlacierUploaderOptionParser extends OptionParser {

  public final OptionSpec<String> ENDPOINT;
  public final OptionSpec<String> VAULT;
  public final OptionSpec<String> UPLOAD;
  public final OptionSpec<String> DOWNLOAD;

  public GlacierUploaderOptionParser() {
    super();
    VAULT = accepts("vault", "name of your vault").withRequiredArg();
    ENDPOINT = accepts("endpoint", "URL of the amazon AWS endpoint where your vault is").withRequiredArg();
    UPLOAD = accepts("upload", "start uploading a new archive").withRequiredArg();
    accepts("list-inventory", "retrieve the inventory listing of a vault");
    DOWNLOAD = accepts("download", "download an existing archive").withRequiredArg();
  }

}
