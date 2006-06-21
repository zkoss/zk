/* Image.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jul 18 20:57:18     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

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
import com.potix.zk.ui.ext.Viewable;

import com.potix.zul.html.impl.XulElement;

/**
 * An image.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Image extends XulElement implements Viewable {
	private String _align, _border, _hspace, _vspace;
	private String _src;
	/** The image. If not null, _src is generated automatically. */
	private com.potix.image.Image _image;
	/** Count the version of {@link #_image}. */
	private int _imgver;

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
			if (_image == null) smartUpdate("src", getEncodedSrc());
				//_src is meaningful only if _image is null
		}
	}
	/** Returns the encoded src ({@link #getSrc}).
	 */
	private String getEncodedSrc() {
		return _image != null ? getContentSrc(): //already encoded
			getDesktop().getExecution().encodeURL(
				_src != null ? _src: "~./img/spacer.gif");
	}

	/** Sets the content directly.
	 * Default: null.
	 *
	 * @param image the image to display. If not null, it has higher
	 * priority than {@link #getSrc}.
	 */
	public void setContent(com.potix.image.Image image) {
		if (image != _image) {
			_image = image;
			if (_image != null) ++_imgver; //enforce browser to reload image
			smartUpdate("src", getEncodedSrc());
		}
	}
	/** Returns the content set by {@link #setContent}.
	 * <p>Note: it won't fetch what is set thru by {@link #setSrc}.
	 * It simply returns what is passed to {@link #setContent}.
	 */
	public com.potix.image.Image getContent() {
		return _image;
	}

	/** Returns the encoded URL for the current image content.
	 * Don't call this method unless _image is not null;
	 */
	private String getContentSrc() {
		if (getDesktop() == null) return ""; //no avail at client

		final StringBuffer sb = new StringBuffer(64).append('/');
		Strings.encode(sb, _imgver);
		final String name = _image.getName();
		final String format = _image.getFormat();
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
		return _image;
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String clkattrs = getAllOnClickAttrs(false);
		return clkattrs == null ? attrs: attrs + clkattrs;
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
}
