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
import com.amazonaws.services.glacier.AmazonGlacier;
import com.amazonaws.services.glacier.model.DeleteVaultRequest;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sqs.AmazonSQS;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

public class DeleteVaultCommand extends AbstractCommand {
  public DeleteVaultCommand(AmazonGlacier client, AmazonSQS sqs, AmazonSNS sns) {
    super(client, sqs, sns);
  }

  private void deleteVault(final String vaultName) {
    Validate.notNull(vaultName, "vaultName can not be null");
    log.info("Deleting vault " + vaultName + "...");

    final DeleteVaultRequest deleteVaultRequest = new DeleteVaultRequest(vaultName);
    // TODO check for notifications first?
    client.deleteVault(deleteVaultRequest);
    log.info("Vault " + vaultName + " deleted.");
  }

  @Override
  public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
    String vaultName = options.valueOf(optionParser.VAULT);
    this.deleteVault(vaultName);
  }

  @Override
  public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
    return options.has(optionParser.DELETE_VAULT);
  }
}
