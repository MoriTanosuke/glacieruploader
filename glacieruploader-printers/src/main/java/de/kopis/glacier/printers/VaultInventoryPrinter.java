package de.kopis.glacier.printers;

/*
 * #%L
 * glacieruploader-printers
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

import java.io.OutputStream;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VaultInventoryPrinter {
    private String inventory;

    public VaultInventoryPrinter() {
    }

    public VaultInventoryPrinter(final String inventory) {
        this.inventory = inventory;
    }

    public String getInventory() {
        return inventory;
    }

    public void setInventory(final String inventory) {
        this.inventory = inventory;
    }

    public void printInventory(final OutputStream out) throws JSONException {
        final PrintWriter o = new PrintWriter(out);

        final JSONObject json = new JSONObject(inventory);
        final String vaultArn = json.getString("VaultARN");
        o.println("ARN:\t\t\t\t" + vaultArn);
        final JSONArray archives = json.getJSONArray("ArchiveList");
        for (int i = 0; i < archives.length(); i++) {
            printArchive(o, (JSONObject) archives.get(i));
        }

        o.flush();
    }

    private void printArchive(final PrintWriter o, final JSONObject archive) throws JSONException {
        o.println("------------------------------------------------------------------------------");
        o.println("Description:\t\t\t" + archive.get("ArchiveDescription"));
        o.println("Archive ID:\t\t\t" + archive.get("ArchiveId"));
        o.println("CreationDate:\t\t\t" + archive.get("CreationDate"));
        o.println("Size:\t\t\t\t" + printArchiveSize(archive));
        o.println("SHA:\t\t\t\t" + archive.get("SHA256TreeHash"));
    }

    public String printArchiveSize(final JSONObject archive) throws JSONException {
        final int size = archive.getInt("Size");
        final String humanReadableSize = HumanReadableSize.parse(size);
        return size + " (" + humanReadableSize + ")";
    }
}
