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

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Desktop;

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
public class Style extends AbstractComponent {
	private String _src;
	/** _src and _content cannot be nonnull at the same time. */
	private String _content;
	private String _media;

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
			smartUpdate("content", _content);
		}
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "media", _media);
		final String cnt = getContent();
		if (cnt != null)
			render(renderer, "content", cnt);
		else
			render(renderer, "src", getEncodedURL());
	}

	//Component//
	/** Not allowed. */
	public boolean setVisible(boolean visible) {
		throw new UnsupportedOperationException("style is always invisible");
	}

	/** Not childable. */
	protected boolean isChildable() {
		return false;
	}

	/** Returns the encoded URL of the image (never null).
	 */
	private String getEncodedURL() {
		if (_src != null) {
			final Desktop dt = getDesktop();
			if (dt != null)
				return dt.getExecution().encodeURL(_src);
		}
		return "";
	}

	private class EncodedURL implements org.zkoss.zk.au.DeferredValue {
		public Object getValue() {
			return getEncodedURL();
		}
	}
}
