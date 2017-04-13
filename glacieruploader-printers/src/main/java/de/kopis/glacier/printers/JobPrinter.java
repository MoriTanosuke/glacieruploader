package de.kopis.glacier.printers;

import java.io.OutputStream;
import java.io.PrintWriter;

import com.amazonaws.services.glacier.model.GlacierJobDescription;

public class JobPrinter {

    public void printJob(GlacierJobDescription job, OutputStream o) {
        final PrintWriter out = new PrintWriter(o);
        out.println("Job ID:\t" + job.getJobId());
        out.println("Creation date:\t" + job.getCreationDate());
        if (job.getCompleted()) {
            out.println("Completion date:\t" + job.getCompletionDate());
        }
        out.println("Status:\t" + job.getStatusCode() + (job.getStatusMessage() != null ? " (" + job.getStatusMessage() + ")" : ""));
        out.println();
        out.flush();
    }
}
