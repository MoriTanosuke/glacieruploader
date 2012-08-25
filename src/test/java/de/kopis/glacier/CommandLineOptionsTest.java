package de.kopis.glacier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import joptsimple.OptionSet;

import org.junit.Before;
import org.junit.Test;

public class CommandLineOptionsTest {

  private GlacierUploaderOptionParser optionsParser;
  private String[] args;

  @Before
  public void setUp() {
    optionsParser = new GlacierUploaderOptionParser();

    args = new String[] { "--vault", "vaultname", "--endpoint", "file:///endpointurl" };
  }

  @Test
  public void hasRequiredVaultOptionWithName() {
    OptionSet optionSet = optionsParser.parse(args);
    assertTrue("Option 'vault' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
        optionSet.has("vault"));
    assertEquals("Value of option 'vault' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
        "vaultname", optionSet.valueOf("vault"));
  }

  @Test
  public void hasRequiredEndpointOptionWithUrl() throws MalformedURLException {
    OptionSet optionSet = optionsParser.parse(args);
    assertTrue("Option 'endpoint' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
        optionSet.has("endpoint"));
    assertEquals("Value of option 'endpoint' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
        new URL("file:///endpointurl"), optionSet.valueOf("endpoint"));
  }

  @Test
  public void hasOptionalCredentialsOptionWithFile() {
    String[] newArgs = Arrays.copyOf(args, args.length + 2);
    newArgs[newArgs.length - 2] = "--credentials";
    newArgs[newArgs.length - 1] = "/path/to/aws.properties";
    
    OptionSet optionSet = optionsParser.parse(newArgs);
    assertTrue("Option 'credentials' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
        optionSet.has("credentials"));
    assertEquals("Value of option 'credentials' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
        new File("/path/to/aws.properties"), optionSet.valueOf("credentials"));
  }

  @Test
  public void hasActionOptionUpload() {
    String[] newArgs = Arrays.copyOf(args, args.length + 2);
    newArgs[newArgs.length - 2] = "--upload";
    newArgs[newArgs.length - 1] = "/path/to/file";

    OptionSet optionSet = optionsParser.parse(newArgs);
    assertTrue("Option 'upload' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
        optionSet.has("upload"));
    assertEquals("Value of option 'upload' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
        new File("/path/to/file"), optionSet.valueOf("upload"));
  }

  @Test
  public void hasActionOptionListInventory() {
    String[] newArgs = Arrays.copyOf(args, args.length + 1);
    newArgs[newArgs.length - 1] = "--list-inventory";

    OptionSet optionSet = optionsParser.parse(newArgs);
    assertTrue("Option 'list-inventory' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
        optionSet.has("list-inventory"));
  }
  @Test
  public void hasActionOptionListInventoryWithJobId() {
    String[] newArgs = Arrays.copyOf(args, args.length + 2);
    newArgs[newArgs.length - 2] = "--list-inventory";
    newArgs[newArgs.length - 1] = "inventory-job-id";

    OptionSet optionSet = optionsParser.parse(newArgs);
    assertTrue("Option 'list-inventory' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
        optionSet.has("list-inventory"));
    if(optionSet.hasArgument(optionsParser.INVENTORY_LISTING)) {
      assertEquals("Value of option 'list-inventory' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
          "inventory-job-id", optionSet.valueOf("list-inventory"));
    }
  }

  @Test
  public void hasActionOptionDownload() {
    String[] newArgs = Arrays.copyOf(args, args.length + 2);
    newArgs[newArgs.length - 2] = "--download";
    newArgs[newArgs.length - 1] = "myarchiveid";

    OptionSet optionSet = optionsParser.parse(newArgs);
    assertTrue("Option 'download' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
        optionSet.has("download"));
    assertEquals("Value of option 'download' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
        "myarchiveid", optionSet.valueOf("download"));
  }
}
