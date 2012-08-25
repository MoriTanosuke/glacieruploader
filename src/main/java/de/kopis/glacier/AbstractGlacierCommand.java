package de.kopis.glacier;

import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;

public abstract class AbstractGlacierCommand {
  protected static final String AWS_PROPERTIES = "/aws.properties";
  protected final AWSCredentials credentials;
  protected final AmazonGlacierClient client;

  public AbstractGlacierCommand() throws IOException {
    credentials = new PropertiesCredentials(CommandLineGlacierUploader.class.getResourceAsStream(AWS_PROPERTIES));
    client = new AmazonGlacierClient(credentials);
  }

}