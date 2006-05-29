/* Audio.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Nov 16 11:48:27     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.lang.Objects;
import com.potix.lang.Strings;
import com.potix.util.media.Media;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.Execution;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.WrongValueException;
import com.potix.zk.ui.ext.Viewable;
import com.potix.zk.au.AuScript;

import com.potix.zul.html.impl.XulElement;

/**
 * An audio clip.
 *
 * <p>An extension to XUL.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.16 $ $Date: 2006/05/29 04:28:20 $
 */
public class Audio extends XulElement implements Viewable {
	private String _align, _border;
	protected String _src;
	/** The audio. If not null, _src is generated automatically. */
	private com.potix.sound.Audio _audio;
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
		response("ctrl",
			new AuScript(this, "zkAudio.play('"+getUuid()+"')"));
	}
	/** Stops the audio at the cient.
	 */
	public void stop() {
		response("ctrl",
			new AuScript(this, "zkAudio.stop('"+getUuid()+"')"));
	}
	/** Pauses the audio at the cient.
	 */
	public void pause() {
		response("ctrl",
			new AuScript(this, "zkAudio.pause('"+getUuid()+"')"));
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
		return _audio != null ? getAudioSrc(): //already encoded
			getDesktop().getExecution().encodeURL(
				_src != null ? _src: "~./aud/mute.mid");
	}

	/** Returns whether to auto start playing the audio.
	 *
	 * <p>Default: false;
	 */
	public final boolean getAutostart() {
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
	public void setContent(com.potix.sound.Audio audio) {
		if (audio != _audio) {
			_audio = audio;
			if (_audio != null) ++_audver; //enforce browser to reload
			updateSrc();
		}
	}
	private void updateSrc() {
		invalidate(OUTER);
		//IE won't work if we only change the src attribute
		//smartUpdate("src", getEncodedSrc());
	}
	/** Returns the content set by {@link #setContent}.
	 * <p>Note: it won't fetch what is set thru by {@link #setSrc}.
	 * It simply returns what is passed to {@link #setContent}.
	 */
	public com.potix.sound.Audio getContent() {
		return _audio;
	}

	/** Returns the encoded URL for the current audio content.
	 * Don't call this method unless _audio is not null;
	 */
	private String getAudioSrc() {
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
		return getViewURI(sb.toString()); //already encoded
	}

	//-- Viewable --//
	public Media getView(String pathInfo) {
		return _audio;
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
}
