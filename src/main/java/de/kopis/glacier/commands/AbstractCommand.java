package de.kopis.glacier.commands;

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

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.datamodeling.JsonMarshaller;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public abstract class AbstractCommand {
    protected final Log log;
    protected final Log cliLog = LogFactory.getLog("de.kopis.glacier.commandline");

    protected AmazonGlacierClient client = null;
    protected AmazonSQSClient sqs = null;
    protected AmazonSNSClient sns = null;

    public AbstractCommand(final URL endpoint) {
        log = LogFactory.getLog(getClass());
        if (endpoint != null) {
            this.setEndpoint(endpoint);
        }
    }

    public AbstractCommand(final URL endpoint, final File credentials) throws IOException {
        this(endpoint);
        final PropertiesCredentials creds = new PropertiesCredentials(credentials);
        this.client = new AmazonGlacierClient(creds);
        this.sqs = new AmazonSQSClient(creds);
        this.sns = new AmazonSNSClient(creds);
    }

    public AbstractCommand(final URL endpoint, AmazonGlacierClient client, AmazonSQSClient sqs, AmazonSNSClient sns) {
        this(endpoint);
        this.client = client;
        this.sqs = sqs;
        this.sns = sns;
    }

    protected void setEndpoint(final URL endpoint) {
        client.setEndpoint(endpoint.toExternalForm());
        // TODO check if this really fixes #13
        sqs.setEndpoint(endpoint.toExternalForm().replaceAll("glacier", "sqs"));
        sns.setEndpoint(endpoint.toExternalForm().replaceAll("glacier", "sns"));
    }


    protected <T> String marshall(T initJobResult) {
        return new JsonMarshaller<T>().marshall(initJobResult);
    }

    public abstract Optional<CommandResult> exec(OptionSet options, GlacierUploaderOptionParser optionParser);

    public abstract boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser);

}