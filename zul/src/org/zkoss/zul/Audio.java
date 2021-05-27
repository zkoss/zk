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
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.out.AuInvoke;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.StateChangeEvent;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zul.ext.MediaElement;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * An audio clip.
 *
 * <p>An extension to XUL.
 * Only works for browsers supporting HTML5 audio tag (since ZK 7.0.0).
 *
 * @author tomyeh
 */
public class Audio extends XulElement implements MediaElement {
	/**
	 * Represent the stop state
	 * @since 9.6.0
	 */
	public static final int STOP = 0;
	/**
	 * Represent the play state
	 * @since 9.6.0
	 */
	public static final int PLAY = 1;
	/**
	 * Represent the pause state
	 * @since 9.6.0
	 */
	public static final int PAUSE = 2;
	/**
	 * Represent the end state
	 * @since 9.6.0
	 */
	public static final int END = 3;

	protected List<String> _src = new ArrayList<String>();
	/** The audio. _src and _audio cannot be nonnull at the same time. */
	private org.zkoss.sound.Audio _audio;
	/** Count the version of {@link #_audio}. */
	private byte _audver;
	private boolean _autoplay, _controls, _loop, _muted;
	private String _preload;
	private int _currentState;

	static {
		addClientEvent(Audio.class, Events.ON_STATE_CHANGE, CE_IMPORTANT);
	}

	public Audio() {
	}

	public Audio(String src) {
		setSrc(src);
	}

	@Override
	public void service(AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (Events.ON_STATE_CHANGE.equals(cmd)) {
			_currentState = (Integer) request.getData().get("state");
			Events.postEvent(new StateChangeEvent(cmd, this, _currentState));
		} else {
			super.service(request, everError);
		}
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
		_currentState = STOP;
		Events.postEvent(new StateChangeEvent(Events.ON_STATE_CHANGE, this, _currentState));
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
			list = new ArrayList<String>(Arrays.asList(src.split("\\s*,\\s*")));
		} else {
			list.add(src.trim());
		}
		if (_audio != null || !_src.equals(list)) {
			_audio = null;
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

	/** Returns whether to auto start playing the audio.
	 *
	 * <p>Default: false;
	 * @deprecated As of release 7.0.0, use {@link #isAutoplay} instead.
	 */
	public boolean isAutostart() {
		return isAutoplay();
	}

	/** Sets whether to auto start playing the audio.
	 * 
	 * @deprecated As of release 7.0.0, use {@link #setAutoplay} instead.
	 */
	public void setAutostart(boolean autostart) {
		setAutoplay(autostart);
	}

	/** Returns whether to auto start playing the audio.
	 *
	 * <p>Default: false.
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
	 * "none" or "metadata" or "auto" or null
	 * <p>Default: null.
	 * @since 7.0.0
	 */
	public String getPreload() {
		return _preload;
	}

	/** Sets whether and how the audio should be loaded.
	 * Refer to <a href="http://www.w3.org/TR/html5/embedded-content-0.html#attr-media-preload">Preload Attribute Description</a> for details.
	 * @param preload which could be one of "none", "metadata", "auto".
	 * @since 7.0.0
	 */
	public void setPreload(String preload) {
		if ("none".equalsIgnoreCase(preload)) {
			preload = "none";
		} else if ("metadata".equalsIgnoreCase(preload)) {
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
	 * <p>Default: false.
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
	 * <p>Default: false.
	 * @since 3.6.1
	 */
	public boolean isLoop() {
		return _loop;
	}

	/** Sets whether to play the audio repeatedly.
	 * 
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
	 * <p>Default: false.
	 * @since 7.0.0
	 */
	public boolean isMuted() {
		return _muted;
	}

	/** Sets whether to mute the audio.
	 * 
	 * @since 7.0.0
	 */
	public void setMuted(boolean muted) {
		if (_muted != muted) {
			_muted = muted;
			smartUpdate("muted", _muted);
		}
	}

	/**
	 * Return whether the audio is playing.
	 *
	 * @return true if audio is playing;
	 * @since 9.6.0
	 */
	public boolean isPlaying() {
		return _currentState == PLAY;
	}

	/**
	 * Return whether the audio is paused.
	 *
	 * @return true if audio is paused;
	 * @since 9.6.0
	 */
	public boolean isPaused() {
		return _currentState == PAUSE;
	}

	/**
	 * Return whether the audio is stopped.
	 * While the audio is ended, also means it is stopped.
	 *
	 * @return true if audio is stopped or ended;
	 * @since 9.6.0
	 */
	public boolean isStopped() {
		return _currentState == STOP || _currentState == END;
	}

	/**
	 * Return whether the audio is ended.
	 *
	 * @return true if audio is ended;
	 * @since 9.6.0
	 */
	public boolean isEnded() {
		return _currentState == END;
	}

	/** Sets the content directly.
	 * 
	 * <p>Default: null.
	 *
	 * <p>Calling this method implies setSrc(null).
	 * In other words, the last invocation of {@link #setContent} overrides
	 * the previous {@link #setSrc}, if any.
	 *
	 * @param audio the audio to display.
	 * @see #setSrc
	 */
	public void setContent(org.zkoss.sound.Audio audio) {
		if (_src != null || audio != _audio) {
			_audio = audio;
			_src = null;
			if (_audio != null)
				++_audver; //enforce browser to reload
			smartUpdate("src", new EncodedSrc());
		}
	}

	/** Returns the content set by {@link #setContent}.
	 * <p>Note: it won't fetch what is set thru by {@link #setSrc}.
	 * It simply returns what is passed to {@link #setContent}.
	 */
	public org.zkoss.sound.Audio getContent() {
		return _audio;
	}

	private List<String> getEncodedSrc() {
		final Desktop dt = getDesktop();
		List<String> list = new ArrayList<String>();
		if (_audio != null) {
			list.add(getAudioSrc());
		} else if (dt != null) {
			for (String src : _src) {
				list.add(dt.getExecution().encodeURL(src));
			}
		}
		return list;
	}

	/** Returns the encoded URL for the current audio content.
	 * Don't call this method unless _audio is not null;
	 */
	private String getAudioSrc() {
		return Utils.getDynamicMediaURI(this, _audver, _audio.getName(), _audio.getFormat());
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "src", getEncodedSrc());
		render(renderer, "autoplay", _autoplay);
		render(renderer, "preload", _preload);
		render(renderer, "controls", _controls);
		render(renderer, "loop", _loop);
		render(renderer, "muted", _muted);
	}

	@Override
	public void beforeChildAdded(Component child, Component insertBefore) {
		if (!(child instanceof Track))
			throw new UiException("Unsupported child for audio: " + child);
		super.beforeChildAdded(child, insertBefore);
	}

	// ComponentCtrl
	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}

	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements DynamicMedia {
		//-- DynamicMedia --//
		public Media getMedia(String pathInfo) {
			return _audio;
		}
	}

	private class EncodedSrc implements org.zkoss.zk.au.DeferredValue {
		public Object getValue() {
			return getEncodedSrc();
		}
	}
}
