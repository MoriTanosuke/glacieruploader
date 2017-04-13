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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
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
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        job.setCompletionDate(dateFormat.format(new Date(0)));
        job.setStatusCode(StatusCode.Succeeded);
        job.setStatusMessage(statusMessage);
        new JobPrinter().printJob(job, out);
        assertEquals("Job ID:\t\t\t\t" + jobId + "\n" +
                "Creation date:\t\tnull\n" +
                "Completion date:\t1970-01-01 00:00:00\n" +
                "Status:\t\t\t\tSucceeded (" + statusMessage + ")\n" +
                "\n", out.toString());
    }

}
