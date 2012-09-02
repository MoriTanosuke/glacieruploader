package de.kopis.glacier;

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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class VaultInventoryPrinter {
  private final Log log;

  private String inventory;

  public VaultInventoryPrinter() {
    log = LogFactory.getLog(VaultInventoryPrinter.class);
  }

  public VaultInventoryPrinter(final String inventory) {
    this();
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
    o.println("ARN:\t\t\t" + vaultArn);
    final JSONArray archives = json.getJSONArray("ArchiveList");
    for (int i = 0; i < archives.length(); i++) {
      printArchive(o, (JSONObject) archives.get(i));
    }

    o.flush();
  }

  private void printArchive(final PrintWriter o, final JSONObject archive) throws JSONException {
    o.println("Archive ID:\t\t" + archive.get("ArchiveId"));
    o.println("CreationDate:\t" + archive.get("CreationDate"));
    o.println("Description:\t" + archive.get("ArchiveDescription"));
    o.println("Size:\t\t\t" + printArchiveSize(archive));
    o.println("SHA:\t\t\t" + archive.get("SHA256TreeHash"));
  }

  private String printArchiveSize(final JSONObject archive) throws JSONException {
    final String size = archive.getString("Size");
    final String humanReadableSize = humanReadableSize(size);
    return humanReadableSize;
  }

  private String humanReadableSize(final String size) {
    final double sizeAsDouble = Double.parseDouble(size);
    String humanReadableSize = "";
    if (sizeAsDouble > 1024) {
      humanReadableSize = humanReadableSize(Double.toString(sizeAsDouble / 1024.0));
    } else {
      humanReadableSize = Double.toString(sizeAsDouble);
    }
    return round(humanReadableSize, 2, BigDecimal.ROUND_UP);
  }

  public String round(final String unrounded, final int precision, final int roundingMode) {
    final BigDecimal bd = new BigDecimal(unrounded);
    final BigDecimal rounded = bd.setScale(precision, roundingMode);
    return rounded.toString();
  }
}
