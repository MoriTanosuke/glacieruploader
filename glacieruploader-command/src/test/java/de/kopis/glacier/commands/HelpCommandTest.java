package de.kopis.glacier.commands;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import org.junit.Test;
import joptsimple.OptionSet;

public class HelpCommandTest extends AbstractCommandTest {
    @Test
    public void exec() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // no need to specify options
        final OptionSet options = optionParser.parse();
        new HelpCommand(client, sqs, sns, out).exec(options, optionParser);

        ByteArrayOutputStream another = new ByteArrayOutputStream();
        optionParser.printHelpOn(another);

        assertEquals(another.toString(), out.toString());
    }

}