/* HttpAuWriter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Dec  3 16:18:18     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.http;

import java.util.Collection;
import java.util.Iterator;
import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.web.servlet.http.Https;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuWriter;
import org.zkoss.zk.au.AuWriters;

/**
 * The writer used to write the output back to the client.
 *
 * @author tomyeh
 * @since 3.0.1
 */
public class HttpAuWriter implements AuWriter{
	/** The writer used to generate the output to.
	 */
	protected StringWriter _out;
	private boolean _compress = true;

	public HttpAuWriter() {
	}

	/** Returns whether to compress the output.
	 * @since 3.6.3
	 */
	public boolean isCompress() {
		return _compress;
	}

	//AuWriter//
	public void setCompress(boolean compress) {
		_compress = compress;
	}

	/** Returns au to represent the response channel for AU requests.
	 * @since 3.5.0
	 */
	public String getChannel() {
		return "au";
	}
	/** Opens the connection.
	 *
	 * <p>Default: it creates a StringWriter instance for {@link #_out}
	 * and then generate the XML header.
	 *
	 * <p>This implementation doesn't support the timeout argument.
	 */
	public AuWriter open(Object request, Object response, int timeout)
	throws IOException {
		((HttpServletResponse)response).setContentType(AuWriters.CONTENT_TYPE);
			//Bug 1907640: with Glassfish v1, we cannot change content type
			//in another thread, so we have to do it here

		_out = new StringWriter();
		_out.write(AuWriters.CONTENT_HEAD);
		_out.write("<rs>\n");
		return this;
	}
	/** Closes the connection.
	 *
	 */
	public void close(Object request, Object response)
	throws IOException {
		_out.write("\n</rs>");
		flush((HttpServletRequest)request, (HttpServletResponse)response);
	}
	/** Flushes the bufferred output ({@link #_out}) to the client.
	 * It is called by {@link #close}.
	 */
	protected void flush(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		//Use OutputStream due to Bug 1528592 (Jetty 6)
		byte[] data = _out.toString().getBytes("UTF-8");
		if (_compress && data.length > 200) {
			byte[] bs = Https.gzip(request, response, null, data);
			if (bs != null) data = bs; //yes, browser support compress
		}

		response.setContentType(AuWriters.CONTENT_TYPE);
			//we have to set content-type again. otherwise, tomcat might
			//fail to preserve what is set in open()
		response.setContentLength(data.length);
		response.getOutputStream().write(data);
		response.flushBuffer();
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
