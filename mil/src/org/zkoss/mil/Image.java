/* Image.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 22, 2007 6:42:15 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Desktop;

/**
 * The Image item component that can be layout under the {@link Frame}.
 * @author henrichen
 */
public class Image extends Item {
	private static final long serialVersionUID = 200705231321L;
	
	private String _src;
	private org.zkoss.image.Image _image;
	private int _imgver;
	
	/** Returns the source URI of the image.
	 * <p>Default: null.
	 */
	public String getSrc() {
		return _src;
	}
	/** Sets the source URI of the image.
	 *
	 * <p>If {@link #setContent} is ever called with non-null,
	 * it takes heigher priority than this method.
	 *
	 * @param src the URI of the image source
	 */
	public void setSrc(String src) {
		if (src != null && src.length() == 0)
			src = null;

		if (!Objects.equals(_src, src)) {
			_src = src;
			if (_image == null) smartUpdate("im", getEncodedSrc());
				//_src is meaningful only if _image is null
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
	 * Default: null.
	 *
	 * @param image the image to display. If not null, it has higher
	 * priority than {@link #getSrc}.
	 */
	public void setContent(org.zkoss.image.Image image) {
		if (image != _image) {
			_image = image;
			if (_image != null) ++_imgver; //enforce browser to reload image
			smartUpdate("im", getEncodedSrc());
		}
	}
	/** Returns the content set by {@link #setContent}.
	 * <p>Note: it won't fetch what is set thru by {@link #setSrc}.
	 * It simply returns what is passed to {@link #setContent}.
	 */
	public org.zkoss.image.Image getContent() {
		return _image;
	}
	
	public String getInnerAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getInnerAttrs());
		HTMLs.appendAttribute(sb, "im",  getEncodedSrc());
		return sb.toString();
	}


	/** Returns the encoded URL for the current image content.
	 * Don't call this method unless _image is not null;
	 *
	 * <p>Used only for component template, not for application developers.
	 */
	private String getContentSrc() {
		final Desktop desktop = getDesktop();
		if (desktop == null) return ""; //no avail at client

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
		return desktop.getDynamicMediaURI(this, sb.toString()); //already encoded
	}
}
