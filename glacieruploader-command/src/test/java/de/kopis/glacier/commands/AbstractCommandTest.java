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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.configuration.CompositeConfiguration;
import org.junit.Before;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQS;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;

public abstract class AbstractCommandTest {
    protected AmazonGlacier client;
    protected AmazonSQS sqs;
    protected AmazonSNS sns;

    // use a dummy configuration
    protected final CompositeConfiguration dummyConfig = new CompositeConfiguration();
    protected final GlacierUploaderOptionParser optionParser = new GlacierUploaderOptionParser(dummyConfig);

    @Before
    public void setUpMocks() {
        client = createMock(AmazonGlacier.class);
        sqs = createMock(AmazonSQS.class);
        sns = createMock(AmazonSNS.class);
    }

    protected static File writeTemporaryFile(final String content) throws IOException {
        final File file = File.createTempFile("junit", ".txt");
        try(FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }
        // make sure we clean up after testing
        file.deleteOnExit();
        return file;
    }

}
