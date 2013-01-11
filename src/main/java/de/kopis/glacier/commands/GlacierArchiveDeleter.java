package de.kopis.glacier.commands;

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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.amazonaws.services.glacier.model.DeleteArchiveRequest;

public class GlacierArchiveDeleter extends AbstractGlacierCommand {

  public GlacierArchiveDeleter(final URL endpoint, final File credentials) throws IOException {
    super(endpoint, credentials);
  }

  public void delete(final String vaultName, final String archiveId) {
    log.info("Deleting archive " + archiveId + " from vault " + vaultName + "...");

    final DeleteArchiveRequest deleteRequest = new DeleteArchiveRequest(vaultName, archiveId);
    client.deleteArchive(deleteRequest);

    log.info("Archive " + archiveId + " deletion started.");
  }

}
