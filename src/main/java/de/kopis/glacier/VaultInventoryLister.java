package de.kopis.glacier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.amazonaws.services.glacier.model.GetJobOutputRequest;
import com.amazonaws.services.glacier.model.GetJobOutputResult;
import com.amazonaws.services.glacier.model.InitiateJobRequest;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import com.amazonaws.services.glacier.model.JobParameters;

public class VaultInventoryLister extends AbstractGlacierCommand {

  public VaultInventoryLister() throws IOException {
    super();
  }

  public void startInventoryListing(String endpoint, String vaultName) {
    System.out.println("Using endpoint " + endpoint);
    client.setEndpoint(endpoint);

    final InitiateJobRequest initJobRequest = new InitiateJobRequest().withVaultName(vaultName).withJobParameters(
        new JobParameters().withType("inventory-retrieval"));

    final InitiateJobResult initJobResult = client.initiateJob(initJobRequest);
    final String jobId = initJobResult.getJobId();
    System.out.println("Inventory Job created with ID=" + jobId);

    // TODO wait for job, but it could take about 4 hours says the SDK...
  }

  public void retrieveInventoryListing(String endpoint, String vaultName, String jobId) {
    System.out.println("Using endpoint " + endpoint);
    client.setEndpoint(endpoint);
    
    final GetJobOutputRequest jobOutputRequest = new GetJobOutputRequest().withVaultName(vaultName).withJobId(jobId);
    final GetJobOutputResult jobOutputResult = client.getJobOutput(jobOutputRequest);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(jobOutputResult.getBody()));
    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
    } catch (IOException e) {
      System.err.println("Something went wrong while reading inventory.");
      e.printStackTrace();
    }
  }
}
