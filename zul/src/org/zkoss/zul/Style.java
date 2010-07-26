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
	private String _media;
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
	 * @param src the URI of an external style sheet.
	 * @param media the media dependencies for the style sheet.
	 * @since 5.0.3
	 */
	public Style(String src, String media) {
		this(src);
		setMedia(media);
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

	/** Returns the media dependencies for this style sheet.
	 *
	 * <p>Default: null.
	 * <p>Refer to <a href="http://www.w3.org/TR/CSS2/media.html">media-depedent style sheet</a> for details.
	 * @since 5.0.3
	 */
	public String getMedia() {
		return _media;
	}
	/** Sets the media dependencies for this style sheet.
	 * <p>Refer to <a href="http://www.w3.org/TR/CSS2/media.html">media-depedent style sheet</a> for details.
	 *
	 * @param media the media dependencies for this style sheet
	 * @since 5.0.3
	 */
	public void setMedia(String media) {
		if (media != null && media.length() == 0)
			media = null;
		if (!Objects.equals(_media, media)) {
			_media = media;
			smartUpdate("media", _media);
		}
	}

	/** Returns the content of the style element.
	 * By content we mean the CSS rules that will be sent to the client.
	 *
	 * <p>Default: null.
	 *
	 * <p>Deriving class can override this method to return whatever
	 * it prefers (ignored if null).
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

		boolean gened = false;
		final String cnt = getContent();
			//allow derive to override getContent()
		if (cnt != null) {
			final HtmlPageRenders.RenderContext rc =
				HtmlPageRenders.getRenderContext(null);
			if (rc != null) {
				final Writer out = rc.perm;
					//don't use rc.temp which will be replaced with widgets later
				out.write("\n<style id=\"");
				out.write(getUuid());
				out.write("-css\" type=\"text/css\"");
				if (_media != null) {
					out.write(" media=\"");
					out.write(_media);
					out.write('"');
				}
				out.write(">\n");
				out.write(cnt);
				out.write("\n</style>\n");
				gened = true;
			}
		}
		if (!gened) {
			render(renderer, "src", getEncodedURL());
			render(renderer, "media", _media);
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
	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl implements DynamicMedia {
		//-- DynamicMedia --//
		public Media getMedia(String pathInfo) {
			return new AMedia("css", "css", "text/css;charset=UTF-8", getContent());
		}
	}

	/** Returns the encoded URL of the image (never null).
	 */
	private String getEncodedURL() {
		if (getContent() != null) //allow derived to override getContent()
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
