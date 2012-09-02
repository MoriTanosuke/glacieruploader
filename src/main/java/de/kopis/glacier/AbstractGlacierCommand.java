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
import java.io.IOException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;

public abstract class AbstractGlacierCommand {
  protected final Log log;

  protected final AWSCredentials credentials;
  protected final AmazonGlacierClient client;
  protected final AmazonSQSClient sqs;
  protected final AmazonSNSClient sns;

  public AbstractGlacierCommand(final URL endpoint, final File credentials) throws IOException {
    this.log = LogFactory.getLog(this.getClass());

    this.credentials = new PropertiesCredentials(credentials);
    client = new AmazonGlacierClient(this.credentials);
    sqs = new AmazonSQSClient(this.credentials);
    sns = new AmazonSNSClient(this.credentials);

    setEndpoint(endpoint);
  }

  protected void setEndpoint(final URL endpoint) {
    log.info("Using endpoint " + endpoint);

    client.setEndpoint(endpoint.toExternalForm());
    // TODO check if this really fixes #13
    sqs.setEndpoint(endpoint.toExternalForm().replaceAll("glacier", "sqs"));
    sns.setEndpoint(endpoint.toExternalForm().replaceAll("glacier", "sns"));
  }

}