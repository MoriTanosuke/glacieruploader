package de.kopis.glacier;

import java.io.File;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;

public class CommandLineGlacierUploader {

	public static void main(final String[] args) throws IOException {
		final AWSCredentials credentials = new PropertiesCredentials(
				CommandLineGlacierUploader.class.getResourceAsStream("/aws.properties"));
		final AmazonGlacierClient client = new AmazonGlacierClient(credentials);
		client.setEndpoint("https://glacier.eu-west-1.amazonaws.com");    
		final ArchiveTransferManager atm = new ArchiveTransferManager(client, credentials);
		final File uploadFile = new File(args[0]);
		final String archiveId = atm.upload("cr_backup", "archive-" + uploadFile.getName(),
				uploadFile).getArchiveId();
		System.out.println("Uploaded archive " + archiveId);
	}
}
