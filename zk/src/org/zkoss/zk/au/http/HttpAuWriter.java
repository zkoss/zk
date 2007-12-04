/* HttpHttpAuWriter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Dec  3 16:18:18     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.http;

import java.util.Collection;
import java.util.Iterator;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.web.servlet.http.Https;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.AuWriter;

/**
 * The writer used to write the output back to the client.
 *
 * @author tomyeh
 * @since 3.0.1
 */
public class HttpAuWriter implements AuWriter{
	/** The writer used to generate the output to.
	 */
	protected Writer _out;

	public HttpAuWriter() {
	}

	//AuWriter//
	/** Opens the connection.
	 *
	 * <p>Default: it creates a StringWriter instance for {@link #_out}
	 * and then invoke {@link #writeXMLHead} to generate the XML head.
	 *
	 * <p>This implementation doesn't support the timeout argument.
	 */
	public AuWriter open(Object request, Object response, int timeout)
	throws IOException {
		_out = new StringWriter();
		writeXMLHead();
		return this;
	}
	public void close(Object request, Object response)
	throws IOException {
		writeXMLTail();

		//Use OutputStream due to Bug 1528592 (Jetty 6)
		final HttpServletRequest hreq = (HttpServletRequest)request;
		final HttpServletResponse hres = (HttpServletResponse)response;
		byte[] data = _out.toString().getBytes("UTF-8");
		if (data.length > 200) {
			byte[] bs = Https.gzip(hreq, hres, null, data);
			if (bs != null) data = bs; //yes, browser support compress
		}

		hres.setContentType("text/xml;charset=UTF-8");
		hres.setContentLength(data.length);
		hres.getOutputStream().write(data);
		hres.flushBuffer();
	}

	public void write(AuResponse response) throws IOException {
		_out.write("\n<r><c>");
		_out.write(response.getCommand());
		_out.write("</c>");
		final String[] data = response.getData();
		if (data != null) {
			for (int j = 0; j < data.length; ++j) {
				_out.write("\n<d>");
				encodeXML(data[j], _out);
				_out.write("</d>");
			}
		}
		_out.write("\n</r>");
	}
	public void write(Collection responses) throws IOException {
		for (Iterator it = responses.iterator(); it.hasNext();)
			write((AuResponse)it.next());
	}
	public void writeSequenceId(Desktop desktop) throws IOException {
		_out.write("\n<sid>");
		_out.write(Integer.toString(
			((DesktopCtrl)desktop).getResponseSequence(true)));
		_out.write("</sid>");
	}
	/** Flushes the bufferred output to the client.
	 * <p>Default: does nothing.
	 */
	public void flush() throws IOException {
	}

	//Utilities//
	/** Writes the XML head (&lt;?xml ...?&gt;&lt;rs&gt;).
	 * <p>Don't write anything before calling this method.
	 */
	protected void writeXMLHead() throws IOException {
		_out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rs>\n");
	}
	/** Writes the XML tail (&lt;/rs&gt;).
	 * Don't write anything else to the output if this method is called.
	 */
	protected void writeXMLTail() throws IOException {
		_out.write("\n</rs>");
	}
	private static void encodeXML(String data, Writer out)
	throws IOException {
		if (data == null || data.length() == 0)
			return;

		//20051208: Tom Yeh
		//The following codes are tricky.
		//Reason:
		//1. nested CDATA is not allowed
		//2. Firefox (1.0.7)'s XML parser cannot handle over 4096 chars
		//	if CDATA is not used
		int j = 0;
		for (int k; (k = data.indexOf("]]>", j)) >= 0;) {
			encodeByCData(data.substring(j, k), out);
			out.write("]]&gt;");
			j = k + 3;
		}
		encodeByCData(data.substring(j), out);
	}
	private static void encodeByCData(String data, Writer out)
	throws IOException {
		for (int j = data.length(); --j >= 0;) {
			final char cc = data.charAt(j);
			if (cc == '<' || cc == '>' || cc == '&') {
				out.write("<![CDATA[");
				out.write(data);
				out.write("]]>");
				return;
			}
		}
		out.write(data);
	}
}
