/* AuWriter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Dec  3 16:37:03     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

import java.util.Collection;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.au.AuResponse;

/**
 * Represents a writer that is used to send the output back to the client,
 * when processing {@link AuRequest}.
 *
 * @author tomyeh
 * @since 3.0.1
 * @see org.zkoss.zk.ui.util.Configuration#setAuWriter
 */
public interface AuWriter {
	/** Initializes the writer.
	 *
	 * @param timeout the elapsed time (milliseconds) before sending
	 * a whitespace to the client to indicate the connection is alive.
	 * Ignored if non-positive, or the implementation doesn't support
	 * this feature.
	 * @return this object
	 */
	public AuWriter open(HttpServletRequest request, HttpServletResponse response,
	int timeout) throws IOException;
	/** Closes the writer and flush the result to client.
	 */
	public void close(HttpServletRequest request, HttpServletResponse response)
	throws IOException;
	/** Flushes the bufferred output to the client.
	 * It doesn't close the writer.
	 */
	public void flush() throws IOException;

	/** Generates the sequence ID of the specified desktop to the output.
	 * @see org.zkoss.zk.ui.sys.DesktopCtrl#getResponseSequence
	 */
	public void writeSequenceId(Desktop desktop) throws IOException;
	/** Generates the specified the response to the output.
	 */
	public void write(AuResponse response) throws IOException;
	/** Generates a list of responses to the output.
	 */
	public void write(Collection responses) throws IOException;
}
