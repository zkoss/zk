/* LabelImageElement.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 12:09:00     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.awt.image.RenderedImage;

import org.zkoss.lang.Objects;
import org.zkoss.image.Images;
import org.zkoss.util.media.Media;
import org.zkoss.image.Image;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.DynamicMedia;

/**
 * A XUL element with a label ({@link #getLabel}) 
 * and an image ({@link #getImage}).
 * 
 * @author tomyeh
 */
abstract public class LabelImageElement extends LabelElement implements org.zkoss.zul.impl.api.LabelImageElement{
	private String _src;
	/** The image. _src and _image cannot be both non-null. */
	private Image _image;
	/** The hover image's src. */
	private String _hoversrc;
	/** The hover image. */
	private Image _hoverimg;
	/** Count the version of {@link #_image}. */
	private byte _imgver;
	/** Count the version of {@link #_hoverimg}. */
	private byte _hoverimgver;

	protected LabelImageElement() {
	}
	/** @since 5.0.0
	 */
	protected LabelImageElement(String label, String image) {
		setLabel(label);
		setImage(image);
	}
	/** @since 5.0.0
	 */
	protected LabelImageElement(String label) {
		setLabel(label);
	}

	/** Returns the image URI.
	 * <p>Default: null.
	 */
	public String getImage() {
		return _src;
	}
	/** Sets the image URI.
	 * <p>Calling this method implies setImageContent(null).
	 * In other words, the last invocation of {@link #setImage} overrides
	 * the previous {@link #setImageContent}, if any.
	 * @see #setImageContent(Image)
	 * @see #setImageContent(RenderedImage)
	 */
	public void setImage(String src) {
		if (src != null && src.length() == 0) src = null;
		if (_image != null || !Objects.equals(_src, src)) {
			_src = src;
			_image = null;
			smartUpdate("image", new EncodedImageURL());
		}
	}
	/** @deprecated As of release 3.5.0, it is redudant since
	 * it is the same as {@link #getImage}
	 */
	public String getSrc() {
		return getImage();
	}
	/** @deprecated As of release 3.5.0, it is redudant since
	 * it is the same as {@link #setImage}
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
			smartUpdate("image", new EncodedImageURL());
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

	/** Returns the URI of the hover image.
	 * The hover image is used when the mouse is moving over this component.
	 * <p>Default: null.
	 * @since 3.5.0
	 */
	public String getHoverImage() {
		return _hoversrc;
	}
	/** Sets the image URI.
	 * The hover image is used when the mouse is moving over this component.
	 * <p>Calling this method implies setHoverImageContent(null).
	 * In other words, the last invocation of {@link #setHoverImage} overrides
	 * the previous {@link #setHoverImageContent}, if any.
	 * @since 3.5.0
	 */
	public void setHoverImage(String src) {
		if (src != null && src.length() == 0) src = null;
		if (_hoverimg != null || !Objects.equals(_hoversrc, src)) {
			_hoversrc = src;
			_hoverimg = null;
			smartUpdate("hoverImage", new EncodedHoverURL());
		}
	}
	/** Sets the content of the hover image directly.
	 * The hover image is used when the mouse is moving over this component.
	 * <p>Default: null.
	 *
	 * <p>Calling this method implies setHoverImage(null).
	 * In other words, the last invocation of {@link #setHoverImageContent} overrides
	 * the previous {@link #setHoverImage}, if any.
	 * @param image the image to display.
	 * @since 3.5.0
	 */
	public void setHoverImageContent(Image image) {
		if (_hoversrc != null || image != _hoverimg) {
			_hoverimg = image;
			_hoversrc = null;
			if (_hoverimg != null) _hoverimgver++; //enforce browser to reload image
			smartUpdate("hoverImage", new EncodedHoverURL());
		}
	}
	/** Sets the content of the hover image directly with the rendered image.
	 * The hover image is used when the mouse is moving over this component.
	 *
	 * <p>It actually encodes the rendered image to an PNG image
	 * ({@link org.zkoss.image.Image}) with {@link Images#encode},
	 * and then invoke {@link #setHoverImageContent(org.zkoss.image.Image)}.
	 *
	 * <p>If you want more control such as different format, quality,
	 * and naming, you can use {@link Images} directly.
	 * @since 3.5.0
	 */
	public void setHoverImageContent(RenderedImage image) {
		try {
			setHoverImageContent(Images.encode("hover.png", image));
		} catch (java.io.IOException ex) {
			throw new UiException(ex);
		}
	}

	/** Returns whether the image is available.
	 * In other words, it return true if {@link #setImage} or
	 * {@link #setImageContent(org.zkoss.image.Image)} is called with non-null.
	 */
	public boolean isImageAssigned() {
		return _src != null || _image != null;
	}
	/** Returns the encoded URL for the image ({@link #getImage}
	 * or {@link #getImageContent}), or null if no image.
	 * <p>Used only for component developements; not by app developers.
	 * <p>Note: this method can be invoked only if execution is not null.
	 */
	private String getEncodedImageURL() {
		if (_image != null)
			return Utils.getDynamicMediaURI( //already encoded
				this, _imgver, "c/" + _image.getName(), _image.getFormat());

		final Desktop dt = getDesktop(); //it might not belong to any desktop
		return dt != null && _src != null ?
			dt.getExecution().encodeURL(_src): null;
	}
	/** Returns the encoded URL for the hover image or null if not
	 * available.
	 */
	private String getEncodedHoverURL() {
		if (_hoverimg != null)
			return Utils.getDynamicMediaURI(
				this, _hoverimgver,
				"h/" + _hoverimg.getName(), _hoverimg.getFormat());

		final Desktop dt = getDesktop(); //it might not belong to any desktop
		return dt != null && _hoversrc != null ?
			dt.getExecution().encodeURL(_hoversrc): null;
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "image", getEncodedImageURL());
		render(renderer, "hoverImage", getEncodedHoverURL());
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
			if (pathInfo != null) {
				int j = pathInfo.indexOf('/', 1);
				if (j >= 0) {
					int k = pathInfo.indexOf('/', ++j);
					if (k == j + 1 && pathInfo.charAt(j) == 'h')
						return _hoverimg;
				}
			}
			return _image;
		}
	}
	private class EncodedImageURL implements org.zkoss.zk.ui.util.DeferredValue {
		public Object getValue() {
			return getEncodedImageURL();
		}
	}
	private class EncodedHoverURL implements org.zkoss.zk.ui.util.DeferredValue {
		public Object getValue() {
			return getEncodedHoverURL();
		}
	}
}
