/* LabelImageElement.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 12:09:00     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.awt.image.RenderedImage;

import org.zkoss.lang.Objects;
import org.zkoss.image.Images;
import org.zkoss.util.media.Media;
import org.zkoss.image.Image;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.DynamicMedia;

/**
 * A XUL element with a label ({@link #getLabel})and an image ({@link #getImage}).
 * 
 * @author tomyeh
 */
public class LabelImageElement extends LabelElement {
	private String _src;
	/** The image. _src and _image cannot be both non-null. */
	private Image _image;
	/** Count the version of {@link #_image}. */
	private byte _imgver;

	/** Returns the image URI.
	 * <p>Default: null.
	 * <p>The same as {@link #getSrc}.
	 */
	public String getImage() {
		return _src;
	}
	/** Sets the image URI.
	 * <p>Calling this method implies setImageContent(null).
	 * In other words, the last invocation of {@link #setImage} overrides
	 * the previous {@link #setImageContent}, if any.
	 * <p>If src is changed, the component's inner is invalidate.
	 * Thus, you want to smart-update, you have to override this method.
	 * <p>The same as {@link #setSrc}.
	 * @see #setImageContent(Image)
	 * @see #setImageContent(RenderedImage)
	 */
	public void setImage(String src) {
		if (src != null && src.length() == 0) src = null;
		if (_image != null || !Objects.equals(_src, src)) {
			_src = src;
			_image = null;
			invalidate();
		}
	}

	/** Returns the src (an image URI).
	 * <p>Default: null.
	 * <p>The same as {@link #getImage}.
	 */
	public String getSrc() {
		return getImage();
	}
	/** Sets the src (the image URI).
	 * <p>If src is changed, the component's inner is invalidate.
	 * Thus, you want to smart-update, you have to override this method.
	 * <p>The same as {@link #setImage}.
	 */
	public void setSrc(String src) {
		setImage(src);
	}

	/** Sets the content directly.
	 * <p>Default: null.
	 *
	 * <p>Calling this method implies setImage(null).
	 * In other words, the last invocation of {@link #setImageContent} overrides
	 * the previous {@link #setImage}, if any.
	 * @param image the image to display.
	 * @see #setImage
	 */
	public void setImageContent(Image image) {
		if (_src != null || image != _image) {
			_image = image;
			_src = null;
			if (_image != null) _imgver++; //enforce browser to reload image
			invalidate();
		}
	}
	/** Sets the content directly with the rendered image.
	 * It actually encodes the rendered image to an PNG image
	 * ({@link org.zkoss.image.Image}) with {@link Images#encode},
	 * and then invoke {@link #setImageContent(org.zkoss.image.Image)}.
	 *
	 * <p>If you want more control such as different format, quality,
	 * and naming, you can use {@link Images} directly.
	 *
	 * @since 3.0.7
	 */
	public void setImageContent(RenderedImage image) {
		try {
			setImageContent(Images.encode("a.png", image));
		} catch (java.io.IOException ex) {
			throw new UiException(ex);
		}
	}
	/** Returns the image content
	 * set by {@link #setImageContent(Image)}
	 * or {@link #setImageContent(RenderedImage)}.
	 *
	 * <p>Note: it won't load the content specified by {@link #setImage}.
	 * Actually, it returns null if {@link #setImage} was called.
	 */
	public Image getImageContent() {
		return _image;
	}

	/** Returns whether the image is available.
	 * In other words, it return true if {@link #setImage} or
	 * {@link #setImageContent(org.zkoss.image.Image)} is called with non-null.
	 */
	public boolean isImageAssigned() {
		return _src != null || _image != null;
	}
	/** Returns the HTML IMG tag for the image part, or null
	 * if no image is assigned ({@link #isImageAssigned})
	 *
	 * <p>Used only for component template, not for application developers.
	 *
	 * <p>Note: the component template shall use this method to
	 * generate the HTML tag, instead of using {@link #getImage}.
	 */
	public String getImgTag() {
		if (_src == null && _image == null)
			return null;

		final StringBuffer sb = new StringBuffer(64)
			.append("<img src=\"")
			.append(_image != null ? getContentSrc(): //already encoded
				getDesktop().getExecution().encodeURL(_src))
			.append("\" align=\"absmiddle\"/>");

		final String label = getLabel();
		if (label != null && label.length() > 0) sb.append(' ');

		return sb.toString(); //keep a space
	}
	/** Returns the encoded URL for the current image content.
	 * Don't call this method unless _image is not null;
	 *
	 * <p>Used only for component template, not for application developers.
	 */
	private String getContentSrc() {
		return Utils.getDynamicMediaURI(
			this, _imgver, _image.getName(), _image.getFormat());
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends LabelElement.ExtraCtrl
	implements DynamicMedia {
		//-- DynamicMedia --//
		public Media getMedia(String pathInfo) {
			return _image;
		}
	}
}
