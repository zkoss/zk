/* FileWriter.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 19 15:05:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.io;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;

/**
 * Convenience class for writing character files.
 * Unlike java.io.FileWriter, where the default character encoding is used,
 * it accepts different character encoding.
 *
 * @author tomyeh
 * @since 3.0.8
 */
public class FileWriter extends OutputStreamWriter {
	/**
	 * Constructs a FileWriter object given a file name.
	 *
	 * @param filename  String The system-dependent filename.
	 * @param charset the charset to decode the file, such as UTF-8.
	 * If null, UTF-8 is assumed.
	 * @throws IOException  if the named file exists but is a directory
	 * rather than a regular file, does not exist but cannot be created,
	 * or cannot be opened for any other reason
	 */
	public FileWriter(String filename, String charset) throws IOException {
		super(new FileOutputStream(filename), charset != null ? charset: "UTF-8");
	}

	/**
	 * Constructs a FileWriter object given a file name with a boolean
	 * indicating whether or not to append the data written.
	 *
	 * @param filename  String The system-dependent filename.
	 * @param charset the charset to decode the file, such as UTF-8.
	 * If null, UTF-8 is assumed.
	 * @param append boolean if true, then data will be written
	 * to the end of the file rather than the beginning.
	 * @throws IOException  if the named file exists but is a directory
	 * rather than a regular file, does not exist but cannot be created,
	 * or cannot be opened for any other reason
	 */
	public FileWriter(String filename, String charset, boolean append) throws IOException {
		super(new FileOutputStream(filename, append), charset != null ? charset: "UTF-8");
	}

	/**
	 * Constructs a FileWriter object given a File object.
	 *
	 * @param file  a File object to write to.
	 * @param charset the charset to decode the file, such as UTF-8.
	 * If null, UTF-8 is assumed.
	 * @throws IOException  if the named file exists but is a directory
	 * rather than a regular file, does not exist but cannot be created,
	 * or cannot be opened for any other reason
	 */
	public FileWriter(File file, String charset) throws IOException {
		super(new FileOutputStream(file), charset != null ? charset: "UTF-8");
	}

	/**
	 * Constructs a FileWriter object given a File object. If the second
	 * argument is true, then bytes will be written to the end
	 * of the file rather than the beginning.
	 *
	 * @param file  a File object to write to
	 * @param charset the charset to decode the file, such as UTF-8.
	 * If null, UTF-8 is assumed.
	 * @param append if true, then bytes will be written
	 * to the end of the file rather than the beginning
	 * @throws IOException  if the named file exists but is a directory
	 * rather than a regular file, does not exist but cannot be created,
	 * or cannot be opened for any other reason
	 */
	public FileWriter(File file, String charset, boolean append) throws IOException {
		super(new FileOutputStream(file, append), charset != null ? charset: "UTF-8");
	}

	/**
	 * Constructs a FileWriter object associated with a file descriptor.
	 *
	 * @param fd  FileDescriptor object to write to.
	 * @param charset the charset to decode the file, such as UTF-8.
	 * If null, UTF-8 is assumed.
	 */
	public FileWriter(FileDescriptor fd, String charset) throws IOException {
		super(new FileOutputStream(fd), charset != null ? charset: "UTF-8");
	}
}
