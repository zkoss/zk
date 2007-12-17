/* Audio.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Nov 16 11:48:27     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.util.media.Media;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zk.au.out.AuInvoke;

import org.zkoss.zul.impl.XulElement;

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
	/** The audio. If not null, _src is generated automatically. */
	private org.zkoss.sound.Audio _audio;
	/** Count the version of {@link #_audio}. */
	private int _audver;
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
	 * <p>If {@link #setContent} is ever called with non-null,
	 * it takes heigher priority than this method.
	 */
	public void setSrc(String src) {
		if (src != null && src.length() == 0)
			src = null;

		if (!Objects.equals(_src, src)) {
			_src = src;
			if (_audio == null) updateSrc();
				//_src is meaningful only if _audio is null
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
	 * Default: null.
	 *
	 * @param audio the audio to display. If not null, it has higher
	 * priority than {@link #getSrc}.
	 */
	public void setContent(org.zkoss.sound.Audio audio) {
		if (audio != _audio) {
			_audio = audio;
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
		final Desktop desktop = getDesktop();
		if (desktop == null) return ""; //no avail at client

		final StringBuffer sb = new StringBuffer(64).append('/');
		Strings.encode(sb, _audver);
		final String name = _audio.getName();
		final String format = _audio.getFormat();
		if (name != null || format != null) {
			sb.append('/');
			boolean bExtRequired = true;
			if (name != null && name.length() != 0) {
				sb.append(name);
				bExtRequired = name.lastIndexOf('.') < 0;
			} else {
				sb.append(getId());
			}
			if (bExtRequired && format != null)
				sb.append('.').append(format);
		}
		return desktop.getDynamicMediaURI(this, sb.toString()); //already encoded
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
