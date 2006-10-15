/* Script.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct 15 12:15:47     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.UiException;

/**
 * Represents a HTML <code>script</code> element.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Script extends AbstractComponent {
	private String _content = "";
	private String _src, _type, _charset;
	private boolean _defer;

	public Script() {
	}

	/** Returns the type of this script.
	 * <p>Default: null.
	 */
	public String getType() {
		return _type;
	}
	/** Sets the type of this script.
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
	 * <p>You either set the script codes directly with {@link #setContent}, or
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

	/** Returns the content of the script codes.
	 * <p>Default: empty.
	 */
	public String getContent() {
		return _content;
	}
	/** Sets the content of the script codes directly.
	 *
	 * <p>You either set the script codes directly with {@link #setContent}, or
	 * set the URI to load the script codes with {@link #setSrc}.
	 * But, not both.
	 *
	 * @param content the script codes
	 */
	public void setContent(String content) {
		if (content == null) content = "";
		if (!Objects.equals(_content, content)) {
			_content = content;
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

	/** Returns the exterior attribute for generating HTML tags.
	 * <p>Used only for component development.
	 */
	public String getOuterAttrs() {
		if (_type == null)
			throw new UiException("The type is required. For example, text/javascript");

		final StringBuffer sb = new StringBuffer(80);
		HTMLs.appendAttribute(sb, "type",  _type);
		HTMLs.appendAttribute(sb, "charset",  _charset);
		if (_src != null)
			HTMLs.appendAttribute(sb, "src",
				getDesktop().getExecution().encodeURL(_src));
		if (_defer)
			sb.append(" defer=\"defer\"");
		return sb.toString();
	}

	//-- Component --//
	/** Default: not childable.
	 */
	public boolean isChildable() {
		return false;
	}
}
