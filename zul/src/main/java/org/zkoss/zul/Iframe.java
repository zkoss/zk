/* Iframe.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 21 11:11:18     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.util.media.Media;
import org.zkoss.util.media.RepeatableMedia;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.URIEvent;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * Includes an inline frame.
 *
 * <p>Unlike HTML iframe, this component doesn't have the frameborder
 * property. Rather, use the CSS style to customize the border (like
 * any other components).
 *
 * <p>To handle the onload event, you have to use the client-attribute namespace.
 * Please refer to <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Iframe#onload">ZK Component Reference: iframe</a> fore more information.
 *
 * @author tomyeh
 * @see Include
 */
public class Iframe extends HtmlBasedComponent {
	private String _name;
	private String _src, _scrolling = "auto";
	/** The media. _src and _media cannot be nonnull at the same time. */
	private Media _media;
	/** Count the version of {@link #_media}. */
	private byte _medver;
	/** Whether to hide when a popup or dropdown is placed on top of it. */
	private boolean _autohide;

	static {
		addClientEvent(Iframe.class, Events.ON_URI_CHANGE, 0);
	}

	public Iframe() {
	}

	public Iframe(String src) {
		setSrc(src);
	}

	/**
	 * Define scroll bars
	 * @param scrolling "true", "false", "yes" or "no" or "auto", "auto" by default
	 * If null, "auto" is assumed.
	 * @since 3.0.4
	 */
	public void setScrolling(String scrolling) {
		if (scrolling == null)
			scrolling = "auto";
		if (!scrolling.equals(_scrolling)) {
			_scrolling = scrolling;
			smartUpdate("scrolling", _scrolling);
		}
	}

	/**
	 * Return the scroll bars.
	 * <p>Default: "auto"
	 * @since 3.0.4
	 */
	public String getScrolling() {
		return _scrolling;
	}

	/** Returns the frame name.
	 * <p>Default: null (use browser default).
	 */
	public String getName() {
		return _name;
	}

	/** Sets the frame name.
	 */
	public void setName(String name) {
		if (!Objects.equals(_name, name)) {
			_name = name;
			smartUpdate("name", _name);
		}
	}

	/** Returns whether to automatically hide this component if
	 * a popup or dropdown is overlapped with it.
	 *
	 * <p>Default: false.
	 *
	 * <p>If an iframe contains PDF or other non-HTML resource,
	 * it is possible that it obscures the popup that shall be shown
	 * above it. To resolve this, you have to specify autohide="true"
	 * to this component, and specify the following in the page:
	 * <pre><code>&lt;?script content="zk.useStack='auto';"?>
	 * <p>Please refer to <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Customization/Stackup_and_Shadow">Stackup and Shadow</a>
	 * for more information.
	 */
	public boolean isAutohide() {
		return _autohide;
	}

	/** Sets whether to automatically hide this component if
	 * a popup or dropdown is overlapped with it.
	 * Refer to {@link #isAutohide} for more information
	 */
	public void setAutohide(boolean autohide) {
		if (_autohide != autohide) {
			_autohide = autohide;
			smartUpdate("autohide", _autohide);
		}
	}

	/** Returns the src.
	 * <p>Default: null.
	 */
	public String getSrc() {
		return _src;
	}

	/** Sets the src.
	 *
	 * <p>Calling this method implies setContent(null).
	 * In other words, the last invocation of {@link #setSrc} overrides
	 * the previous {@link #setContent}, if any.
	 * @param src the source URL. If null or empty, nothing is included.
	 * @see #setContent
	 */
	public void setSrc(String src) {
		if (src != null && src.length() == 0)
			src = null;

		if (_media != null || !Objects.equals(_src, src)) {
			_src = src;
			_media = null;
			smartUpdate("src", new EncodedSrc()); //Bug 1850895
		}
	}

	/** Returns the encoded src ({@link #getSrc}).
	 */
	protected String getEncodedSrc() {
		final Desktop dt = getDesktop();
		return _media != null ? getMediaSrc()
				: //already encoded
				dt != null && _src != null ? dt.getExecution().encodeURL(_src) : "";
	}

	/** Sets the content directly.
	 * Default: null.
	 *
	 * <p>Calling this method implies setSrc(null).
	 * In other words, the last invocation of {@link #setContent} overrides
	 * the previous {@link #setSrc}, if any.
	 * @param media the media for this inline frame.
	 * @see #setSrc
	 */
	public void setContent(Media media) {
		if (_src != null || media != _media) {
			_media = RepeatableMedia.getInstance(media);
			//Use RepeatableMedia since it might be reloaded
			//if the component is invalidated or overlapped wnd (Bug 1896797)
			_src = null;
			if (_media != null)
				++_medver; //enforce browser to reload
			smartUpdate("src", new EncodedSrc()); //Bug 1850895
		}
	}

	/** Returns the content set by {@link #setContent}.
	 * <p>Note: it won't fetch what is set thru by {@link #setSrc}.
	 * It simply returns what is passed to {@link #setContent}.
	 */
	public Media getContent() {
		return _media;
	}

	/** Returns the encoded URL for the current media content.
	 * Don't call this method unless _media is not null;
	 */
	private String getMediaSrc() {
		return Utils.getDynamicMediaURI(this, _medver, _media.getName(), _media.getFormat());
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "src", getEncodedSrc());
		if (!"auto".equals(_scrolling))
			render(renderer, "scrolling", _scrolling);
		render(renderer, "name", _name);
		render(renderer, "autohide", _autohide);
	}

	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link XulElement#service(AuRequest, boolean)},
	 * it also handles onURIChange.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (Events.ON_URI_CHANGE.equals(cmd)) {
			Events.postEvent(URIEvent.getURIEvent(request));
		} else
			super.service(request, everError);
	}

	//-- Component --//
	/** Default: not childable.
	 */
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
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements DynamicMedia {
		//-- DynamicMedia --//
		public Media getMedia(String pathInfo) {
			return _media;
		}
	}

	private class EncodedSrc implements org.zkoss.zk.au.DeferredValue {
		public Object getValue() {
			return getEncodedSrc();
		}
	}
}
