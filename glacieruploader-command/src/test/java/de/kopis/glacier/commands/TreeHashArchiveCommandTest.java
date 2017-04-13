package de.kopis.glacier.commands;

import java.io.File;
import java.util.UUID;

import org.junit.Test;
import joptsimple.OptionSet;

public class TreeHashArchiveCommandTest extends AbstractCommandTest {
    @Test
    public void canCalculateTreeHashForFile() throws Exception {
        final String content = UUID.randomUUID().toString();
        final File file = writeTemporaryFile(content);

        final OptionSet options = optionParser.parse("--calculate", file.getAbsolutePath());
        // TODO how to verify this call? output is only into log
        new TreeHashArchiveCommand(client, sqs, sns).exec(options, optionParser);
    }

}