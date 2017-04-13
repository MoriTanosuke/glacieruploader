package de.kopis.glacier.printers;

/*-
 * #%L
 * glacieruploader-printers
 * %%
 * Copyright (C) 2012 - 2017 Carsten Ringe
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

import com.amazonaws.services.glacier.model.GlacierJobDescription;

public class JobPrinter {

    public void printJob(GlacierJobDescription job, OutputStream o) {
        final PrintWriter out = new PrintWriter(o);
        out.println("Job ID:\t\t\t\t" + job.getJobId());
        out.println("Creation date:\t\t" + job.getCreationDate());
        if (job.getCompleted()) {
            out.println("Completion date:\t" + job.getCompletionDate());
        }
        out.println("Status:\t\t\t\t" + job.getStatusCode() + (job.getStatusMessage() != null ? " (" + job.getStatusMessage() + ")" : ""));
        out.println();
        out.flush();
    }
}
