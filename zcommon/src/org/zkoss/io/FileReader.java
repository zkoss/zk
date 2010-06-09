/* FileReader.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 19 15:01:16     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileDescriptor;

/**
 * Convenience class for reading character files.
 * Unlike java.io.FileReader, where the default character encoding is used,
 * it accepts different character encoding.
 *
 * @author tomyeh
 * @since 3.0.8
 */
public class FileReader extends InputStreamReader {
   /**
	* Creates a new FileReader, given the name of the file to read from.
	*
	* @param filename the name of the file to read from
	* @param charset the charset to decode the file, such as UTF-8.
	* If null, UTF-8 is assumed.
	* @exception FileNotFoundException  if the named file does not exist,
	* is a directory rather than a regular file,
	* or for some other reason cannot be opened for reading.
	*/
	public FileReader(String filename, String charset)
	throws IOException {
		super(new FileInputStream(filename), charset != null ? charset: "UTF-8");
	}

   /**
	* Creates a new FileReader, given the File instance to read from.
	*
	* @param file the File to read from
	* @param charset the charset to decode the file, such as UTF-8.
	* If null, UTF-8 is assumed.
	* @exception FileNotFoundException  if the named file does not exist,
	* is a directory rather than a regular file,
	* or for some other reason cannot be opened for reading.
	*/
	public FileReader(File file, String charset) throws IOException {
		super(new FileInputStream(file), charset != null ? charset: "UTF-8");
	}

   /**
	* Creates a new FileReader, given the 
	* FileDescriptor to read from.
	*
	* @param fd the FileDescriptor to read from
	* @param charset the charset to decode the file, such as UTF-8.
	* If null, UTF-8 is assumed.
	*/
	public FileReader(FileDescriptor fd, String charset)
	throws IOException {
		super(new FileInputStream(fd), charset != null ? charset: "UTF-8");
	}
}
