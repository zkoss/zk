/* Style.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 20 15:17:40     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.lang.Objects;
import org.zkoss.util.media.Media;
import org.zkoss.util.media.AMedia;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.DynamicMedia;

import org.zkoss.zul.impl.Utils;

/**
 * The style component used to specify CSS styles for the owner desktop.
 *
 * <p>Note: a style component can appear anywhere in a ZUML page, but it
 * affects all components in the same desktop.
 *
 * <p>Note: If {@link #isDynamic} is false, the HTML STYLE
 * or LINK tag is generated to represent this component.
 * Due to IE's limitation, there is no effect if
 * the style component is added or removed dynamically
 * and if {@link #isDynamic} is false.
 *
 * <p>If {@link #isDynamic} is true, this component can be added and removed
 * dynamically and the rules will be attached and detached accordingly.
 * Note: in this case, the link is generated when this component
 * is initialized at the client, so the style will be loaded to
 * the client after all components are initialized.
 *
 * <p>There are three formats when used in a ZUML page:
 *
 * <p>Method 1: Specify the URL of the CSS file
 * <pre><code>&lt;style src="my.css"/&gt;
 * </code></pre>
 *
 * <p>Method 2: Specify the CSS directly
 * <pre><code>&lt;style&gt;
 * .mycls {
 *  border: 1px outset #777;
 * }
 *&lt;/style&gt;
 * </code></pre>
 *
 * <p>Method 3: Specify the CSS by use of the content
 * property ({@link #setContent}).
 * <pre><code>&lt;style&gt;
 * &lt;attribute name="content"&gt;
 * .mycls {
 *  border: 1px outset #777;
 * }
 * &lt;/attribute&gt;
 *&lt;/style&gt;
 * </code></pre>
 *
 * <p>Note: if the src and content properties are both set,
 * the content property is ignored.
 *
 * @author tomyeh
 */
public class Style extends AbstractComponent implements org.zkoss.zul.api.Style {
	private String _src;
	/** _src and _content cannot be nonnull at the same time. */
	private String _content;
	private boolean _dynamic;
	/** Count the version of {@link #_content}. */
	private byte _cntver;

	public Style() {
		super.setVisible(false);
	}
	/**
	 * @param src the URI of an external style sheet.
	 */
	public Style(String src) {
		this();
		setSrc(src);
	}
	
	/**
	 * Sets whether to load an external Style Sheet dynamically.
	 * <p>Default: false.
	 * @since 3.0.4 
	 * @see #isDynamic
	 */
	public void setDynamic(boolean dynamic) {
		if (_dynamic != dynamic) {
			_dynamic = dynamic;
			invalidate();
		}
	}
	
	/**
	 * Returns whether to load an external Style Sheet dynamically.
	 * If false, a HTML STYLE or LINK tag is generated to represent 
	 * the content or the src.
	 *
	 * <p>Due to IE's limitation, there is no effect if
	 * the style component is added or removed dynamically
	 * and if {@link #isDynamic} is false.
	 *
	 * <p>If {@link #isDynamic} is true, this component can be added and removed
	 * dynamically and the rules will be attached and detached accordingly.
	 * Note: in this case, the HTML LINK tag is generated when this component
	 * is initialized at the client, so the style will be loaded to
	 * the client after all components are initialized.
	 *
	 * <p>Default: false.
	 * @since 3.0.4
	 */
	public boolean isDynamic() {
		return _dynamic;
	}

	/** Returns the URI of an external style sheet.
	 *
	 * <p>Default: null.
	 */
	public String getSrc() {
		return _src;
	}
	/** Sets the URI of an external style sheet.
	 *
	 * <p>Calling this method implies setContent(null).
	 * In other words, the last invocation of {@link #setSrc} overrides
	 * the previous {@link #setContent}, if any.
	 *
	 * @param src the URI of an external style sheet
	 * @see #setContent
	 */
	public void setSrc(String src) {
		if (src != null && src.length() == 0)
			src = null;
		if (_content != null || !Objects.equals(_src, src)) {
			_src = src;
			_content = null;
			invalidate();
		}
	}

	/** Returns the content of the style element.
	 * By content we mean the CSS rules that will be sent to the client.
	 *
	 * <p>Default: null.
	 *
	 * @since 3.0.0
	 */
	public String getContent() {
		return _content;
	}
	/** Sets the content of the style element.
	 * By content we mean the CSS rules that will be sent to the client.
	 *
	 * <p>Calling this method implies setSrc(null).
	 * In other words, the last invocation of {@link #setContent} overrides
	 * the previous {@link #setSrc}, if any.
	 * @since 3.0.0
	 * @see #setSrc
	 */
	public void setContent(String content) {
		if (content != null && content.length() == 0)
			content = null;

		if (_src != null || !Objects.equals(_content, content)) {
			_content = content;
			_src = null;
			++_cntver;
			invalidate();
		}
	}

	/** Returns the attributes for generating the HTML tags.
	 */
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64);
		HTMLs.appendAttribute(sb, "z.src",
			_src != null ? getDesktop().getExecution().encodeURL(_src):
			Utils.getDynamicMediaURI(this, _cntver, "css", "css"));
		return sb.toString();
	}

	//Component//
	/** Not allowd. */
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("style is always invisible");
	}
	/** Not childable. */
	public boolean isChildable() {
		return false;
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl implements DynamicMedia {
		//-- DynamicMedia --//
		public Media getMedia(String pathInfo) {
			return new AMedia("css", "css", "text/css;charset=UTF-8", _content);
		}
	}
	public void redraw(java.io.Writer out) throws java.io.IOException {
		if (isDynamic() && _content != null) {
			super.redraw(out);
			return;	
		}

		final boolean ie = Executions.getCurrent().isExplorer();
		if (ie) {
			//IE: unable to look back LINK or STYLE with ID
			out.write("<div id=\"");
			out.write(getUuid());
			out.write("\">");
		}

		if (_src != null) {
			out.write("\n<link rel=\"stylesheet\" type=\"text/css\" href=\"");
			out.write(getDesktop().getExecution().encodeURL(_src));
			if (!ie) {
				out.write("\" id=\"");
				out.write(getUuid());
			}
			out.write("\"/>");
		} else {
			out.write("\n<style type=\"text/css\"");
			if (!ie) {
				out.write(" id=\"");
				out.write(getUuid());
				out.write('"');
			}
			out.write(">\n");

			final String content = getContent();
			if (content != null) {
				out.write(content);
				out.write('\n');
			}

			out.write("</style>");
		}

		if (ie)
			out.write("</div>\n");
	}
}
