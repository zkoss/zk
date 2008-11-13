/* Script.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct 15 12:15:47     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.UiException;

/**
 * A component to represent script codes running at the client.
 * It is the same as HTML SCRIPT tag.
 *
 * <p>Note: it is the scripting codes running at the client, not at the
 * server. Don't confuse it with the <code>zscript</code> element.
 *
 * <p>There are three formats when used in a ZUML page:
 *
 * <p>Method 1: Specify the URL of the JS file
 * <pre><code>&lt;script type="text/javascript" src="my.js"/&gt;
 * </code></pre>
 *
 * <p>Method 2: Specify the JavaScript codes directly
 * <pre><code>&lt;script type="text/javascript"&gt;
 * some_js_at_browser();
 *&lt;/script&gt;
 * </code></pre>
 *
 * <p>Method 3: Specify the JavaScript codes by use of the content
 * property ({@link #setContent}).
 * <pre><code>&lt;script type="text/javascript"&gt;
 * &lt;attribute name="content"&gt;
 *  some_js_at_browser();
 * &lt;/attribute&gt;
 *&lt;/script&gt;
 * </code></pre>
 *
 * @author tomyeh
 */
public class Script extends AbstractComponent implements org.zkoss.zul.api.Script {
	private String _src, _type = "text/javascript", _charset;
	private String _content;
	private boolean _defer;

	public Script() {
	}

	/** Returns the type of this client script.
	 * <p>Default: text/javascript.
	 */
	public String getType() {
		return _type;
	}
	/** Sets the type of this client script.
	 * For JavaScript, it is <code>text/javascript</code>
	 *
	 * <p>Note: this property is NOT optional. You must specify one.
	 */
	public void setType(String type) {
		if (type == null || type.length() == 0)
			throw new IllegalArgumentException("non-empty is required");

		if (!Objects.equals(_type, type)) {
			_type = type;
			invalidate();
		}
	}
	/** Returns the character enconding of the source.
	 * It is used with {@link #getSrc}.
	 *
	 * <p>Default: null.
	 */
	public String getCharset() {
		return _charset;
	}
	/** Sets the character encoding of the source.
	 * It is used with {@link #setSrc}.
	 */
	public void setCharset(String charset) {
		if (charset != null && charset.length() == 0)
			charset = null;

		if (!Objects.equals(_charset, charset)) {
			_charset = charset;
			invalidate();
		}
	}

	/** Returns the URI of the source that contains the script codes.
	 * <p>Default: null.
	 */
	public String getSrc() {
		return _src;
	}
	/** Sets the URI of the source that contains the script codes.
	 *
	 * <p>You either add the script codes directly with the {@link Label}
	 * children, or
	 * set the URI to load the script codes with {@link #setSrc}.
	 * But, not both.
	 *
	 * @param src the URI of the source that contains the script codes
	 */
	public void setSrc(String src) {
		if (src != null && src.length() == 0)
			src = null;

		if (!Objects.equals(_src, src)) {
			_src = src;
			invalidate();
		}
	}

	/** Returns whether to defer the execution of the script codes.
	 *
	 * <p>Default: false.
	 */
	public boolean isDefer() {
		return _defer;
	}
	/** Sets whether to defer the execution of the script codes.
	 */
	public void setDefer(boolean defer) {
		if (_defer != defer) {
			_defer = defer;
			invalidate();
		}
	}

	/** Returns the content of the script element.
	 * By content we mean the JavaScript codes that will be enclosed
	 * by the HTML SCRIPT element.
	 *
	 * <p>Default: null.
	 *
	 * @since 3.0.0
	 */
	public String getContent() {
		return _content;
	}
	/** Sets the content of the script element.
	 * By content we mean the JavaScript codes that will be enclosed
	 * by the HTML SCRIPT element.
	 *
	 * @since 3.0.0
	 */
	public void setContent(String content) {
		if (content != null && content.length() == 0)
			content = null;

		if (!Objects.equals(_content, content)) {
			_content = content;
			invalidate();
		}
	}

	//-- Component --//
	/** Not childable. */
	protected boolean isChildable() {
		return false;
	}
	public void redraw(java.io.Writer out) throws java.io.IOException {
		final StringBuffer sb = new StringBuffer(256).append("\n<script");
		HTMLs.appendAttribute(sb, "id",  getUuid());
		HTMLs.appendAttribute(sb, "type",  _type);
		HTMLs.appendAttribute(sb, "charset",  _charset);

		if (_src != null)
			HTMLs.appendAttribute(sb, "src",
				getDesktop().getExecution().encodeURL(_src));

		if (_defer)
			sb.append(" defer=\"defer\"");

		out.write(sb.append(">\n").toString());

		final String content = getContent();
		if (content != null) {
			out.write(content);
			out.write('\n');
		}

		out.write("</script>");
	}
}
