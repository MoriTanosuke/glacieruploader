package de.kopis.glacier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DescribeVaultOutput;
import com.amazonaws.services.glacier.model.GetJobOutputRequest;
import com.amazonaws.services.glacier.model.GetJobOutputResult;
import com.amazonaws.services.glacier.model.InitiateJobRequest;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import com.amazonaws.services.glacier.model.JobParameters;
import com.amazonaws.services.glacier.model.ListVaultsRequest;
import com.amazonaws.services.glacier.model.ListVaultsResult;

public class VaultInventoryLister {

  public static void main(final String[] args) throws IOException {
    final AWSCredentials credentials = new PropertiesCredentials(
        CommandLineGlacierUploader.class.getResourceAsStream("/aws.properties"));

    final AmazonGlacierClient client = new AmazonGlacierClient(credentials);
    client.setEndpoint("https://glacier.eu-west-1.amazonaws.com");

    final ListVaultsResult listVaults = client.listVaults(new ListVaultsRequest());
    for (final DescribeVaultOutput vault : listVaults.getVaultList()) {
      System.out.println("Vault " + vault.getVaultName() + ", last inventory: " + vault.getLastInventoryDate());

      if (vault.getLastInventoryDate() != null) {
        final InitiateJobRequest initJobRequest = new InitiateJobRequest().withVaultName(vault.getVaultName())
            .withJobParameters(new JobParameters().withType("inventory-retrieval"));

        final InitiateJobResult initJobResult = client.initiateJob(initJobRequest);
        final String jobId = initJobResult.getJobId();
        System.out.println("Inventory Job ID=" + jobId);

        // TODO wait for job

        final GetJobOutputRequest jobOutputRequest = new GetJobOutputRequest().withVaultName(vault.getVaultName())
            .withJobId(jobId);
        final GetJobOutputResult jobOutputResult = client.getJobOutput(jobOutputRequest);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(jobOutputResult.getBody()));
        String line = null;
        while ((line = reader.readLine()) != null) {
          System.out.println(line);
        }
      } else {
        System.out.println("No inventory yet.");
      }
    }
  }
}
