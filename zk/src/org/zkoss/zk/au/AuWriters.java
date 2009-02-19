/* AuWriters.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec  4 11:20:33     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.idom.Verifier;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.http.HttpAuWriter;
import org.zkoss.zk.device.marshal.Marshaller;

/**
 * Utilities to instantiate an implmentation of {@link AuWriter}.
 *
 * @author tomyeh
 * @since 3.0.1
 */
public class AuWriters {
	/** The implementation class of {@link AuWriter}. */
	private static Class _awCls;

	/** Returns the implementation class of {@link AuWriter} that
	 * will be used to generate the output to the client.
	 *
	 * <p>Default: {@link HttpAuWriter}.
	 */
	public static Class getImplementationClass() {
		return _awCls != null ? _awCls: HttpAuWriter.class;
	}
	/** Sets the implementation class of {@link AuWriter} that
	 * will be used to generate the output to the client.
	 */
	public static void setImplementationClass(Class cls) {
		if (cls != null)
			if (cls.equals(HttpAuWriter.class))
				cls = null;
			else if (!AuWriter.class.isAssignableFrom(cls))
				throw new IllegalArgumentException(cls+" must implement "+AuWriter.class.getName());
		_awCls = cls;
	}
	/** Creates an instance of {@link AuWriter}.
	 */
	public static AuWriter newInstance() throws UiException {
		if (_awCls != null) {
			try {
				return (AuWriter)_awCls.newInstance();
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
		return new HttpAuWriter();
	}

	/** The first few characters of the output content.
	 * @since 3.5.0
	 */
	public static final String CONTENT_HEAD =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	/** The content type of the output.
	 * @since 3.5.0
	 */
	public static final String CONTENT_TYPE = "text/xml;charset=UTF-8";

	/** Wites a XML fragment representing the response ID.
	 * @since 3.5.0
	 */
	public static void writeResponseId(Writer out, int resId) throws IOException {
		out.write("<rid>");
		out.write(Integer.toString(resId));
		out.write("</rid>\n");
	}
	/** Writes a XML fragment representing the response to the output.
	 * @since 5.0.0
	 */
	public static
	void write(Marshaller marshaller, Writer out, AuResponse response)
	throws IOException {
		out.write("\n<r><c>");
		out.write(response.getCommand());
		out.write("</c>");
		final String[] data = response.getEncodedData(marshaller);
		if (data != null) {
			for (int j = 0; j < data.length; ++j) {
				out.write("\n<d>");
				encodeXML(out, data[j]);
				out.write("</d>");
			}
		}
		out.write("\n</r>");
	}
	//Private Utilities//
	private static void encodeXML(Writer out, String data)
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
					encodeByCData(out, data.substring(k, j + 2));
					out.write("&gt;");
					k = j += 3;
					continue;
				}
			} else if (!Verifier.isXMLCharacter(cc)) {
				encodeByCData(out, data.substring(k, j));
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
			encodeByCData(out, data.substring(k));
	}
	private static void encodeByCData(Writer out, String data)
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
