/* Image.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jul 18 20:57:18     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.awt.image.RenderedImage;

import org.zkoss.lang.Objects;
import org.zkoss.image.Images;
import org.zkoss.util.media.Media;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.DynamicMedia;

import org.zkoss.zul.impl.XulElement;
import org.zkoss.zul.impl.Utils;

/**
 * An image.
 *
 * <p>See also <a href="http://docs.zkoss.org/wiki/ZK/How-Tos/Concepts-and-Tricks#How_to_fix_the_alpha_transparency_problem_of_PNG_files_found_in_IE6.3F">how to fix the alpha transparency problem of PNG files found in IE6?</a>
 * @author tomyeh
 */
public class Image extends XulElement implements org.zkoss.zul.api.Image{
	private String _align, _border, _hspace, _vspace;
	private String _src;
	/** The image. _src and _image cannot be nonnull at the same time.  */
	private org.zkoss.image.Image _image;
	/** The hover image's src. */
	private String _hoversrc;
	/** The hover image. */
	private org.zkoss.image.Image _hoverimg;
	/** Count the version of {@link #_image}. */
	private byte _imgver;
	/** Count the version of {@link #_hoverimg}. */
	private byte _hoverimgver;

	public Image() {
	}
	public Image(String src) {
		setSrc(src);
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
	public void setAlign(String align) {
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
	public void setBorder(String border) {
		if (border != null && border.length() == 0)
			border = null;

		if (!Objects.equals(_border, border)) {
			_border = border;
			smartUpdate("border", _border);
		}
	}
	/** Returns number of pixels of extra space to the left and right
	 * side of the image.
	 * <p>Default: null (use browser default).
	 */
	public String getHspace() {
		return _hspace;
	}
	/** Sets number of pixels of extra space to the left and right
	 * side of the image.
	 */
	public void setHspace(String hspace) {
		if (hspace != null && hspace.length() == 0)
			hspace = null;

		if (!Objects.equals(_hspace, hspace)) {
			_hspace = hspace;
			smartUpdate("hspace", _hspace);
		}
	}
	/** Returns number of pixels of extra space to the top and bottom
	 * side of the image.
	 * <p>Default: null (use browser default).
	 */
	public String getVspace() {
		return _vspace;
	}
	/** Sets number of pixels of extra space to the top and bottom
	 * side of the image.
	 */
	public void setVspace(String vspace) {
		if (vspace != null && vspace.length() == 0)
			vspace = null;

		if (!Objects.equals(_vspace, vspace)) {
			_vspace = vspace;
			smartUpdate("vspace", _vspace);
		}
	}

	/** Returns the source URI of the image.
	 * <p>Default: null.
	 */
	public String getSrc() {
		return _src;
	}
	/** Sets the source URI of the image.
	 *
	 * <p>Calling this method implies setContent(null).
	 * In other words, the last invocation of {@link #setSrc} overrides
	 * the previous {@link #setContent}, if any.
	 *
	 * @param src the URI of the image source
	 * @see #setContent(org.zkoss.image.Image)
	 * @see #setContent(RenderedImage)
	 */
	public void setSrc(String src) {
		if (src != null && src.length() == 0)
			src = null;

		if (_image != null || !Objects.equals(_src, src)) {
			_src = src;
			_image = null;
			smartUpdate("src", new EncodedURL()); //Bug 1850895
		}
	}
	/** Sets the content directly.
	 * <p>Default: null.
	 *
	 * <p>Calling this method implies setSrc(null).
	 * In other words, the last invocation of {@link #setContent} overrides
	 * the previous {@link #setSrc}, if any.
	 *
	 * @param image the image to display.
	 * @see #setSrc
	 */
	public void setContent(org.zkoss.image.Image image) {
		if (_src != null || image != _image) {
			_image = image;
			_src = null;
			if (_image != null) ++_imgver; //enforce browser to reload image
			smartUpdate("src", new EncodedURL()); //Bug 1850895
		}
	}
	/** Sets the content directly with the rendered image.
	 * It actually encodes the rendered image to an PNG image
	 * ({@link org.zkoss.image.Image}) with {@link Images#encode},
	 * and then invoke {@link #setContent(org.zkoss.image.Image)}.
	 *
	 * <p>If you want more control such as different format, quality,
	 * and naming, you can use {@link Images} directly.
	 *
	 * @since 3.0.7
	 */
	public void setContent(RenderedImage image) {
		try {
			setContent(Images.encode("a.png", image));
		} catch (java.io.IOException ex) {
			throw new UiException(ex);
		}
	}
	/** Returns the content set by {@link #setContent(org.zkoss.image.Image)}.
	 * <p>Note: it won't fetch what is set thru by {@link #setSrc}.
	 * It simply returns what is passed to {@link #setContent(org.zkoss.image.Image)}.
	 */
	public org.zkoss.image.Image getContent() {
		return _image;
	}

	/** Returns the encoded URL of the image (never null).
	 */
	private String getEncodedURL() {
		if (_image != null)
			return Utils.getDynamicMediaURI( //already encoded
				this, _imgver, "c/" + _image.getName(), _image.getFormat());

		final Desktop dt = getDesktop(); //it might not belong to any desktop
		return dt != null ? dt.getExecution()
			.encodeURL(_src != null ? _src: "~./img/spacer.gif"): "";
	}
	/** Returns the encoded URL of the hover image, or null if no hover image.
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

	/** Returns the URI of the hover image.
	 * The hover image is used when the mouse is moving over this component.
	 * <p>Default: null.
	 * @since 3.5.0
	 */
	public String getHover() {
		return _hoversrc;
	}
	/** Sets the image URI.
	 * The hover image is used when the mouse is moving over this component.
	 * <p>Calling this method implies setHoverContent(null).
	 * In other words, the last invocation of {@link #setHover} overrides
	 * the previous {@link #setHoverContent}, if any.
	 * @since 3.5.0
	 * @see #setHoverContent(org.zkoss.image.Image)
	 * @see #setHoverContent(RenderedImage)
	 */
	public void setHover(String src) {
		if (src != null && src.length() == 0) src = null;
		if (_hoverimg != null || !Objects.equals(_hoversrc, src)) {
			_hoversrc = src;
			_hoverimg = null;
			smartUpdate("hover", new EncodedHoverURL());
		}
	}
	/** Sets the content of the hover image directly.
	 * The hover image is used when the mouse is moving over this component.
	 * <p>Default: null.
	 *
	 * <p>Calling this method implies setHover(null).
	 * In other words, the last invocation of {@link #setHoverContent} overrides
	 * the previous {@link #setHover}, if any.
	 * @param image the image to display.
	 * @since 3.5.0
	 * @see #setHover
	 */
	public void setHoverContent(org.zkoss.image.Image image) {
		if (_hoversrc != null || image != _hoverimg) {
			_hoverimg = image;
			_hoversrc = null;
			if (_hoverimg != null) _hoverimgver++; //enforce browser to reload image
			smartUpdate("hover", new EncodedHoverURL());
		}
	}
	/** Sets the content of the hover image directly with the rendered image.
	 * The hover image is used when the mouse is moving over this component.
	 *
	 * <p>It actually encodes the rendered image to an PNG image
	 * ({@link org.zkoss.image.Image}) with {@link Images#encode},
	 * and then invoke {@link #setHoverContent(org.zkoss.image.Image)}.
	 *
	 * <p>If you want more control such as different format, quality,
	 * and naming, you can use {@link Images} directly.
	 * @since 3.5.0
	 */
	public void setHoverContent(RenderedImage image) {
		try {
			setHoverContent(Images.encode("hover.png", image));
		} catch (java.io.IOException ex) {
			throw new UiException(ex);
		}
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "src", getEncodedURL());
		if (_hoversrc != null || _hoverimg != null)
			render(renderer, "hover", getEncodedHoverURL());
		render(renderer, "align", _align);
		render(renderer, "border", _border);
		render(renderer, "hspace", _hspace);
		render(renderer, "vspace", _vspace);
	}


	//-- Component --//
	/** Default: not childable.
	 */
	protected boolean isChildable() {
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

	private class EncodedURL implements org.zkoss.zk.ui.util.DeferredValue {
		public Object getValue() {
			return getEncodedURL();
		}
	}
	private class EncodedHoverURL implements org.zkoss.zk.ui.util.DeferredValue {
		public Object getValue() {
			return getEncodedHoverURL();
		}
	}
}
