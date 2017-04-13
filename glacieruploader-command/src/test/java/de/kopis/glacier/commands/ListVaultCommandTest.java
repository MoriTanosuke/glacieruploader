package de.kopis.glacier.commands;

/*-
 * #%L
 * glacieruploader-command
 * %%
 * Copyright (C) 2012 - 2017 Carsten Ringe
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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.OutputStream;
import java.util.Arrays;

import org.junit.Test;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.ListVaultsRequest;
import com.amazonaws.services.glacier.model.ListVaultsResult;
import de.kopis.glacier.printers.VaultPrinter;
import joptsimple.OptionSet;

public class ListVaultCommandTest extends AbstractCommandTest {
    @Test
    public void exec() throws Exception {
        final String vaultName = "dummyVaultName";

        final ListVaultsResult vaultList = new ListVaultsResult();
        final DescribeVaultOutput describeVaultOutput = new DescribeVaultOutput();
        describeVaultOutput.setVaultName(vaultName);
        vaultList.setVaultList(Arrays.asList(describeVaultOutput));
        expect(client.listVaults(isA(ListVaultsRequest.class))).andReturn(vaultList).times(1);
        replay(client);

        // there is no need to supply the LIST_VAULT option, because we're calling the command directly and it
        // does not take arguments anyway
        final OptionSet options = optionParser.parse();
        new ListVaultCommand(client, sqs, sns, new VaultPrinter() {
            @Override
            public void printVault(final DescribeVaultOutput output, final OutputStream o) {
                assertEquals(vaultName, output.getVaultName());
            }
        }).exec(options, optionParser);

        verify(client);
    }

}
