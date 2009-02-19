/* Iframe.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

import org.zkoss.util.media.Media;

/**
 * Includes an inline frame.
 * 
 * <p>
 * Unlike HTML iframe, this component doesn't have the frameborder property.
 * Rather, use the CSS style to customize the border (like any other
 * components).
 * 
 * @author tomyeh
 * @see Include
 * @since 3.5.2
 */
public interface Iframe extends org.zkoss.zul.impl.api.XulElement {
	/**
	 * Define scroll bars
	 * 
	 * @param scrolling
	 *            "true", "false", "yes" or "no" or "auto", "auto" by default If
	 *            null, "auto" is assumed.
	 */
	public void setScrolling(String scrolling);

	/**
	 * Return the scroll bars.
	 * <p>
	 * Defalut: "auto"
	 * 
	 */
	public String getScrolling();

	/**
	 * Returns the alignment.
	 * <p>
	 * Default: null (use browser default).
	 */
	public String getAlign();

	/**
	 * Sets the alignment: one of top, middle, bottom, left, right and center.
	 */
	public void setAlign(String align);

	/**
	 * Returns the frame name.
	 * <p>
	 * Default: null (use browser default).
	 */
	public String getName();

	/**
	 * Sets the frame name.
	 */
	public void setName(String name);

	/**
	 * Returns whether to automatically hide this component if a popup or
	 * dropdown is overlapped with it.
	 * 
	 * <p>
	 * Default: false.
	 * 
	 * <p>
	 * If an iframe contains PDF or other embeds, it will be placed on top of
	 * other components. It may then make popups and dropdowns obscure. In this
	 * case, you have to specify autohide="true" to ask ZK to hide the iframe
	 * when popups or dropdowns is overlapped with the iframe.
	 */
	public boolean isAutohide();

	/**
	 * Sets whether to automatically hide this component if a popup or dropdown
	 * is overlapped with it.
	 */
	public void setAutohide(boolean autohide);

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
	 * @param src
	 *            the source URL. If null or empty, nothing is included.
	 * @see #setContent
	 */
	public void setSrc(String src);

	/**
	 * Sets the content directly. Default: null.
	 * 
	 * <p>
	 * Calling this method implies setSrc(null). In other words, the last
	 * invocation of {@link #setContent} overrides the previous {@link #setSrc},
	 * if any.
	 * 
	 * @param media
	 *            the media for this inline frame.
	 * @see #setSrc
	 */
	public void setContent(Media media);

	/**
	 * Returns the content set by {@link #setContent}.
	 * <p>
	 * Note: it won't fetch what is set thru by {@link #setSrc}. It simply
	 * returns what is passed to {@link #setContent}.
	 */
	public Media getContent();
}
