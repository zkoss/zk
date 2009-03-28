/* Flash.java

 {{IS_NOTE
 Purpose: ZK Flash Component
 
 Description:
 
 History:
 Jul 17, 2007 , Created by jeffliu
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 3.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */

package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.util.media.Media;
import org.zkoss.util.media.RepeatableMedia;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.impl.Utils;

/**
 * A generic flash component.
 *
 * <p>Non XUL extension.
 *
 * @author Jeff
 * @since 3.0.0
 */
public class Flash extends HtmlBasedComponent implements org.zkoss.zul.api.Flash {
	private String _src;
	private Media _media;
	private String _wmode = "transparent";	
	private String _quality = "hight";
	private byte _medver;
	private boolean _autoPlay = true;
	private boolean _loop;

	public Flash() {
	}
	public Flash(String src) {
		setSrc(src);
	}
	
	/**
	 * Returns true if the Flash movie plays repeatly
	 * <p>Default: false
	 * @return true if the Flash movie plays repeatly 
	 */
	public boolean isLoop() {
		return _loop;
	}
	/**
	 * Sets whether the Flash movie plays repeatly
	 * @param loop
	 */
	public void setLoop(boolean loop) {
		if(_loop != loop){
			_loop = loop;
			smartUpdate("loop",loop);
		}
	}
	
	/**
	 * Return true if the Flash movie starts playing automatically
	 * <p>Default: true
	 * @return true if the Flash movie starts playing automatically
	 */
	public boolean isAutoPlay() {
		return _autoPlay;
	}
	/**
	 * Sets wether the song Flash movie playing automatically
	 * @param play
	 */
	public void setAutoPlay(boolean play){
		if(_autoPlay != play){
			_autoPlay = play;
			smartUpdate("autoPlay",play);
		}
	}
	
	/**
	 * Returns the Window mode property of the Flash movie 
	 * <p>Default: "transparent".
	 * @return the Window mode property of the Flash movie 
	 */
	public String getWmode() {
		return _wmode;
	}
	/**
	 * Sets the Window Mode property of the Flash movie 
	 * for transparency, layering, and positioning in the browser.
	 * @param wmode Possible values: window, opaque, transparent.
	 */
	public void setWmode(String wmode) {
		if(!Objects.equals(_wmode, wmode)){
			_wmode = wmode;
			smartUpdate("wmode", wmode);
		}
	}

	/**
	 * Returns the quality of the Flash movie 
	 * <p>Default: "high".
	 * @return the quality of the Flash movie 
	 * @since 5.0.0
	 */
	public String getQuality() {
		return _quality;
	}
	/**
	 * Sets the quality of the Flash movie.
	 * @param quality the quality of the Flash movie.
	 * @since 5.0.0
	 */
	public void setQuality(String quality) {
		if(!Objects.equals(_quality, quality)){
			_quality = quality;
			smartUpdate("quality", quality);
		}
	}

	/**
	 * Gets the source path of Flash movie
	 * @return  the source path of Flash movie
	 */
	public String getSrc() {
		return _src;
	}
	/**
	 * Sets the source path of Flash movie 
	 * and redraw the component
	 * @param src
	 */
	public void setSrc(String src) {
		if (src != null && src.length() == 0)
			src = null;

		if (_media != null || !Objects.equals(_src, src)) {
			_src = src;
			_media = null;
			smartUpdate("src", new EncodedSrc());
		}
	}

	/** Sets the content of the flash directly.
	 * Default: null.
	 *
	 * <p>Calling this method implies setSrc(null).
	 * In other words, the last invocation of {@link #setContent} overrides
	 * the previous {@link #setSrc}, if any.
	 * @param media the media representing the flash, i.e., SWF.
	 * @see #setSrc
	 * @since 3.6.1
	 */
	public void setContent(Media media) {
		if (_src != null || media != _media) {
			_media = RepeatableMedia.getInstance(media);
				//Use RepeatableMedia since it might be reloaded
				//if the component is invalidated or overlapped wnd (Bug 1896797)
			_src = null;
			if (_media != null) ++_medver; //enforce browser to reload
			smartUpdate("src", new EncodedSrc());
		}
	}
	/** Returns the content set by {@link #setContent}.
	 * <p>Note: it won't fetch what is set thru by {@link #setSrc}.
	 * It simply returns what is passed to {@link #setContent}.
	 * @since 3.6.1
	 */
	public Media getContent() {
		return _media;
	}

	private String getEncodedSrc() {
		final Desktop dt = getDesktop();
		return _media != null ? getMediaSrc(): //already encoded
			dt != null && _src != null ?
				dt.getExecution().encodeURL(_src):  "";
	}
	private String getMediaSrc() {
		return Utils.getDynamicMediaURI(
			this, _medver, _media.getName(), _media.getFormat());
	}
	private class EncodedSrc implements org.zkoss.zk.ui.util.DeferredValue {
		public Object getValue() {
			return getEncodedSrc();
		}
	}

	//super//
	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "src", getEncodedSrc());
		if (!"transparent".equals(_wmode))
			render(renderer, "wmode", _wmode);
		if (!"high".equals(_quality))
			render(renderer, "quality", _quality);
		if (_loop) renderer.render("loop", _loop);
		if (!_autoPlay) renderer.render("autoPlay", _autoPlay);
	}
}
