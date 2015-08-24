package de.kopis.glacier.commands;

/*
 * #%L
 * glacieruploader
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 - 2015 Carsten Ringe
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

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.ListVaultsRequest;
import com.amazonaws.services.glacier.model.ListVaultsResult;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import de.kopis.glacier.printers.CommandResult;
import de.kopis.glacier.printers.VaultPrinter;
import joptsimple.OptionSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class ListVaultCommand extends AbstractCommand {

    public ListVaultCommand(final URL endpoint, final File credentials) throws IOException {
        super(endpoint, credentials);
    }

    public ListVaultCommand(final URL endpoint, AmazonGlacierClient client, AmazonSQSClient sqs, AmazonSNSClient sns) {
        super(endpoint, client, sqs, sns);
    }

    private CommandResult listVaults() {
        log.info("Listing all vaults...");

        CommandResult result = null;
        try {
            final ListVaultsRequest listVaultsRequest = new ListVaultsRequest();
            final ListVaultsResult listVaultsResult = client.listVaults(listVaultsRequest);
            final List<DescribeVaultOutput> vaults = listVaultsResult.getVaultList();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (DescribeVaultOutput vault : vaults) {
                new VaultPrinter().printVault(vault, baos);
            }
            final String description = baos.toString();
            result = new CommandResult(CommandResult.CommandResultStatus.SUCCESS, description);
        } catch (AmazonClientException e) {
            log.error("Can't list vaults.", e);
            result = new CommandResult(CommandResult.CommandResultStatus.FAILURE, "Can not create vault: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Optional<CommandResult> exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
        return Optional.ofNullable(listVaults());
    }

    @Override
    public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
        return options.has(optionParser.LIST_VAULT);
    }
}
