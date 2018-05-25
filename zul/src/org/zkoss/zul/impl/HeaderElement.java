/* HeaderElement.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan 11 11:55:13     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import org.zkoss.lang.Objects;

/**
 * A skeletal implementation for a header.
 *
 * @author tomyeh
 */
public abstract class HeaderElement extends LabelImageElement {
	private String _align, _valign, _width;

	protected HeaderElement() {
	}

	/** @since 5.0.0
	 */
	protected HeaderElement(String label) {
		super(label);
	}

	/** @since 5.0.0
	 */
	protected HeaderElement(String label, String image) {
		super(label, image);
	}

	/** Returns the horizontal alignment of this column.
	 * <p>Default: null (system default: left unless CSS specified).
	 */
	public String getAlign() {
		return _align;
	}

	/** Sets the horizontal alignment of this column.
	 */
	public void setAlign(String align) {
		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}

	/** Returns the vertical alignment of this grid.
	 * <p>Default: null (system default: top).
	 */
	public String getValign() {
		return _valign;
	}

	/** Sets the vertical alignment of this grid.
	 */
	public void setValign(String valign) {
		if (!Objects.equals(_valign, valign)) {
			_valign = valign;
			smartUpdate("valign", _valign);
		}
	}

	void setWidthByClient(String width) {
		// reset hflex min first; for Bug ZK-2772 Misaligned Grid columns
		setHflexByClient("false");
		setWidthDirectly(width);
	}

	//feature#3177275: Listheader should override hflex when sized by end user
	void setHflexByClient(String hflex) {
		setHflexDirectly(hflex);
	}

	// ZK-3791: width="%" acts differently in various browsers. Use hflex would be better.
	public void setWidth(String width) {
		if (!Objects.equals(_width, width)) {
			_width = width;
			if (width != null && width.endsWith("%")) {
				super.setWidth0(null);
				setHflex0(width.substring(0, width.length() - 1));
			} else {
				super.setWidth(width);
				setHflex0(null);
			}
		}
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		render(renderer, "valign", _valign);
		render(renderer, "align", _align);
	}
}
