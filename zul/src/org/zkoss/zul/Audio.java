/* Audio.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 16 11:48:27     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.util.media.Media;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.ext.render.DynamicMedia;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.impl.Utils;

/**
 * An audio clip.
 *
 * <p>An extension to XUL.
 *
 * @author tomyeh
 */
public class Audio extends XulElement {
	protected List<String> _src;
	/** The audio. _src and _audio cannot be nonnull at the same time. */
	private List<org.zkoss.sound.Audio> _audio;
	/** Count the version of {@link #_audio}. */
	private byte _audver;
	private boolean _autoplay, _controls, _loop, _muted;
	private String _preload;

	public Audio() {
		_src = new ArrayList<String>();
		_audio = new ArrayList<org.zkoss.sound.Audio>();
	}
	public Audio(String src) {
		_src = new ArrayList<String>();
		_audio = new ArrayList<org.zkoss.sound.Audio>();
		setSrc(src);
	}

	/** Plays the audio at the client.
	 */
	public void play() {
		response("ctrl", new AuInvoke(this, "play"));
	}
	/** Stops the audio at the client.
	 */
	public void stop() {
		response("ctrl", new AuInvoke(this, "stop"));
	}
	/** Pauses the audio at the client.
	 */
	public void pause() {
		response("ctrl", new AuInvoke(this, "pause"));
	}

	/** Returns the src.
	 * <p>Default: [].
	 */
	public List<String> getSrc() {
		return _src;
	}
	/** Sets the src.
	 *
	 * <p>Calling this method implies setContent(null).
	 * In other words, the last invocation of {@link #setSrc} overrides
	 * the previous {@link #setContent}, if any.
	 * @see #setContent
	 */
	public void setSrc(String src) {
		List<String> list = new ArrayList<String>();  
		if (src.contains(",")) {
			list = new ArrayList(Arrays.asList(src.split("\\s*,\\s*"))); 
		} else {
			list.add(src.trim());
		}
		if (!_audio.isEmpty() || !_src.equals(list)) {
			_audio.clear();
			setSrc(list);
		}
		
	}
	/** Sets the source list.
	 * 
	 * @since 7.0.0
	 */
	public void setSrc(List<String> src) {
		if (!src.equals(_src)) {
			_src = src;
			smartUpdate("src", new EncodedSrc());
		}
	}
	
	private List<String> getEncodedSrc() {
		final Desktop dt = getDesktop();
		List<String> list = new ArrayList<String>();
		if(!_audio.isEmpty()) {
			for (org.zkoss.sound.Audio audio : _audio) {
				list.add(getAudioSrc(audio));
			}
		} else if (dt != null) {
			for(String src: _src) {
				list.add(dt.getExecution().encodeURL(src));
			}
		}
		return list;
	}

	/** Returns whether to auto start playing the audio.
	 *
	 * <p>Default: false;
	 * @deprecated As of release 7.0.0, use {@link #isAutoplay()} instead.
	 */
	public boolean isAutostart() {
		return isAutoplay();
	}
	/** Sets whether to auto start playing the audio.
	 * @deprecated As of release 7.0.0, use {@link #setAutoplay()} instead.
	 */
	public void setAutostart(boolean autostart) {
		setAutoplay(autostart);
	}
	/** Returns whether to auto start playing the audio.
	 *
	 * <p>Default: false;
	 * @since 7.0.0
	 */
	public boolean isAutoplay() {
		return _autoplay;
	}
	/** Sets whether to auto start playing the audio.
	 * 
	 * @since 7.0.0
	 */
	public void setAutoplay(boolean autoplay) {
		if (_autoplay != autoplay) {
			_autoplay = autoplay;
			smartUpdate("autoplay", _autoplay);
		}
	}
	/** Returns whether and how the audio should be loaded.
	 *
	 * <p>Default: false;
	 * @since 7.0.0
	 */
	public String getPreload() {
		return _preload;
	}
	/** Sets whether and how the audio should be loaded.
	 * @since 7.0.0
	 */
	public void setPreload(String preload) {
		if ("none".equalsIgnoreCase(preload)) {
			preload = "none";
		} else if ("metadata".equalsIgnoreCase(preload)){
			preload = "metadata";
		} else {
			preload = "auto";
		}
		if (!preload.equals(_preload)) {
			_preload = preload;
			smartUpdate("preload", _preload);
		}
	}
	/** Returns whether to display the audio controls.
	 *
	 * <p>Default: false;
	 * @since 7.0.0
	 */
	public boolean isControls() {
		return _controls;
	}
	/** Sets whether to display the audio controls.
	 * @since 7.0.0
	 */
	public void setControls(boolean controls) {
		if (_controls != controls) {
			_controls = controls;
			smartUpdate("controls", _controls);
		}
	}
	/** Returns whether to play the audio repeatedly.
	 *
	 * <p>Default: false;
	 * @since 3.6.1
	 */
	public boolean isLoop() {
		return _loop;
	}
	/** Sets whether to play the audio repeatedly.
	 * @since 3.6.1
	 */
	public void setLoop(boolean loop) {
		if (_loop != loop) {
			_loop = loop;
			smartUpdate("loop", _loop);
		}
	}
	/** Returns whether to mute the audio.
	 *
	 * <p>Default: false;
	 * @since 7.0.0
	 */
	public boolean isMuted() {
		return _muted;
	}
	/** Sets whether to mute the audio.
	 * @since 7.0.0
	 */
	public void setMuted(boolean muted) {
		if (_muted != muted) {
			_muted = muted;
			smartUpdate("muted", _muted);
		}
	}
	/** Sets the content directly.
	 * <p>Default: null.
	 *
	 * <p>Calling this method implies setSrc(null).
	 * In other words, the last invocation of {@link #setContent} overrides
	 * the previous {@link #setSrc}, if any.
	 * @param audio the audio to display.
	 * @see #setSrc
	 */
	public void setContent(org.zkoss.sound.Audio audio) {
		List<org.zkoss.sound.Audio> list = new ArrayList<org.zkoss.sound.Audio>();
		list.add(audio);
		setContent(list);
	}
	public void setContent(List<org.zkoss.sound.Audio> audio) {
		if (!_src.isEmpty() || !_audio.contains(audio)) {
			_audio.clear();
			_audio.addAll(audio);
			if (!_src.isEmpty()) 
				_src.clear();
			if (!_audio.isEmpty()) ++_audver; //enforce browser to reload
			smartUpdate("src", new EncodedSrc());
		}
	}
	/** Returns the content set by {@link #setContent}.
	 * <p>Note: it won't fetch what is set thru by {@link #setSrc}.
	 * It simply returns what is passed to {@link #setContent}.
	 */
	public org.zkoss.sound.Audio getContent() {
		return _audio.get(0);
	}

	/** Returns the encoded URL for the current audio content.
	 * Don't call this method unless _audio is not null;
	 */
	private String getAudioSrc(org.zkoss.sound.Audio audio) {
		return Utils.getDynamicMediaURI(
			this, _audver, audio.getName(), audio.getFormat());
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "src", getEncodedSrc());
		render(renderer, "autoplay", _autoplay);
		render(renderer, "preload", _preload);
		render(renderer, "controls", _controls);
		render(renderer, "loop", _loop);
		render(renderer, "muted", _muted);
	}

	/** Default: not childable.
	 */
	protected boolean isChildable() {
		return false;
	}
	
	// ComponentCtrl
	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements DynamicMedia {
		// get Audio
		public Media getMedia(String pathInfo) {
			if (_audio.isEmpty())
				return null;
			return _audio.get(0);
		}
	}

	private class EncodedSrc implements org.zkoss.zk.au.DeferredValue {
		public Object getValue() {
			return getEncodedSrc();
		}
	}
}
