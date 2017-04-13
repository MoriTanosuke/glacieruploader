package de.kopis.glacier.printers;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import com.amazonaws.services.glacier.model.GlacierJobDescription;
import com.amazonaws.services.glacier.model.StatusCode;

public class JobPrinterTest {
    @Test
    public void printJob() throws Exception {
        final String jobId = UUID.randomUUID().toString();
        final String statusMessage = UUID.randomUUID().toString();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final GlacierJobDescription job = new GlacierJobDescription();
        job.setJobId(jobId);
        job.setCompleted(true);
        job.setCompletionDate(new SimpleDateFormat().format(new Date(0)));
        job.setStatusCode(StatusCode.Succeeded);
        job.setStatusMessage(statusMessage);
        new JobPrinter().printJob(job, out);
        assertEquals("Job ID:\t\t\t\t" + jobId + "\n" +
                "Creation date:\t\tnull\n" +
                "Completion date:\t01.01.70 01:00\n" +
                "Status:\t\t\t\tSucceeded (" + statusMessage + ")\n" +
                "\n", out.toString());
    }

}