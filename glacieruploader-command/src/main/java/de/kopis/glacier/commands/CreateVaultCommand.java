package de.kopis.glacier.commands;

/*
 * #%L
 * glacieruploader-command
 * %%
 * Copyright (C) 2012 - 2016 Carsten Ringe
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

import org.apache.commons.lang3.Validate;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.model.CreateVaultRequest;
import com.amazonaws.services.glacier.model.CreateVaultResult;
import com.amazonaws.services.glacier.model.DescribeVaultRequest;
import com.amazonaws.services.glacier.model.DescribeVaultResult;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQS;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import de.kopis.glacier.printers.VaultPrinter;
import joptsimple.OptionSet;

public class CreateVaultCommand extends AbstractCommand {
    private VaultPrinter printer;

    public CreateVaultCommand(AmazonGlacier client, AmazonSQS sqs, AmazonSNS sns) {
        this(client, sqs, sns, new VaultPrinter());
    }

    public CreateVaultCommand(final AmazonGlacier client, final AmazonSQS sqs, final AmazonSNS sns, final VaultPrinter printer) {
        super(client, sqs, sns);
        this.printer = printer;
    }

    private void createVault(final String vaultName) {
        Validate.notNull(vaultName, "vaultName can not be null");
        log.info("Creating vault " + vaultName + "...");

        try {
            final CreateVaultRequest createVaultRequest = new CreateVaultRequest(vaultName);
            final CreateVaultResult createVaultResult = client.createVault(createVaultRequest);
            log.info("Vault " + vaultName + " created. " + createVaultResult);
            final DescribeVaultRequest describeVaultRequest = new DescribeVaultRequest().withVaultName(vaultName);
            final DescribeVaultResult describeVaultResult = client.describeVault(describeVaultRequest);
            printer.printVault(describeVaultResult, System.out);
        } catch (final AmazonClientException e) {
            log.error("Couldn't create vault.", e);
        }
    }

    @Override
    public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
        final String vaultName = options.valueOf(optionParser.VAULT);
        this.createVault(vaultName);
    }

    @Override
    public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
        return options.has(optionParser.CREATE_VAULT);
    }
}
