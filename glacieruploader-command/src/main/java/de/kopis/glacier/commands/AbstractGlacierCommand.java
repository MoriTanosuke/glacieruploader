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

import org.apache.commons.lang3.Validate;

import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQS;

/**
 * Base class for Glacier commands.
 * 
 */
public abstract class AbstractGlacierCommand extends AbstractCommand {
    protected AWSCredentials credentials = null;
    protected AmazonGlacier client = null;
    protected AmazonSQS sqs = null;
    protected AmazonSNS sns = null;

    public AbstractGlacierCommand(AmazonGlacier client, AmazonSQS sqs, AmazonSNS sns) {
        super();
        Validate.notNull(client);
        Validate.notNull(sqs);
        Validate.notNull(sns);

        this.client = client;
        this.sqs = sqs;
        this.sns = sns;
    }
}