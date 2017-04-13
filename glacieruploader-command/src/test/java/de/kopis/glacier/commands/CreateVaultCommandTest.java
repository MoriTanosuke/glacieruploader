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

import org.junit.Test;
import com.amazonaws.services.glacier.model.CreateVaultRequest;
import com.amazonaws.services.glacier.model.CreateVaultResult;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.DescribeVaultRequest;
import com.amazonaws.services.glacier.model.DescribeVaultResult;
import de.kopis.glacier.printers.VaultPrinter;
import joptsimple.OptionSet;

public class CreateVaultCommandTest extends AbstractCommandTest {
    @Test
    public void exec() throws Exception {
        final CreateVaultResult createVaultResult = new CreateVaultResult();
        createVaultResult.setLocation("dummyLocation");
        expect(client.createVault(isA(CreateVaultRequest.class))).andReturn(createVaultResult).times(1);
        //TODO check against provided createVaultResult
        expect(client.describeVault(isA(DescribeVaultRequest.class))).andReturn(new DescribeVaultResult()).times(1);
        replay(client);

        final String vaultName = "dummyVaultName";
        final OptionSet options = optionParser.parse("--vault", vaultName);
        new CreateVaultCommand(client, sqs, sns, new VaultPrinter() {
            @Override
            public void printVault(final DescribeVaultOutput output, final OutputStream o) {
                assertEquals(vaultName, output.getVaultName());
            }}).exec(options, optionParser);

        verify(client);
    }

}
