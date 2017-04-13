package de.kopis.glacier.commands;

/*-
 * #%L
 * glacieruploader-command
 * %%
 * Copyright (C) 2012 - 2017 Carsten Ringe
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

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.configuration.CompositeConfiguration;
import org.junit.Before;
import org.junit.Test;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQS;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

public class CommandFactoryTest {

    private AmazonGlacier client;
    private AmazonSQS sqs;
    private AmazonSNS sns;

    @Before
    public void setUp() {
        client = createMock(AmazonGlacier.class);
        sqs = createMock(AmazonSQS.class);
        sns = createMock(AmazonSNS.class);

        CommandFactory.setDefaultCommand(new HelpCommand(client, sqs, sns));
    }

    @Test
    public void canAddCommands() throws Exception {
        CommandFactory.add(new ListVaultCommand(client, sqs, sns));

        final GlacierUploaderOptionParser optionParser = new GlacierUploaderOptionParser(new CompositeConfiguration());
        final OptionSet options = optionParser.parse("--list-vaults");

        assertNotNull(CommandFactory.getDefaultCommand());
        assertNotNull(CommandFactory.get(options, optionParser));
    }

    @Test
    public void returnsDefaultCommandIfNoMatchFound() throws Exception {
        final ListVaultCommand listCommand = new ListVaultCommand(client, sqs, sns);
        CommandFactory.add(listCommand);
        CommandFactory.remove(listCommand);

        final GlacierUploaderOptionParser optionParser = new GlacierUploaderOptionParser(new CompositeConfiguration());
        final OptionSet options = optionParser.parse("--list-vaults");

        final AbstractCommand command = CommandFactory.get(options, optionParser);
        final AbstractCommand defaultCommand = CommandFactory.getDefaultCommand();
        // when command is not found, default command is returned
        assertEquals(defaultCommand, command);
    }
}
