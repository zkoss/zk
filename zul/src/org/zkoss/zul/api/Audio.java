/* Audio.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 09:27:29     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.zk.ui.WrongValueException;

/**
 * An audio clip.
 * 
 * <p>
 * An extension to XUL.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Audio extends org.zkoss.zul.impl.api.XulElement {
	/**
	 * Plays the audio at the client.
	 */
	public void play();

	/**
	 * Stops the audio at the cient.
	 */
	public void stop();

	/**
	 * Pauses the audio at the cient.
	 */
	public void pause();

	/**
	 * Returns the alignment.
	 * <p>
	 * Default: null (use browser default).
	 */
	public String getAlign();

	/**
	 * Sets the alignment: one of top, texttop, middle, absmiddle, bottom,
	 * absbottom, baseline, left, right and center.
	 */
	public void setAlign(String align) throws WrongValueException;

	/**
	 * Returns the width of the border.
	 * <p>
	 * Default: null (use browser default).
	 */
	public String getBorder();

	/**
	 * Sets the width of the border.
	 */
	public void setBorder(String border) throws WrongValueException;

	/**
	 * Returns the src.
	 * <p>
	 * Default: null.
	 */
	public String getSrc();

	/**
	 * Sets the src.
	 * 
	 * <p>
	 * Calling this method implies setContent(null). In other words, the last
	 * invocation of {@link #setSrc} overrides the previous {@link #setContent},
	 * if any.
	 * 
	 * @see #setContent
	 */
	public void setSrc(String src);

	/**
	 * Sets the content directly.
	 * <p>
	 * Default: null.
	 * 
	 * <p>
	 * Calling this method implies setSrc(null). In other words, the last
	 * invocation of {@link #setContent} overrides the previous {@link #setSrc},
	 * if any.
	 * 
	 * @param audio
	 *            the audio to display.
	 * @see #setSrc
	 */
	public void setContent(org.zkoss.sound.Audio audio);

	/**
	 * Returns the content set by {@link #setContent}.
	 * <p>
	 * Note: it won't fetch what is set thru by {@link #setSrc}. It simply
	 * returns what is passed to {@link #setContent}.
	 */
	public org.zkoss.sound.Audio getContent();

}
