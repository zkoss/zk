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
 * <p>To use the writer, {@link #open} must be called first.
 * And, {@link #close} after all responses are written.
 *
 * <p>However there are two ways to write responses.
 * First, you can call {@link #write(AuResponse)}
 * and/or {@link #write(Collection)} to write responses individually.
 * After all responses are written, you
 * then invoke {@link #close} to close the writer.
 *
 * <p>Second, you can call {@link #writeRawContent} to write
 * the raw content of all responses (XML),
 * and then invoke {@link #close} to close the writer.
 * The raw content is retrieved in the previous invocation to
 * {@link #getRawContent}.
 *
 * <p>Note: you can not mix the use of {@link #write} and {@link #writeRawContent},
 * and {@link #writeRawContent} can be called only once.
 *
 * @author tomyeh
 * @since 3.0.1
 * @see AuWriters#setImplementationClass
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
	 *
	 * @param request the request (HttpServletRequest if HTTP)
	 * @param response the response (HttpServletResponse if HTTP)
	 */
	public void close(Object request, Object response)
	throws IOException;

	/** Generates the specified the response to the output.
	 */
	public void write(AuResponse response) throws IOException;
	/** Generates a list of responses to the output.
	 */
	public void write(Collection responses) throws IOException;

	/** Retrieves the raw content of the responses being written
	 * by {@link #write}.
	 *
	 * <p>It is designed to implement the resend mechanism:
	 * <ol>
	 * <li>Call this method after all responses being written
	 * (by {@link #write}).</li>
	 * <li>Call {@link org.zkoss.zk.ui.sys.DesktopCtrl#responseSent}
	 * to store the raw content.</li>
	 * <li>Call {@link org.zkoss.zk.ui.sys.DesktopCtrl#getLastResponse}
	 * to see if it is a resend request, when a new request arrives.
	 * <li>If a resend request, call {@link #writeRawContent} instead
	 * of {@link #write}.
	 * </ol>
	 * @since 3.0.5
	 */
	public String getRawContent();
	/** Writes the responses in the raw format.
	 * Note: it can be called only once and cannot be used with
	 * {@link #write}.
	 *
	 * @param rawContent the raw content is what the client really recieved.
	 * @see #getRawContent
	 * @since 3.0.5
	 */
	public void writeRawContent(String rawContent) throws IOException;
}
