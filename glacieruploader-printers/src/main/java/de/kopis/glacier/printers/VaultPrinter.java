package de.kopis.glacier.printers;

/*
 * #%L
 * glacieruploader-printers
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

import java.io.OutputStream;
import java.io.PrintWriter;

import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.DescribeVaultResult;

public class VaultPrinter {

    public void printVault(final DescribeVaultOutput output, OutputStream o) {
        final PrintWriter out = new PrintWriter(o);
        final String creationDate = output.getCreationDate();
        final String lastInventoryDate = output.getLastInventoryDate();
        final Long numberOfArchives = output.getNumberOfArchives();
        final Long sizeInBytes = output.getSizeInBytes();
        final String vaultARN = output.getVaultARN();
        final String vaultName = output.getVaultName();
        printVault(out, creationDate, lastInventoryDate, numberOfArchives, sizeInBytes, vaultARN, vaultName);
    }

    public void printVault(final DescribeVaultResult output, final OutputStream o) {
        final PrintWriter out = new PrintWriter(o);
        printVault(out, output.getCreationDate(), output.getLastInventoryDate(), output.getNumberOfArchives(), output.getSizeInBytes(), output.getVaultARN(), output.getVaultName());
    }

    private void printVault(final PrintWriter out, final String creationDate, final String lastInventoryDate, final Long numberOfArchives, final Long sizeInBytes, final String vaultARN, final String vaultName) {
        out.println("CreationDate:\t" + creationDate);
        out.println("LastInventoryDate:\t" + lastInventoryDate);
        out.println("NumberOfArchives:\t" + numberOfArchives);
        out.println("SizeInBytes:\t\t" + sizeInBytes);
        out.println("VaultARN:\t\t" + vaultARN);
        out.println("VaultName:\t\t" + vaultName);
        out.flush();
    }
}