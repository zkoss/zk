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
import java.io.Writer;

import org.zkoss.lang.Objects;
import org.zkoss.util.media.Media;
import org.zkoss.util.media.AMedia;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zk.ui.sys.HtmlPageRenders;

import org.zkoss.zul.impl.Utils;

/**
 * The style component used to specify CSS styles for the owner desktop.
 *
 * <p>Note: a style component can appear anywhere in a ZUML page, but it
 * affects all components in the same desktop.
 *
 * <p>There are two formats when used in a ZUML page:
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
 * <p>Note: if the src and content properties are both set, the later one
 * overrides the previous one.
 *
 * @author tomyeh
 */
public class Style extends AbstractComponent implements org.zkoss.zul.api.Style {
	private String _src;
	/** _src and _content cannot be nonnull at the same time. */
	private String _content;
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
	
	/** @deprecated As of release 5.0.0, it is decided automatically.
	 */
	public void setDynamic(boolean dynamic) {
	}
	
	/** @deprecated As of release 5.0.0, it is decided automatically.
	 */
	public boolean isDynamic() {
		return true;
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
			smartUpdate("src", new EncodedURL());
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
			smartUpdate("src", new EncodedURL());
				//AU: always uses src to solve IE/Chrome/... issue
		}
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		final String encURL = getEncodedURL();
		render(renderer, "src", encURL);

		if (_content != null) {
			final HtmlPageRenders.RenderContext rc =
				HtmlPageRenders.getRenderContext(null);
			if (rc != null) {
				final Writer out = rc.extra;
				out.write("\n<style type=\"text/css\">\n");
				out.write(_content);
				out.write("\n</style>\n");

				render(renderer, "content", _content);
				//we have to re-gen later (in widget's redraw)
				//(since IE cannot retrieve the content of style)
				//
				//both src and content are generated (src for rerender)
			}
		}
	}

	//Component//
	/** Not allowd. */
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("style is always invisible");
	}
	/** Not childable. */
	protected boolean isChildable() {
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

	/** Returns the encoded URL of the image (never null).
	 */
	private String getEncodedURL() {
		if (_content != null)
			return Utils.getDynamicMediaURI(this, _cntver, "css", "css");

		if (_src != null) {
			final Desktop dt = getDesktop();
			if (dt != null)
				return dt.getExecution().encodeURL(_src);
		}
		return "";
	}

	private class EncodedURL implements org.zkoss.zk.ui.util.DeferredValue {
		public Object getValue() {
			return getEncodedURL();
		}
	}
}
