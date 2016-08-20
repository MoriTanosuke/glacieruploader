package de.kopis.glacier.commands;

/*
 * #%L
 * glacieruploader-command
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
import java.io.IOException;
import java.net.URL;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;
import org.apache.log4j.Logger;

public abstract class AbstractCommand {
    protected final Logger log;

    protected AWSCredentials credentials = null;
    protected AmazonGlacierClient client = null;
    protected AmazonSQSClient sqs = null;
    protected AmazonSNSClient sns = null;

    public AbstractCommand(final URL endpoint, final File credentials) throws IOException {
        this.log = Logger.getLogger(this.getClass());

        if (credentials != null) {
            this.credentials = new PropertiesCredentials(credentials);
            this.client = new AmazonGlacierClient(this.credentials);
            this.sqs = new AmazonSQSClient(this.credentials);
            this.sns = new AmazonSNSClient(this.credentials);
        }

        if (endpoint != null) {
            this.setEndpoint(endpoint);
        }
    }

    protected void setEndpoint(final URL endpoint) {
        client.setEndpoint(endpoint.toExternalForm());
        // TODO check if this really fixes #13
        sqs.setEndpoint(endpoint.toExternalForm().replaceAll("glacier", "sqs"));
        sns.setEndpoint(endpoint.toExternalForm().replaceAll("glacier", "sns"));
    }

    public abstract void exec(OptionSet options, GlacierUploaderOptionParser optionParser);

    public abstract boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser);

}