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

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.au.AuResponse;

/**
 * Represents a writer that is used to send the output back to the client,
 * when processing {@link AuRequest}.
 *
 * @author tomyeh
 * @since 2.4.3
 */
public interface AuWriter {
	/** Initializes the writer.
	 *
	 * @param request the request (HttpServletRequest if HTTP)
	 * @param response the response (HttpServletResponse if HTTP)
	 * @param timeout the elapsed time (milliseconds) before sending
	 * a whitespace to the client to indicate the connection is alive.
	 * Ignored if non-positive, or the implementation doesn't support
	 * this feature.
	 * @return this object
	 */
	public AuWriter open(Object request, Object response, int timeout)
	throws IOException;
	/** Closes the writer and flush the result to client.
	 * @param request the request (HttpServletRequest if HTTP)
	 * @param response the response (HttpServletResponse if HTTP)
	 */
	public void close(Object request, Object response)
	throws IOException;

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
