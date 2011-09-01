/* AuWriter.java

	Purpose:
		
	Description:
		
	History:
		Mon Dec  3 16:37:03     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

import java.util.Collection;
import java.io.IOException;

/**
 * Represents a writer that is used to send the output back to the client,
 * when processing {@link AuRequest}.
 *
 * <p>To use the writer, {@link #open} must be called first.
 * And, {@link #close} after all responses are written.
 *
 * @author tomyeh
 * @since 3.0.1
 * @see AuWriters#setImplementationClass
 */
public interface AuWriter {
	/** Sets whether to compress the output with GZIP.
	 * <p>Default: true.
	 * @since 3.6.3
	 */
	public void setCompress(boolean compress);
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
	public void close(Object request, Object response) throws IOException;

	/** Indicates the writing has been completed.
	 * Invokes this method before {@link #close},
	 * if the caller supports the resend mechanism.
	 * The caller usually stores the return value to a desktop by
	 * {@link org.zkoss.zk.ui.sys.DesktopCtrl#responseSent}).
	 * <p>Unlike {@link #close}, this method must be called
	 * in an activated execution.
	 * <p>Once this method is called, the caller shall not invoke
	 * ay other write method. It shall invoke only {@link #close}
	 * to end the writer.
	 * @since 5.0.4
	 */
	public Object complete() throws IOException;
	/** Resend the content of the previous request returned by {@link #complete}.
	 * <p>The content is usually stored to a desktop
	 * by {@link org.zkoss.zk.ui.sys.DesktopCtrl#responseSent},
	 * and retrieved by {@link org.zkoss.zk.ui.sys.DesktopCtrl#getLastResponse}.
	 *
	 * <p>Once this method is called, the caller shall not invoke
	 * ay other write method nor {@link #complete}.
	 * It shall invoke only {@link #close} to end the writer.
	 *
	 * @param prevContent the previous content returned by
	 * {@link #close} of the previous {@link AuWriter}.
	 * @exception IllegalArgumentException if prevContent is null.
	 * @exception IllegalStateException if any of write methods
	 * (such as {@link #write}) is called.
	 * @since 5.0.4
	 */
	public void resend(Object prevContent) throws IOException;

	/** Generates the response ID to the output.
	 * @see org.zkoss.zk.ui.sys.DesktopCtrl#getResponseId
	 * @since 3.5.0
	 */
	public void writeResponseId(int resId) throws IOException;
	/** Generates the specified the response to the output.
	 */
	public void write(AuResponse response) throws IOException;
	/** Generates a list of responses to the output.
	 */
	public void write(Collection responses) throws IOException;
}
