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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HumanReadableSize {
    private static final Logger LOG = LoggerFactory.getLogger(HumanReadableSize.class);

    private HumanReadableSize() {
        // do not instantiate
    }

    public static String parse(final Long size) throws IllegalArgumentException {
        return parse(size.toString());
    }

    public static String parse(final Integer size) throws IllegalArgumentException {
        return parse(size.toString());
    }

    public static String parse(final String size) throws IllegalArgumentException {
        LOG.debug("Parsing '" + size + "'");
        final String[] sanitizedSize = sanitize(size);
        BigDecimal sizeAsNumber = null;
        try {
            // parse as US value, because WTF? java default?
            Number parsed = NumberFormat.getInstance(Locale.US).parse(sanitizedSize[0]);
            sizeAsNumber = new BigDecimal(parsed.toString());
        } catch (final ParseException e) {
            throw new IllegalArgumentException("Can not parse Number", e);
        }
        LOG.debug("Parsed as number: " + sizeAsNumber);
        String humanReadableSize = "";
        String sizeClass = sanitizedSize[1];
        if (sizeAsNumber.longValue() >= 1024L) {
            sizeClass = getLargerSizeClass(sanitizedSize[1]);
            humanReadableSize = parse(sizeAsNumber.divide(new BigDecimal("1024")) + " " + sizeClass);
        } else {
            humanReadableSize = round(new BigDecimal(sizeAsNumber.toString()), 2, BigDecimal.ROUND_UP) + sizeClass;
        }
        LOG.debug("Parsed: " + humanReadableSize);
        return humanReadableSize;
    }

    private static String getLargerSizeClass(final String oldSizeClass) {
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
        LOG.debug("previous sizeClass: " + oldSizeClass + " new sizeClass: " + newSizeClass);
        return newSizeClass;
    }

    public static String[] sanitize(final String size) {
        LOG.debug("Sanitizing '" + size + "'");
        final Pattern patternClass = Pattern.compile("([0-9.]+)\\s*?([kMGTP]?B)");
        final Matcher m = patternClass.matcher(size);
        String[] s = new String[]{size, "B"};
        if (m.find()) {
            final String pureSize = m.group(1);
            final String sizeClass = m.group(2);
            s = new String[]{pureSize, sizeClass};
        }

        LOG.debug("Sanitized: " + Arrays.deepToString(s));
        return s;
    }

    private static String round(final BigDecimal unrounded, final int precision, final int roundingMode) {
        LOG.debug("Rounding '" + unrounded + "' to " + precision + " precision");
        final BigDecimal rounded = unrounded.setScale(precision, roundingMode);
        LOG.debug("Rounded: " + rounded);
        return rounded.toString();
    }
}
