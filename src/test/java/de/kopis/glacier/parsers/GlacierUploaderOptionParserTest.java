package de.kopis.glacier.parsers;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;

import joptsimple.OptionSet;

import org.apache.commons.configuration.SystemConfiguration;
import org.junit.Before;
import org.junit.Test;

public class GlacierUploaderOptionParserTest {

	private GlacierUploaderOptionParser optionsParser;
	private String[] args;

	@Before
	public void setUp() {
		optionsParser = new GlacierUploaderOptionParser(new SystemConfiguration());

		args = new String[] { "--vault", "vaultname", "--endpoint", "file:///endpointurl" };
	}
	
	@Test
	public void acceptsShortcutForVaultOption() {
		final OptionSet optionSet = optionsParser.parse(new String[] { "-v", "vaultname", "--endpoint", "file:///endpointurl" });
		assertTrue("Option 'vault' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				optionSet.has("vault"));
		assertEquals("Value of option 'vault' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				"vaultname", optionSet.valueOf("vault"));
	}

	@Test
	public void hasRequiredVaultOptionWithName() {
		final OptionSet optionSet = optionsParser.parse(args);
		assertTrue("Option 'vault' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				optionSet.has("vault"));
		assertEquals("Value of option 'vault' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				"vaultname", optionSet.valueOf("vault"));
	}

	@Test
	public void hasRequiredEndpointOptionWithUrl() throws MalformedURLException {
		final OptionSet optionSet = optionsParser.parse(args);
		assertTrue("Option 'endpoint' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				optionSet.has("endpoint"));
		assertEquals("Value of option 'endpoint' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				"file:///endpointurl", optionSet.valueOf("endpoint"));
	}

	@Test
	public void hasOptionalCredentialsOptionWithFile() {
		final String[] newArgs = Arrays.copyOf(args, args.length + 2);
		newArgs[newArgs.length - 2] = "--credentials";
		newArgs[newArgs.length - 1] = "/path/to/aws.properties";

		final OptionSet optionSet = optionsParser.parse(newArgs);
		assertTrue("Option 'credentials' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				optionSet.has("credentials"));
		assertEquals("Value of option 'credentials' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				new File("/path/to/aws.properties"), optionSet.valueOf("credentials"));
	}

	@Test
	public void hasActionOptionUpload() {
		final String[] newArgs = Arrays.copyOf(args, args.length + 2);
		newArgs[newArgs.length - 2] = "--upload";
		newArgs[newArgs.length - 1] = "/path/to/file";

		final OptionSet optionSet = optionsParser.parse(newArgs);
		assertTrue("Option 'upload' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				optionSet.has("upload"));
		assertEquals("Value of option 'upload' not found in " + Arrays.deepToString(optionSet.specs().toArray()), new File(
				"/path/to/file"), optionSet.valueOf("upload"));
	}

	@Test
	public void hasActionOptionListInventory() {
		final String[] newArgs = Arrays.copyOf(args, args.length + 1);
		newArgs[newArgs.length - 1] = "--list-inventory";

		final OptionSet optionSet = optionsParser.parse(newArgs);
		assertTrue("Option 'list-inventory' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				optionSet.has("list-inventory"));
	}

	@Test
	public void hasActionOptionListInventoryWithJobId() {
		final String[] newArgs = Arrays.copyOf(args, args.length + 2);
		newArgs[newArgs.length - 2] = "--list-inventory";
		newArgs[newArgs.length - 1] = "inventory-job-id";

		final OptionSet optionSet = optionsParser.parse(newArgs);
		assertTrue("Option 'list-inventory' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				optionSet.has("list-inventory"));
		if (optionSet.hasArgument(optionsParser.INVENTORY_LISTING)) {
			assertEquals("Value of option 'list-inventory' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
					"inventory-job-id", optionSet.valueOf("list-inventory"));
		}
	}

	@Test
	public void hasActionOptionDownload() {
		final String[] newArgs = Arrays.copyOf(args, args.length + 2);
		newArgs[newArgs.length - 2] = "--download";
		newArgs[newArgs.length - 1] = "myarchiveid";

		final OptionSet optionSet = optionsParser.parse(newArgs);
		assertTrue("Option 'download' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				optionSet.has("download"));
		assertEquals("Value of option 'download' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				"myarchiveid", optionSet.valueOf("download"));
	}

	@Test
	public void hasActionOptionCreateVault() {
		final String[] newArgs = Arrays.copyOf(args, args.length + 1);
		newArgs[newArgs.length - 1] = "--create";

		final OptionSet optionSet = optionsParser.parse(newArgs);
		assertTrue("Option 'create' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				optionSet.has("create"));
	}

	@Test
	public void hasActionOptionDeleteArchive() {
		final String[] newArgs = Arrays.copyOf(args, args.length + 2);
		newArgs[newArgs.length - 2] = "--delete";
		newArgs[newArgs.length - 1] = "myarchiveid";

		final OptionSet optionSet = optionsParser.parse(newArgs);
		assertTrue("Option 'delete' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				optionSet.has("delete"));
		assertEquals("Value of option 'delete' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				"myarchiveid", optionSet.valueOf("delete"));
	}

	@Test
	public void hasActionOptionDeleteVault() {
		final String[] newArgs = Arrays.copyOf(args, args.length + 1);
		newArgs[newArgs.length - 1] = "--delete-vault";

		final OptionSet optionSet = optionsParser.parse(newArgs);
		assertTrue("Option 'delete-vault' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				optionSet.has("delete-vault"));
	}

	@Test
	public void hasActionOptionCalculateHash() {
		final String[] newArgs = Arrays.copyOf(args, args.length + 2);
		newArgs[newArgs.length - 2] = "--calculate";
		newArgs[newArgs.length - 1] = "inventorylisting.txt";

		final OptionSet optionSet = optionsParser.parse(newArgs);
		assertTrue("Option 'calculate' not found in " + Arrays.deepToString(optionSet.specs().toArray()),
				optionSet.has("calculate"));
	}
}
