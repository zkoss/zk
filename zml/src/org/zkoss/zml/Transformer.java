/* Transformer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 28 13:57:53     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zml;

import java.util.Iterator;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import org.zkoss.idom.Document;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.UiException;

/**
 * XML transformer.
 *
 * @author tomyeh
 */
public class Transformer extends AbstractComponent {
	private Object _xsl;

	/** Returns the XSL (Extensible Stylesheet Language), or null
	 * if not available.
	 *
	 * @see #setXsl(String)
	 * @see #setXsl(URL)
	 * @see #setXsl(File)
	 * @see #setXsl(Source)
	 */
	public Object getXsl() {
		return _xsl;
	}
	/** Sets the XSL with the resource path.
	 * The resource must be retrievable by use of {@link WebApp#getResource}.
	 */
	public void setXsl(String xsl) {
		_xsl = xsl;
	}
	/** Sets the XSL with a file.
	 */
	public void setXsl(File xsl) {
		_xsl = xsl;
	}
	/** Sets the XSL with an URL.
	 */
	public void setXsl(URL xsl) {
		_xsl = xsl;
	}
	/** Sets the XSL with an input stream.
	 */
	public void setXsl(InputStream xsl) {
		_xsl = xsl;
	}
	/** Sets the XSL with a reader.
	 */
	public void setXsl(Reader xsl) {
		_xsl = xsl;
	}
	/** Sets the XSL with a XML source.
	 */
	public void setXsl(Source xsl) {
		_xsl = xsl;
	}
	/** Sets the XSL with a document.
	 */
	public void setXsl(org.w3c.dom.Document xsl) {
		_xsl = xsl;
	}
	/** Sets the XSL with a iDOM document.
	 */
	public void setXsl(Document xsl) {
		_xsl = xsl;
	}

	//Component//
	public void redraw(Writer out) throws IOException {
		final Source src;
		{
			final StringWriter sw = new StringWriter(1024);
			for (Iterator it = getChildren().iterator(); it.hasNext();)
				((ComponentCtrl)it.next()).redraw(sw);
			src = new StreamSource(new StringReader(sw.toString()));
		}

		final Source xsl;
		InputStream is = null;
		if (_xsl instanceof String) {
			is = getDesktop().getWebApp().getResourceAsStream(
				getDesktop().getExecution().toAbsoluteURI((String)_xsl, false));
			if (is == null)
				throw new UiException("Resouce not found, "+_xsl);
			xsl = new StreamSource(is);
		} else if (_xsl instanceof File) {
			xsl = new StreamSource((File)_xsl);
		} else if (_xsl instanceof InputStream) {
			xsl = new StreamSource((InputStream)_xsl);
		} else if (_xsl instanceof Reader) {
			xsl = new StreamSource((Reader)_xsl);
		} else if (_xsl instanceof URL) {
			xsl = new StreamSource(is = ((URL)_xsl).openStream());
		} else if (_xsl instanceof org.w3c.dom.Document) { //include iDOM
			xsl = new DOMSource((org.w3c.dom.Document)_xsl);
		} else if (_xsl == null) {
			xsl = null;
		} else {
			throw new InternalError("Unknown XSL: "+_xsl.getClass().getName());
		}

		final StringWriter result = new StringWriter();

		try {
			new org.zkoss.idom.transform.Transformer(xsl)
				.transform(src, new StreamResult(result));
		} catch (Throwable ex) {
			throw UiException.Aide.wrap(ex);
		} finally {
			if (is != null) try {is.close();} catch (Throwable ex) {}
		}

		//We have to stripe <?xml...?> since UiEngine generates spaces
		//before this component
		final StringBuffer sb = result.getBuffer();
		int j = 0;
		l_out:
		for (int len = sb.length(); j < len; j++) {
			char cc = sb.charAt(j);
			if (isSpace(cc))
				continue;

			if (cc == '<') {
				int k = j;
				if (isChar(sb, ++k, '?') && isChar(sb, ++k, 'x')
				&& isChar(sb, ++k, 'm') && isChar(sb, ++k, 'l')
				&& ++k < len && isSpace(sb.charAt(k))) {
					while (++k < len) {
						cc = sb.charAt(k);
						if (cc == '>') {
							j = k + 1;
							break l_out; //done
						}
					}
				}
			}
			break;
		}
		out.write(sb.substring(j));
	}
	private static boolean isSpace(char cc) {
		return cc == ' ' || cc == '\t' || cc == '\n' || cc == '\r';
	}
	private static boolean isChar(StringBuffer sb, int j, char cc) {
		return j < sb.length() && sb.charAt(j) == cc;
	}
}
