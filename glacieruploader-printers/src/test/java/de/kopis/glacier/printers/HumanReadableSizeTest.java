package de.kopis.glacier.printers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/*
 * #%L
 * uploader
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 Carsten Ringe
 * Copyright (C) 2013 Deux Huit Huit
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

public class HumanReadableSizeTest {
    @Test
    public void sanitizeMissingSizeIndicator() {
        assertArrayEquals(new String[]{"123456789", "B"}, HumanReadableSize.sanitize("123456789"));
    }

    @Test
    public void sanitizeBytes() {
        assertArrayEquals(new String[]{"123456789", "B"}, HumanReadableSize.sanitize("123456789 B"));
    }

    @Test
    public void sanitizeKilobytes() {
        assertArrayEquals(new String[]{"123456789", "kB"}, HumanReadableSize.sanitize("123456789kB"));
    }

    @Test
    public void sanitizeMegabytes() {
        assertArrayEquals(new String[]{"123456789", "MB"}, HumanReadableSize.sanitize("123456789MB"));
    }

    @Test
    public void sanitizeGigabytes() {
        assertArrayEquals(new String[]{"123456789", "GB"}, HumanReadableSize.sanitize("123456789	 GB"));
    }

    @Test
    public void sanitizeKilTerabytes() {
        assertArrayEquals(new String[]{"123456789", "TB"}, HumanReadableSize.sanitize("123456789TB"));
    }

    @Test
    public void terabyteLargeSizeFailed() {
        assertEquals("68.44TB", HumanReadableSize.parse("75240135239680"));
    }

    @Test
    public void gigabyteLargeSizeFailed() {
        assertEquals("68.44GB", HumanReadableSize.parse("73476694570"));
    }

    @Test
    public void megabyteLargeSizeFailed() {
        assertEquals("68.44MB", HumanReadableSize.parse("71754584"));
    }
}
