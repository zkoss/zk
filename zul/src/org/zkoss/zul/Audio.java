/* Audio.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Nov 16 11:48:27     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.util.media.Media;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zk.au.out.AuInvoke;

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
	private String _align, _border;
	protected String _src;
	/** The audio. _src and _audio cannot be nonnull at the same time. */
	private org.zkoss.sound.Audio _audio;
	/** Count the version of {@link #_audio}. */
	private byte _audver;
	private boolean _autostart, _loop;

	public Audio() {
	}
	public Audio(String src) {
		setSrc(src);
	}

	/** Plays the audio at the client.
	 */
	public void play() {
		response("ctrl", new AuInvoke(this, "play"));
	}
	/** Stops the audio at the cient.
	 */
	public void stop() {
		response("ctrl", new AuInvoke(this, "stop"));
	}
	/** Pauses the audio at the cient.
	 */
	public void pause() {
		response("ctrl", new AuInvoke(this, "pause"));
	}

	/** Returns the alignment.
	 * <p>Default: null (use browser default).
	 */
	public String getAlign() {
		return _align;
	}
	/** Sets the alignment: one of top, texttop, middle, absmiddle,
	 * bottom, absbottom, baseline, left, right and center.
	 */
	public void setAlign(String align) throws WrongValueException {
		if (align != null && align.length() == 0)
			align = null;

		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}
	/** Returns the width of the border.
	 * <p>Default: null (use browser default).
	 */
	public String getBorder() {
		return _border;
	}
	/** Sets the width of the border.
	 */
	public void setBorder(String border) throws WrongValueException {
		if (border != null && border.length() == 0)
			border = null;

		if (!Objects.equals(_border, border)) {
			_border = border;
			smartUpdate("border", _border);
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
	 * @see #setContent
	 */
	public void setSrc(String src) {
		if (src != null && src.length() == 0)
			src = null;

		if (_audio != null || !Objects.equals(_src, src)) {
			_src = src;
			_audio = null;
			updateSrc();
		}
	}
	private String getEncodedSrc() {
		final Desktop dt = getDesktop();
		return _audio != null ? getAudioSrc(): //already encoded
			dt != null ? dt.getExecution().encodeURL(
				_src != null ? _src: "~./aud/mute.mid"): "";
	}

	/** Returns whether to auto start playing the audio.
	 *
	 * <p>Default: false;
	 */
	public final boolean isAutostart() {
		return _autostart;
	}
	/** Sets whether to auto start playing the audio.
	 */
	public final void setAutostart(boolean autostart) {
		if (_autostart != autostart) {
			_autostart = autostart;
			smartUpdate("autostart", _autostart);
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
		if (_src != null || audio != _audio) {
			_audio = audio;
			_src = null;
			if (_audio != null) ++_audver; //enforce browser to reload
			updateSrc();
		}
	}
	private void updateSrc() {
		invalidate();
			//IE won't work if we only change the src attribute
	}
	/** Returns the content set by {@link #setContent}.
	 * <p>Note: it won't fetch what is set thru by {@link #setSrc}.
	 * It simply returns what is passed to {@link #setContent}.
	 */
	public org.zkoss.sound.Audio getContent() {
		return _audio;
	}

	/** Returns the encoded URL for the current audio content.
	 * Don't call this method unless _audio is not null;
	 */
	private String getAudioSrc() {
		return Utils.getDynamicMediaURI(
			this, _audver, _audio.getName(), _audio.getFormat());
	}

	//-- super --//
	public String getOuterAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getOuterAttrs());
		HTMLs.appendAttribute(sb, "src",  getEncodedSrc());
		HTMLs.appendAttribute(sb, "autostart",  _autostart);
		HTMLs.appendAttribute(sb, "loop",  _loop);
		HTMLs.appendAttribute(sb, "align",  _align);
		HTMLs.appendAttribute(sb, "border",  _border);
		sb.append(" z.autohide=\"true\"");
		return sb.toString();
	}

	//-- Component --//
	/** Default: not childable.
	 */
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
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements DynamicMedia {
		//-- DynamicMedia --//
		public Media getMedia(String pathInfo) {
			return _audio;
		}
	}
}
