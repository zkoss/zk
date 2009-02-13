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
 * <p>Note: IE 5.5/6 (not 7) has a bug that failed to render PNG with
 * alpha transparency. See http://homepage.ntlworld.com/bobosola/index.htm for details.
 * Thus, if you want to display such image, you have to use the alphafix mold.
 * <code>&lt;image mold="alphafix"/&gt;</code>
 *
 * @author tomyeh
 */
public class Image extends XulElement {
	private String _align, _border, _hspace, _vspace;
	private String _src;
	/** The image. _src and _image cannot be nonnull at the same time.  */
	private org.zkoss.image.Image _image;
	/** Count the version of {@link #_image}. */
	private byte _imgver;

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
			smartUpdateDeferred("src", new EncodedSrc()); //Bug 1850895
		}
	}
	/** Returns the encoded src ({@link #getSrc}).
	 */
	private String getEncodedSrc() {
		final Desktop dt = getDesktop(); //it might not belong to any desktop
		return _image != null ? getContentSrc(): //already encoded
			dt != null ? dt.getExecution().encodeURL(
				_src != null ? _src: "~./img/spacer.gif"): "";
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
			smartUpdateDeferred("src", new EncodedSrc()); //Bug 1850895
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

	/** Returns the encoded URL for the current image content.
	 * Don't call this method unless _image is not null;
	 *
	 * <p>Used only for component template, not for application developers.
	 */
	private String getContentSrc() {
		return Utils.getDynamicMediaURI(
			this, _imgver, _image.getName(), _image.getFormat());
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String clkattrs = getAllOnClickAttrs();
		if (!alphafix())
			return clkattrs == null ? attrs: attrs + clkattrs;

		//Request 1522329
		final StringBuffer sb = new StringBuffer(64).append(attrs);
		if (clkattrs != null) sb.append(clkattrs);
		sb.append(" zk_alpha=\"true\"");
		return sb.toString();
	}
	/** Tests whether to apply Request 1522329.
	 * To limit the side effect, enable it only if mold is alphafix (and IE6).
	 */
	private boolean alphafix() {
		if ("alphafix".equals(getMold())) {
			final Desktop dt = getDesktop();
			if (dt != null) {
				final Execution exec = dt.getExecution();
				return exec != null && exec.isExplorer() && !exec.isExplorer7();
			}
		}
		return false;		
	}
	public String getInnerAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getInnerAttrs());
		HTMLs.appendAttribute(sb, "align",  _align);
		HTMLs.appendAttribute(sb, "border",  _border);
		HTMLs.appendAttribute(sb, "hspace",  _hspace);
		HTMLs.appendAttribute(sb, "vspace",  _vspace);
		HTMLs.appendAttribute(sb, "src",  getEncodedSrc());
		return sb.toString();
	}

	//-- Component --//
	/** Default: not childable.
	 */
	public boolean isChildable() {
		return false;
	}
	public void smartUpdate(String attr, String value) {
		//Request 1522329: to simplify the client, we always invalidate if alphafix
		if (alphafix()) invalidate();
		else super.smartUpdate(attr, value);
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
			return _image;
		}
	}

	private class EncodedSrc implements org.zkoss.zk.ui.util.DeferredValue {
		public String getValue() {
			return getEncodedSrc();
		}
	}
}
