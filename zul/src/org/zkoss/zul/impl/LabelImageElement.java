/* LabelImageElement.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 12:09:00     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
public class LabelImageElement extends LabelElement {
	private String _src;
	/** The image. If not null, _src is generated automatically. */
	private Image _image;
	/** Count the version of {@link #_image} and {@link #_hoverimg}.
	 * Odd for normal image, while even for hover image.
	 */
	private short _imgver;
	/** The hover image's src. */
	private String _hoversrc;
	/** The hover image. If not null, _hoversrc is generated automatically. */
	private Image _hoverimg;

	/** Returns the image URI.
	 * <p>Default: null.
	 * <p>The same as {@link #getSrc}.
	 */
	public String getImage() {
		return _src;
	}
	/** Sets the image URI.
	 * <p>If src is changed, the component's inner is invalidate.
	 * Thus, you want to smart-update, you have to override this method.
	 * <p>The same as {@link #setSrc}.
	 */
	public void setImage(String src) {
		if (src != null && src.length() == 0) src = null;
		if (!Objects.equals(_src, src)) {
			_src = src;
			if (_image == null) invalidate();

			//_src is meaningful only if _image is null
			//NOTE: Tom Yeh: 20051222
			//It is possible to use smartUpdate if we always generate
			//an image (with an ID) in getImgTag.
			//However, it is too costly by making HTML too big, so
			//we prefer to invalidate (it happens rarely)
		}
	}
	protected void setSrcDirectly(String src) {
		_src = src;
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
	 * @param image the image to display. If not null, it has higher
	 * priority than {@link #getSrc}.
	 */
	public void setImageContent(Image image) {
		if (image != _image) {
			_image = image;
			if (_image != null) _imgver += 2; //enforce browser to reload image
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
	/** Returns the content set by {@link #setImageContent(org.zkoss.image.Image)}.
	 * <p>Note: it won't fetch what is set thru by {@link #setSrc}.
	 * It simply returns what is passed to {@link #setImageContent(org.zkoss.image.Image)}.
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
	 * @since 3.5.0
	 */
	public void setHoverImage(String src) {
		if (src != null && src.length() == 0) src = null;
		if (!Objects.equals(_hoversrc, src)) {
			_hoversrc = src;
			if (_hoverimg == null)
				smartUpdateDeferred("z.hvig", new EncodedHoverSrc());
		}
	}
	/** Sets the content of the hover image directly.
	 * The hover image is used when the mouse is moving over this component.
	 * <p>Default: null.
	 *
	 * @param image the image to display. If not null, it has higher
	 * priority than {@link #getSrc}.
	 * @since 3.5.0
	 */
	public void setHoverImageContent(Image image) {
		if (image != _hoverimg) {
			_hoverimg = image;
			if (_hoverimg != null) _imgver += 2; //enforce browser to reload image
			smartUpdateDeferred("z.hvig", new EncodedHoverSrc());
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
			.append("\" align=\"absmiddle\" id=\"")
			.append(getUuid()).append("!hvig\"/>");

		final String label = getLabel();
		if (label != null && label.length() > 0)
			sb.append(' ');
		return sb.toString(); //keep a space
	}
	/** Returns the encoded URL for the current image content.
	 * Don't call this method unless _image is not null;
	 *
	 * <p>Used only for component template, not for application developers.
	 * @since 3.5.0 (public)
	 */
	public String getContentSrc() {
		return Utils.getDynamicMediaURI(
			this, _imgver, _image.getName(), _image.getFormat());
	}
	/** Returns the encoded src ({@link #getSrc}).
	 */
	private String getEncodedHoverSrc() {
		if (_hoverimg != null)
			return Utils.getDynamicMediaURI(
				this, _imgver + 1, //odd
				_hoverimg.getName(), _hoverimg.getFormat());

		final Desktop dt = getDesktop(); //it might not belong to any desktop
		return dt != null && _hoversrc != null ?
			dt.getExecution().encodeURL(_hoversrc): null;
	}

	//super//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		if (_hoversrc != null || _hoverimg != null)
			return attrs + " z.hvig=\"" + getEncodedHoverSrc() +'"';
		return attrs;
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
					try {
						int v = Integer.parseInt(pathInfo.substring(1, j));
						if ((v & 1) == 1)
							return _hoverimg;
					} catch (Throwable ex) { //ingore it
					}
				}
			}
			return _image;
		}
	}
	private class EncodedHoverSrc implements org.zkoss.zk.ui.util.DeferredValue {
		public String getValue() {
			return getEncodedHoverSrc();
		}
	}
}
