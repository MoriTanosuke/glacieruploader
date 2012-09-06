package de.kopis.glacier.util;

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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import de.kopis.glacier.util.TreeHashCalculator;

public class TreeHashCalculatorTest {

  @Test
  public void testComputeSHA256TreeHashFile() throws NoSuchAlgorithmException, IOException {
    final byte[] hash = TreeHashCalculator.computeSHA256TreeHash(new File("inventorylisting.txt"));
    assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", TreeHashCalculator.toHex(hash));
  }

  @Test
  public void testToHex() {
    assertEquals("313233343536373839", TreeHashCalculator.toHex("123456789".getBytes()));
  }

}
