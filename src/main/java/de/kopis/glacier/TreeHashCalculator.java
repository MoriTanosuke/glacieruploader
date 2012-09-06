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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TreeHashCalculator {

  private static final int ONE_MB = 1024 * 1024;

  /**
   * Computes the SHA-256 tree hash for the given file
   * 
   * @param inputFile
   *          a File to compute the SHA-256 tree hash for
   * @return a byte[] containing the SHA-256 tree hash
   * @throws IOException
   *           Thrown if there's an issue reading the input file
   * @throws NoSuchAlgorithmException
   */
  public static byte[] computeSHA256TreeHash(final File inputFile) throws IOException, NoSuchAlgorithmException {

    final byte[][] chunkSHA256Hashes = getChunkSHA256Hashes(inputFile);
    return computeSHA256TreeHash(chunkSHA256Hashes);
  }

  /**
   * Computes a SHA256 checksum for each 1 MB chunk of the input file. This
   * includes the checksum for the last chunk even if it is smaller than 1 MB.
   * 
   * @param file
   *          A file to compute checksums on
   * @return a byte[][] containing the checksums of each 1 MB chunk
   * @throws IOException
   *           Thrown if there's an IOException when reading the file
   * @throws NoSuchAlgorithmException
   *           Thrown if SHA-256 MessageDigest can't be found
   */
  private static byte[][] getChunkSHA256Hashes(final File file) throws IOException, NoSuchAlgorithmException {

    final MessageDigest md = MessageDigest.getInstance("SHA-256");

    long numChunks = file.length() / ONE_MB;
    if (file.length() % ONE_MB > 0) {
      numChunks++;
    }

    if (numChunks == 0) {
      return new byte[][] { md.digest() };
    }

    final byte[][] chunkSHA256Hashes = new byte[(int) numChunks][];
    FileInputStream fileStream = null;

    try {
      fileStream = new FileInputStream(file);
      final byte[] buff = new byte[ONE_MB];

      int bytesRead;
      int idx = 0;

      while ((bytesRead = fileStream.read(buff, 0, ONE_MB)) > 0) {
        md.reset();
        md.update(buff, 0, bytesRead);
        chunkSHA256Hashes[idx++] = md.digest();
      }

      return chunkSHA256Hashes;

    } finally {
      if (fileStream != null) {
        try {
          fileStream.close();
        } catch (final IOException ioe) {
          System.err.printf("Exception while closing %s.\n %s", file.getName(), ioe.getMessage());
        }
      }
    }
  }

  /**
   * Computes the SHA-256 tree hash for the passed array of 1 MB chunk
   * checksums.
   * 
   * This method uses a pair of arrays to iteratively compute the tree hash
   * level by level. Each iteration takes two adjacent elements from the
   * previous level source array, computes the SHA-256 hash on their
   * concatenated value and places the result in the next level's destination
   * array. At the end of an iteration, the destination array becomes the source
   * array for the next level.
   * 
   * @param chunkSHA256Hashes
   *          An array of SHA-256 checksums
   * @return A byte[] containing the SHA-256 tree hash for the input chunks
   * @throws NoSuchAlgorithmException
   *           Thrown if SHA-256 MessageDigest can't be found
   */
  private static byte[] computeSHA256TreeHash(final byte[][] chunkSHA256Hashes) throws NoSuchAlgorithmException {

    final MessageDigest md = MessageDigest.getInstance("SHA-256");

    byte[][] prevLvlHashes = chunkSHA256Hashes;

    while (prevLvlHashes.length > 1) {

      int len = prevLvlHashes.length / 2;
      if (prevLvlHashes.length % 2 != 0) {
        len++;
      }

      final byte[][] currLvlHashes = new byte[len][];

      int j = 0;
      for (int i = 0; i < prevLvlHashes.length; i = i + 2, j++) {

        // If there are at least two elements remaining
        if (prevLvlHashes.length - i > 1) {

          // Calculate a digest of the concatenated nodes
          md.reset();
          md.update(prevLvlHashes[i]);
          md.update(prevLvlHashes[i + 1]);
          currLvlHashes[j] = md.digest();

        } else { // Take care of remaining odd chunk
          currLvlHashes[j] = prevLvlHashes[i];
        }
      }

      prevLvlHashes = currLvlHashes;
    }

    return prevLvlHashes[0];
  }

  /**
   * Returns the hexadecimal representation of the input byte array
   * 
   * @param data
   *          a byte[] to convert to Hex characters
   * @return A String containing Hex characters
   */
  public static String toHex(final byte[] data) {
    final StringBuilder sb = new StringBuilder(data.length * 2);

    for (int i = 0; i < data.length; i++) {
      final String hex = Integer.toHexString(data[i] & 0xFF);

      if (hex.length() == 1) {
        // Append leading zero.
        sb.append("0");
      }
      sb.append(hex);
    }
    return sb.toString().toLowerCase();
  }
}