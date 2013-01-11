package de.kopis.glacier.commands;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import joptsimple.OptionSet;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.glacier.model.GetJobOutputRequest;
import com.amazonaws.services.glacier.model.GetJobOutputResult;
import com.amazonaws.util.json.JSONException;

import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import de.kopis.glacier.printers.VaultInventoryPrinter;

public class ReceiveArchivesListCommand extends AbstractCommand {

	private final VaultInventoryPrinter printer;

	public ReceiveArchivesListCommand(final URL endpoint, final File credentials) throws IOException {
		super(endpoint, credentials);
		printer = new VaultInventoryPrinter();
	}

	public void retrieveInventoryListing(final String vaultName, final String jobId) {
		log.info("Retrieving inventory for job id " + jobId + "...");

		try {
			final GetJobOutputRequest jobOutputRequest = new GetJobOutputRequest().withVaultName(vaultName).withJobId(jobId);
			final GetJobOutputResult jobOutputResult = client.getJobOutput(jobOutputRequest);
			final BufferedReader reader = new BufferedReader(new InputStreamReader(jobOutputResult.getBody()));
			String content = "";
			String line = null;
			while ((line = reader.readLine()) != null) {
				content += line;
			}
			reader.close();
			// TODO use dependency injection here
			printer.setInventory(content);
			printer.printInventory(System.out);
		} catch (final AmazonClientException e) {
			System.err.println(e.getLocalizedMessage());
		} catch (final JSONException e) {
			System.err.println(e.getLocalizedMessage());
		} catch (final IOException e) {
			System.err.println(e.getLocalizedMessage());
		}
	}

	@Override
	public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
		final String vaultName = options.valueOf(optionParser.VAULT);
		final String jobId = options.valueOf(optionParser.INVENTORY_LISTING);
		this.retrieveInventoryListing(vaultName, jobId);
	}
	
	@Override
	public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
		return options.has(optionParser.INVENTORY_LISTING) && options.hasArgument(optionParser.INVENTORY_LISTING);
	}
}
