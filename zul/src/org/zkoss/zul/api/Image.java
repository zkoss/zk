/* Image.java

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

import java.awt.image.RenderedImage;

import org.zkoss.image.Images;//for javadoc

/**
 * An image.
 * 
 * <p>
 * Note: IE 5.5/6 (not 7) has a bug that failed to render PNG with alpha
 * transparency. See http://homepage.ntlworld.com/bobosola/index.htm for
 * details. Thus, if you want to display such image, you have to use the
 * alphafix mold. <code><image mold="alphafix"/></code>
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Image extends org.zkoss.zul.impl.api.XulElement {

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
	public void setAlign(String align);

	/**
	 * Returns the width of the border.
	 * <p>
	 * Default: null (use browser default).
	 */
	public String getBorder();

	/**
	 * Sets the width of the border.
	 */
	public void setBorder(String border);

	/**
	 * Returns number of pixels of extra space to the left and right side of the
	 * image.
	 * <p>
	 * Default: null (use browser default).
	 */
	public String getHspace();

	/**
	 * Sets number of pixels of extra space to the left and right side of the
	 * image.
	 */
	public void setHspace(String hspace);

	/**
	 * Returns number of pixels of extra space to the top and bottom side of the
	 * image.
	 * <p>
	 * Default: null (use browser default).
	 */
	public String getVspace();

	/**
	 * Sets number of pixels of extra space to the top and bottom side of the
	 * image.
	 */
	public void setVspace(String vspace);

	/**
	 * Returns the source URI of the image.
	 * <p>
	 * Default: null.
	 */
	public String getSrc();

	/**
	 * Sets the source URI of the image.
	 * 
	 * <p>
	 * Calling this method implies setContent(null). In other words, the last
	 * invocation of {@link #setSrc} overrides the previous {@link #setContent},
	 * if any.
	 * 
	 * @param src
	 *            the URI of the image source
	 * @see #setContent(org.zkoss.image.Image)
	 * @see #setContent(RenderedImage)
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
	 * @param image
	 *            the image to display.
	 * @see #setSrc
	 */
	public void setContent(org.zkoss.image.Image image);

	/**
	 * Sets the content directly with the rendered image. It actually encodes
	 * the rendered image to an PNG image ({@link org.zkoss.image.Image}) with
	 * {@link Images#encode}, and then invoke
	 * {@link #setContent(org.zkoss.image.Image)}.
	 * 
	 * <p>
	 * If you want more control such as different format, quality, and naming,
	 * you can use {@link Images} directly.
	 * 
	 */
	public void setContent(RenderedImage image);

	/**
	 * Returns the content set by {@link #setContent(org.zkoss.image.Image)}.
	 * <p>
	 * Note: it won't fetch what is set thru by {@link #setSrc}. It simply
	 * returns what is passed to {@link #setContent(org.zkoss.image.Image)}.
	 */
	public org.zkoss.image.Image getContent();

	/**
	 * Returns the URI of the hover image. The hover image is used when the
	 * mouse is moving over this component.
	 * <p>
	 * Default: null.
	 * 
	 */
	public String getHover();

	/**
	 * Sets the image URI. The hover image is used when the mouse is moving over
	 * this component.
	 * <p>
	 * Calling this method implies setHoverContent(null). In other words, the
	 * last invocation of {@link #setHover} overrides the previous
	 * {@link #setHoverContent}, if any.
	 * 
	 * @see #setHoverContent(org.zkoss.image.Image)
	 * @see #setHoverContent(RenderedImage)
	 */
	public void setHover(String src);

	/**
	 * Sets the content of the hover image directly. The hover image is used
	 * when the mouse is moving over this component.
	 * <p>
	 * Default: null.
	 * 
	 * <p>
	 * Calling this method implies setHover(null). In other words, the last
	 * invocation of {@link #setHoverContent} overrides the previous
	 * {@link #setHover}, if any.
	 * 
	 * @param image
	 *            the image to display.
	 * @see #setHover
	 */
	public void setHoverContent(org.zkoss.image.Image image);

	/**
	 * Sets the content of the hover image directly with the rendered image. The
	 * hover image is used when the mouse is moving over this component.
	 * 
	 * <p>
	 * It actually encodes the rendered image to an PNG image (
	 * {@link org.zkoss.image.Image}) with {@link Images#encode}, and then
	 * invoke {@link #setHoverContent(org.zkoss.image.Image)}.
	 * 
	 * <p>
	 * If you want more control such as different format, quality, and naming,
	 * you can use {@link Images} directly.
	 * 
	 */
	public void setHoverContent(RenderedImage image);

}
