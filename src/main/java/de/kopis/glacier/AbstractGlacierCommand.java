package de.kopis.glacier;

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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.File;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DescribeVaultRequest;
import com.amazonaws.services.glacier.model.DescribeVaultResult;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;

public abstract class AbstractGlacierCommand {
  protected final AWSCredentials credentials;
  protected final AmazonGlacierClient client;
  protected final AmazonSQSClient sqs;
  protected final AmazonSNSClient sns;

  public AbstractGlacierCommand(final File credentials) throws IOException {
    this.credentials = new PropertiesCredentials(credentials);
    client = new AmazonGlacierClient(this.credentials);
    sqs = new AmazonSQSClient(this.credentials);
    sns = new AmazonSNSClient(this.credentials);

  }

  protected void describeVault(final String vaultName) {
    final DescribeVaultRequest describeVaultRequest = new DescribeVaultRequest().withVaultName(vaultName);
    final DescribeVaultResult describeVaultResult = client.describeVault(describeVaultRequest);

    System.out.println("Describing the vault: " + vaultName);
    System.out.println("CreationDate: " + describeVaultResult.getCreationDate());
    System.out.println("LastInventoryDate: " + describeVaultResult.getLastInventoryDate());
    System.out.println("NumberOfArchives: " + describeVaultResult.getNumberOfArchives());
    System.out.println("SizeInBytes: " + describeVaultResult.getSizeInBytes());
    System.out.println("VaultARN: " + describeVaultResult.getVaultARN());
    System.out.println("VaultName: " + describeVaultResult.getVaultName());
  }

}