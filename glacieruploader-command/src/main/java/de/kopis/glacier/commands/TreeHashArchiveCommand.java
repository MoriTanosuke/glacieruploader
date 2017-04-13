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

import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.TreeHashGenerator;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQS;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

public class TreeHashArchiveCommand extends AbstractCommand {

    public TreeHashArchiveCommand(final AmazonGlacier client, final AmazonSQS sqs, final AmazonSNS sns) {
        super(client, sqs, sns);
    }

    private void calculateTreeHash(File file) {
        if (file.exists()) {
            log.info(TreeHashGenerator.calculateTreeHash(file));
        } else {
            log.error(String.format("File '%s' not found", file.getAbsolutePath()));
        }
    }

    @Override
    public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
        final File file = options.valueOf(optionParser.CALCULATE_HASH);
        this.calculateTreeHash(file);
    }

    @Override
    public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
        return options.has(optionParser.CALCULATE_HASH) && options.hasArgument(optionParser.CALCULATE_HASH);
    }

}
