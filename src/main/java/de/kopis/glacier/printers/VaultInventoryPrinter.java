package de.kopis.glacier.printers;

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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.	If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

public class VaultInventoryPrinter {
	private String inventory;

	public VaultInventoryPrinter() {}

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
		final String size = archive.getString("Size");
		final String humanReadableSize = humanReadableSize(size);
		return size + " (" + humanReadableSize + ")";
	}

	private String humanReadableSize(final String size) throws IllegalArgumentException {
		final String[] sanitizedSize = sanitize(size);
		double sizeAsDouble = 0;
		try {
			// parse as US value, because WTF? java default?
			sizeAsDouble = NumberFormat.getInstance(Locale.US).parse(sanitizedSize[0]).doubleValue();
		} catch (final ParseException e) {
			throw new IllegalArgumentException("Can not parse Number", e);
		}
		String humanReadableSize = "";
		String sizeClass = sanitizedSize[1];
		if (sizeAsDouble > 1024) {
			sizeClass = getLargerSizeClass(sanitizedSize[1]);
			humanReadableSize = humanReadableSize(sizeAsDouble / 1024.0 + " " + sizeClass);
		} else {
			humanReadableSize = round(Double.toString(sizeAsDouble), 2, BigDecimal.ROUND_UP) + sizeClass;
		}
		return humanReadableSize;
	}

	private String getLargerSizeClass(final String oldSizeClass) {
		String newSizeClass = "B";
		if ("B".equals(oldSizeClass)) {
			newSizeClass = "kB";
		} else if ("kB".equals(oldSizeClass)) {
			newSizeClass = "MB";
		} else if ("MB".equals(oldSizeClass)) {
			newSizeClass = "GB";
		} else if ("GB".equals(oldSizeClass)) {
			newSizeClass = "TB";
		} else if ("TB".equals(oldSizeClass)) {
			newSizeClass = "PT";
		}
		return newSizeClass;
	}

	public String[] sanitize(final String size) {
		final Pattern patternClass = Pattern.compile("([0-9.]+)\\s*?([kMGTP]?B)");
		final Matcher m = patternClass.matcher(size);
		String[] s = new String[] { size, "B" };
		if (m.find()) {
			final String pureSize = m.group(1);
			final String sizeClass = m.group(2);

			s = new String[] { pureSize, sizeClass };
		}

		return s;
	}

	private String round(final String unrounded, final int precision, final int roundingMode) {
		final BigDecimal bd = new BigDecimal(unrounded);
		final BigDecimal rounded = bd.setScale(precision, roundingMode);
		return rounded.toString();
	}
}
