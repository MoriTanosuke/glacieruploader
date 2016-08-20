package de.kopis.glacier.printers;

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

import java.io.ByteArrayOutputStream;

import com.amazonaws.services.glacier.model.DescribeVaultResult;
import org.junit.Test;

public class VaultPrinterTest {

    private static final String VAULT_NAME = "mytestbackup";
    private static final String ARN = "arn:aws:glacier:eu-west-1:968744042024:vaults/mytestbackup";
    private static final Long SIZE_IN_BYTES = 123456789L;
    private static final Long NUMBER_OF_ARCHIVES = 42L;
    private static final String INVENTORY_DATE = "2012-08-29T02:56:35Z";
    private static final String CREATION_DATE = INVENTORY_DATE;

    @Test
    public void testPrintVault() {
        final String linebreak = System.getProperty("line.separator");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final DescribeVaultResult describeVaultResult = new DescribeVaultResult();
        describeVaultResult.setCreationDate(CREATION_DATE);
        describeVaultResult.setLastInventoryDate(INVENTORY_DATE);
        describeVaultResult.setNumberOfArchives(NUMBER_OF_ARCHIVES);
        describeVaultResult.setSizeInBytes(SIZE_IN_BYTES);
        describeVaultResult.setVaultARN(ARN);
        describeVaultResult.setVaultName(VAULT_NAME);
        new VaultPrinter().printVault(describeVaultResult, out);
        assertEquals("CreationDate:\t" + CREATION_DATE + linebreak + "LastInventoryDate:\t" + INVENTORY_DATE + linebreak
                + "NumberOfArchives:\t" + NUMBER_OF_ARCHIVES + linebreak + "SizeInBytes:\t\t" + SIZE_IN_BYTES + linebreak
                + "VaultARN:\t\t" + ARN + linebreak + "VaultName:\t\t" + VAULT_NAME + linebreak, out.toString());
    }
}
