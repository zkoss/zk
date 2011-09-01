/* Img.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 20 15:13:32     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.action.html;

import java.io.IOException;

import org.zkoss.web.mesg.MWeb;
import org.zkoss.web.servlet.dsp.DspException;
import org.zkoss.web.servlet.dsp.action.AbstractAction;
import org.zkoss.web.servlet.dsp.action.ActionContext;

/**
 * Generates the HTML &lt;img&gt; tag
 *
 * @author tomyeh
 */
public class Img extends AbstractAction {
	private String _id, _onclick;
	private String _src, _height, _width, _border, _hspace, _vspace;
	private String _align, _title, _alt, _sclass, _style;

	/** Returns the id attribute.
	 * Default: null.
	 */
	public String getId() {
		return _id;
	}
	/** Sets the id attribute.
	 */
	public void setId(String id) {
		_id = id;
	}
	/** Returns the src (URL).
	 * Default: null.
	 */
	public String getSrc() {
		return _src;
	}
	/** Sets the src (URL).
	 */
	public void setSrc(String src) {
		_src = src;
	}
	/** Returns the alignment.
	 * Default: null.
	 */
	public String getAlign() {
		return _align;
	}
	/** Sets the alignment.
	 */
	public void setAlign(String align) {
		_align = align;
	}
	/** Returns the alt.
	 * Default: null.
	 */
	public String getAlt() {
		return _alt;
	}
	/** Sets the alt.
	 */
	public void setAlt(String alt) {
		_alt = alt;
	}
	/** Returns the border.
	 * Default: null.
	 */
	public String getBorder() {
		return _border;
	}
	/** Sets the border.
	 */
	public void setBorder(String border) {
		_border = border;
	}
	/** Returns the hspace.
	 * Default: null.
	 */
	public String getHspace() {
		return _hspace;
	}
	/** Sets the hspace.
	 */
	public void setHspace(String hspace) {
		_hspace = hspace;
	}
	/** Returns the vspace.
	 * Default: null.
	 */
	public String getVspace() {
		return _vspace;
	}
	/** Sets the vspace.
	 */
	public void setVspace(String vspace) {
		_vspace = vspace;
	}
	/** Returns the style class.
	 * Default: null.
	 */
	public String getSclass() {
		return _sclass;
	}
	/** Sets the style class.
	 */
	public void setSclass(String sclass) {
		_sclass = sclass;
	}
	/** Returns the style.
	 * Default: null.
	 */
	public String getStyle() {
		return _style;
	}
	/** Sets the style.
	 */
	public void setStyle(String style) {
		_style = style;
	}
	/** Returns the height.
	 * Default: null.
	 */
	public String getHeight() {
		return _height;
	}
	/** Sets the height.
	 */
	public void setHeight(String height) {
		_height = height;
	}
	/** Returns the width.
	 * Default: null.
	 */
	public String getWidth() {
		return _width;
	}
	/** Sets the width.
	 */
	public void setWidth(String width) {
		_width = width;
	}

	/** Returns the title.
	 * Default: null (no title at all).
	 */
	public String getTitle() {
		return _title;
	}
	/** Sets the title.
	 */
	public void setTitle(String title) {
		_title = title;
	}

	/** Returns the onclick.
	 * Default: null.
	 */
	public String getOnclick() {
		return _onclick;
	}
	/** Sets the onclick.
	 */
	public void setOnclick(String onclick) {
		_onclick = onclick;
	}

	//-- Action --//
	public void render(ActionContext ac, boolean nested)
	throws DspException, IOException {
		if (!isEffective()|| _src == null || _src.length() == 0)
			return; //nothing to generate
		if (nested)
			throw new DspException(MWeb.DSP_NESTED_ACTION_NOT_ALLOWED,
				new Object[] {this, new Integer(ac.getLineNumber())});

		final StringBuffer sb = new StringBuffer(64)
			.append("<img src=\"").append(ac.encodeURL(_src)).append('"');
		append(sb, "id", _id);
		append(sb, "height", _height);
		append(sb, "width", _width);
		append(sb, "title", _title);
		append(sb, "align", _align);
		append(sb, "alt", _alt);
		append(sb, "class", _sclass);
		append(sb, "style", _style);
		append(sb, "border", _border);
		append(sb, "hspace", _hspace);
		append(sb, "vspace", _vspace);
		append(sb, "onclick", _onclick);
		sb.append("/>");
		ac.getOut().write(sb.toString());
	}

	//-- Object --//
	public String toString() {
		return "image";
	}
}
