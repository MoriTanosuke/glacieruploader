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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.amazonaws.services.glacier.model.GetJobOutputRequest;
import com.amazonaws.services.glacier.model.GetJobOutputResult;
import com.amazonaws.services.glacier.model.InitiateJobRequest;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import com.amazonaws.services.glacier.model.JobParameters;

public class VaultInventoryLister extends AbstractGlacierCommand {

  public VaultInventoryLister(final File credentials) throws IOException {
    super(credentials);
  }

  public void startInventoryListing(final URL endpointUrl, final String vaultName) {
    System.out.println("Starting inventory listing for vault " + vaultName + "...");
    client.setEndpoint(endpointUrl.toExternalForm());

    final InitiateJobRequest initJobRequest = new InitiateJobRequest().withVaultName(vaultName).withJobParameters(
        new JobParameters().withType("inventory-retrieval"));

    final InitiateJobResult initJobResult = client.initiateJob(initJobRequest);
    final String jobId = initJobResult.getJobId();
    System.out.println("Inventory Job created with ID=" + jobId);

    // TODO wait for job, but it could take about 4 hours says the SDK...
  }

  public void retrieveInventoryListing(final URL endpointUrl, final String vaultName, final String jobId) {
    System.out.println("Retrieving inventory for job id " + jobId + "...");
    client.setEndpoint(endpointUrl.toExternalForm());

    final GetJobOutputRequest jobOutputRequest = new GetJobOutputRequest().withVaultName(vaultName).withJobId(jobId);
    final GetJobOutputResult jobOutputResult = client.getJobOutput(jobOutputRequest);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(jobOutputResult.getBody()));
    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
    } catch (final IOException e) {
      System.err.println("Something went wrong while reading inventory.");
      e.printStackTrace();
    }
  }
}
