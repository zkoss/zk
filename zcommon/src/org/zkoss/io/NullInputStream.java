/* NullInputStream.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jan 24 11:16:26     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.io;

/**
 * An input stream that returns nothing, aka, an empty input stream.
 
 * @author tomyeh
 * @see NullWriter
 * @see NullReader
 */
public class NullInputStream extends java.io.InputStream {
	public int read() {
		return -1;
	}
}
