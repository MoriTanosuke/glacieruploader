package de.kopis.glacier;

import java.io.File;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;

public abstract class AbstractGlacierCommand {
  protected final AWSCredentials credentials;
  protected final AmazonGlacierClient client;

  public AbstractGlacierCommand(File credentials) throws IOException {
    this.credentials = new PropertiesCredentials(credentials);
    client = new AmazonGlacierClient(this.credentials);
  }

}