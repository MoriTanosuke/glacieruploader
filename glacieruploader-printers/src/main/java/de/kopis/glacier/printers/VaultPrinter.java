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
        out.println("CreationDate:\t" + output.getCreationDate());
        out.println("LastInventoryDate:\t" + output.getLastInventoryDate());
        out.println("NumberOfArchives:\t" + output.getNumberOfArchives());
        out.println("SizeInBytes:\t\t" + output.getSizeInBytes());
        out.println("VaultARN:\t\t" + output.getVaultARN());
        out.println();
        out.flush();
    }

    public void printVault(final DescribeVaultResult describeVaultResult, final OutputStream o) {
        final PrintWriter out = new PrintWriter(o);
        out.println("CreationDate:\t" + describeVaultResult.getCreationDate());
        out.println("LastInventoryDate:\t" + describeVaultResult.getLastInventoryDate());
        out.println("NumberOfArchives:\t" + describeVaultResult.getNumberOfArchives());
        out.println("SizeInBytes:\t\t" + describeVaultResult.getSizeInBytes());
        out.println("VaultARN:\t\t" + describeVaultResult.getVaultARN());
        out.println("VaultName:\t\t" + describeVaultResult.getVaultName());
        out.flush();
    }
}