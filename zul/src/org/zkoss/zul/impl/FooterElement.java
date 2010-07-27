/* FooterElement.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 27 09:18:22 TST 2010, Created by jimmy

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul.impl;

import org.zkoss.lang.Objects;

/**
 * A skeletal implementation for a footer.
 * @author jimmy
 */
abstract public class FooterElement extends LabelImageElement implements org.zkoss.zul.impl.api.HeaderElement{
	private String _align, _valign;

	protected FooterElement() {
	}
	/** @since 5.0.4
	 */
	protected FooterElement(String label) {
		super(label);
	}
	/** @since 5.0.4
	 */
	protected FooterElement(String label, String image) {
		super(label, image);
	}

	/** Returns the horizontal alignment of this footer.
	 * <p>Default: null (system default: left unless CSS specified).
	 */
	public String getAlign() {
		return _align;
	}
	/** Sets the horizontal alignment of this footer.
	 */
	public void setAlign(String align) {
		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}
	/** Returns the vertical alignment of this footer.
	 * <p>Default: null (system default: top).
	 */
	public String getValign() {
		return _valign;
	}
	/** Sets the vertical alignment of this footer.
	 */
	public void setValign(String valign) {
		if (!Objects.equals(_valign, valign)) {
			_valign = valign;
			smartUpdate("valign", _valign);
		}
	}
	void setWidthByClient(String width) {
		setWidthDirectly(width);
	}
	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		render(renderer, "valign", _valign);
		render(renderer, "align", _align);
	}
}

