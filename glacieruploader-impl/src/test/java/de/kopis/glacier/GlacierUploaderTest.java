package de.kopis.glacier;

import org.junit.Test;

public class GlacierUploaderTest {
    @Test
    public void canRunGlacierUploader() {
        final String[] args = {"--help", "--endpoint", "eu-central-1", "--credentials", "foo.txt"};
        GlacierUploader.main(args);
    }

}