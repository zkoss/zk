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
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.idom.Verifier;
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
	/** The first few characters of the output content. */
	protected static final String OUTPUT_HEAD =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	/** The content type of the output. */
	private static final String CONTENT_TYPE = "text/xml;charset=UTF-8";

	/** The writer used to generate the output to.
	 */
	protected StringWriter _out;

	public HttpAuWriter() {
	}

	//AuWriter//
	/** Opens the connection.
	 *
	 * <p>Default: it creates a StringWriter instance for {@link #_out}
	 * and then generate the XML header.
	 *
	 * <p>This implementation doesn't support the timeout argument.
	 */
	public AuWriter open(Object request, Object response, int timeout)
	throws IOException {
		((HttpServletResponse)response).setContentType(CONTENT_TYPE);
			//Bug 1907640: with Glassfish v1, we cannot change content type
			//in another thread, so we have to do it here

		_out = new StringWriter();
		_out.write(OUTPUT_HEAD);
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
		if (data.length > 200) {
			byte[] bs = Https.gzip(request, response, null, data);
			if (bs != null) data = bs; //yes, browser support compress
		}

		response.setContentType(CONTENT_TYPE);
			//we have to set content-type again. otherwise, tomcat might
			//fail to preserve what is set in open()
		response.setContentLength(data.length);
		response.getOutputStream().write(data);
		response.flushBuffer();
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

	//Utilities//
	private static void encodeXML(String data, Writer out)
	throws IOException {
		if (data == null || data.length() == 0)
			return;

		//20051208: Tom Yeh
		//The following codes are tricky.
		//Reason:
		//1. Nested CDATA is not allowed
		//2. Illegal character must be encoded
		//3. Firefox (1.0.7)'s XML parser cannot handle over 4096 chars
		//	if CDATA is not used
		int k = 0, len = data.length();
		for (int j = 0; j < len;) {
			final char cc = data.charAt(j);
			if (cc == ']') {
				if (j + 2 < len && data.charAt(j + 1) == ']'
				&& data.charAt(j + 2) == '>') { //]]>
					encodeByCData(data.substring(k, j + 2), out);
					out.write("&gt;");
					k = j += 3;
					continue;
				}
			} else if (!Verifier.isXMLCharacter(cc)) {
				encodeByCData(data.substring(k, j), out);
				out.write('?');
					//No way to represent illegal character (&#xn not allowed, either)
					//FUTURE: consider to use a special escape sequence
					//and restore it at the client. But, it slows down
					//the performance and might not be worth
				k = ++j;
				continue;
			}
			++j;
		}

		if (k < len)
			encodeByCData(data.substring(k), out);
	}
	private static void encodeByCData(String data, Writer out)
	throws IOException {
		int j = data.length();
		if (j > 100) { //Not to scan if it is long
			out.write("<![CDATA[");
			out.write(data);
			out.write("]]>");
			return;
		}

		while (--j >= 0) {
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
