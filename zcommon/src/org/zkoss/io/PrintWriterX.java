/* PrintWriterX.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 23 14:24:08     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.io;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;

/**
 * Print formatted representations of objects to a text-output stream.
 * It is the same as java.io.PrintWriter except it provides
 * an additional method: {@link #getOrigin}.
 *
 * @author tomyeh
 * @since 3.0.6
 */
public class PrintWriterX extends PrintWriter {
	public PrintWriterX(Writer out) {
		super(out);
	}
	public PrintWriterX(Writer out, boolean autoFlush) {
		super(out, autoFlush);
	}
	public PrintWriterX(OutputStream out) {
		super(out);
	}
	public PrintWriterX(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}

	/** Returns the original writer.
	 * If {@link #PrintWriterX(OutputStream)} is used, the returned object
	 * is the encapsulated writer.
	 */
	public Writer getOrigin() {
		return this.out;
	}
}
