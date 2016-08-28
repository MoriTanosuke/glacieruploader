package de.kopis.glacier.commands;

/*
 * #%L
 * glacieruploader-command
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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.amazonaws.services.glacier.model.GlacierJobDescription;
import com.amazonaws.services.glacier.model.ListJobsRequest;
import com.amazonaws.services.glacier.model.ListJobsResult;
import de.kopis.glacier.parsers.GlacierUploaderOptionParser;
import joptsimple.OptionSet;

/**
 * Lists recents jobs for the current vault.
 */
public class ListJobsCommand extends AbstractCommand {

    public ListJobsCommand(final URL endpoint, final File credentials) throws IOException {
        super(endpoint, credentials);
    }

    @Override
    public void exec(OptionSet options, GlacierUploaderOptionParser optionParser) {
        log.info("Listing jobs...");

        final String vaultName = options.valueOf(optionParser.VAULT);
        final ListJobsRequest req = new ListJobsRequest(vaultName);
        final ListJobsResult jobOutputResult = client.listJobs(req);
        for (GlacierJobDescription job : jobOutputResult.getJobList()) {
            System.out.println("Job ID: " + job.getJobId());
            System.out.println("Creation date: " + job.getCreationDate());
            if (job.getCompleted()) {
                System.out.println("Completion date: " + job.getCompletionDate());
            }
            System.out.println("Status: " + job.getStatusCode() + (job.getStatusMessage() != null ? " (" + job.getStatusMessage() + ")" : ""));
            System.out.println();
        }

        log.info("Done.");
    }

    @Override
    public boolean valid(OptionSet options, GlacierUploaderOptionParser optionParser) {
        return options.has(optionParser.LIST_JOBS) && options.hasArgument(optionParser.LIST_JOBS);
    }
}
