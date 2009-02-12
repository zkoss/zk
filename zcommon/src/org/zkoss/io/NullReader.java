/* NullReader.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jan 24 11:15:08     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.io;

/**
 * A reader that returns nothing, aka, an empty reader.
 *
 * @author tomyeh
 * @see NullWriter
 * @see NullInputStream
 */
public class NullReader extends java.io.Reader {
	public int read(char[] cbuf, int off, int len) {
		return -1;
	}
	public void close() {
	}
}
