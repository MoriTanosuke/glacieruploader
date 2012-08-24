package de.kopis.glacier;

import java.io.File;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;

public class CommandLineGlacierUploader {

    private static final int ERR_NO_PARAMETERS_PROVIDED = -1;

    public static void main(final String[] args) throws IOException {
        if (args == null || args.length != 4) {
            System.err.println("USAGE");
            System.err
                    .println("\tjava -jar uploader.jar PATH_TO_CREDENTIALFILE PATH_TO_ARCHIVE ENDPOINT_URL VAULTNAME");
            System.err.println();
            System.err.println("\t\tPATH_TO_CREDENTIALFILE\t AWS credential file, has to include these lines:");
            System.err.println("\t\t\taccessKey=YOURAWSKEY");
            System.err.println("\t\t\tsecretKey=YOURSUPERSECRETKEY");
            System.err.println("\t\tPATH_TO_ARCHIVE\t file you want to upload, i.e. mylargearchive.zip");
            System.err
                    .println("\t\tENDPOINT_URL\t AWS region your want to use, i.e. https://glacier.eu-west-1.amazonaws.com");
            System.err.println("\t\tVAULTNAME\t the name of your vault, i.e. myvault");
            System.exit(ERR_NO_PARAMETERS_PROVIDED);
        }

        System.out.println("Using aws.properties as credentials. Your file will be obeyed in a later version...");
        // XXX load given credentials
        final String credentialFile = "/aws.properties";// args[0];
        final File uploadFile = new File(args[1]);
        final String endpoint = args[2]; // "https://glacier.eu-west-1.amazonaws.com"
        final String vaultName = args[3]; // cr_backup

        try {
            final AWSCredentials credentials = new PropertiesCredentials(
                    CommandLineGlacierUploader.class.getResourceAsStream(credentialFile));

            final AmazonGlacierClient client = new AmazonGlacierClient(credentials);
            System.out.println("Using endpoint " + endpoint);
            client.setEndpoint(endpoint);

            System.out.println("Starting upload of " + uploadFile);
            final ArchiveTransferManager atm = new ArchiveTransferManager(client, credentials);
            final String archiveId = atm.upload(vaultName, uploadFile.getName(), uploadFile).getArchiveId();
            System.out.println("Uploaded archive " + archiveId);
        } catch (NullPointerException e) {
            System.err.println("Can not read credentials. Check your file.");
            e.printStackTrace();
        }
    }
}
