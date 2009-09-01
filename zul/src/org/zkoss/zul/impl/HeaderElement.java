/* HeaderElement.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jan 11 11:55:13     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.UiException;

/**
 * A skeletal implementation for a header.
 *
 * @author tomyeh
 */
abstract public class HeaderElement extends LabelImageElement implements org.zkoss.zul.impl.api.HeaderElement{
	private String _align, _valign;

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
		_width = width;
	}
	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		render(renderer, "valign", _valign);
		render(renderer, "align", _align);
	}
}
