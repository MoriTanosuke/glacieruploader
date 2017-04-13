package de.kopis.glacier.commands;

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
