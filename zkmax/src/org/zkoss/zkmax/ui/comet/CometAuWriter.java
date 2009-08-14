/* CometAuWriter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May  6 17:07:24     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.ui.comet;

import java.util.Iterator;
import java.util.Collection;
import java.io.Writer;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.au.AuWriter;
import org.zkoss.zk.au.AuWriters;
import org.zkoss.zk.au.AuResponse;

/**
 * The AU writer used with {@link CometServerPush}.
 *
 * @author tomyeh
 * @since 3.5.0
 */
/*package*/ class CometAuWriter implements AuWriter {
	private HttpServletResponse _response;
	private Writer _out;

	//AuWriter//
	/** CometAuWriter doesn't support compression.
	 * This method has no effect.
	 * @since 3.6.3
	 */
	public void setCompress(boolean compress) {
	}
	/** Returns au to represent the response channel for AU requests.
	 */
	public String getChannel() {
		return "cm";
	}
	public AuWriter open(Object request, Object response, int timeout)
	throws IOException {
		_response = (HttpServletResponse)response;
		_response.setContentType(AuWriters.CONTENT_TYPE);

		_out = _response.getWriter();
		_out.write(AuWriters.CONTENT_HEAD);
		_out.write("<rs>\n");
		_response.flushBuffer();
		return this;
	}
	/** Closes the connection.
	 * <p>Note: this implementation allows to be closed multiple times.
	 */
	public void close(Object request, Object response)
	throws IOException {
		_out.write("\n</rs>");
		_response.flushBuffer();
	}
	public void writeResponseId(int resId) throws IOException {
		AuWriters.writeResponseId(_out, resId);
	}
	public void write(AuResponse response) throws IOException {
		AuWriters.write(_out, response);
	}
	public void write(Collection responses) throws IOException {
		for (Iterator it = responses.iterator(); it.hasNext();)
			write((AuResponse)it.next());
	}
}
