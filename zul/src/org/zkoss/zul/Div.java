/* Div.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Dec 30 17:49:49     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.zul.impl.XulElement;

/**
 * The same as HTML DIV tag.
 *
 * <p>An extension. It has the same effect as
 * <code>&lt;h:div xmlns:h="http://www.w3.org/1999/xhtml"&gt;</code>.
 * Note: a {@link Window} without title and caption has the same visual effect
 * as {@link Div}, but {@link Div} doesn't implement IdSpace.
 * In other words, {@link Div} won't affect the uniqueness of identifiers.
 *
 * @author tomyeh
 */
public class Div extends XulElement {
	private String _align;

	/** Returns the alignment.
	 * <p>Default: null (use browser default).
	 */
	public String getAlign() {
		return _align;
	}
	/** Sets the alignment: one of left, center, right, ustify,
	 */
	public void setAlign(String align) {
		if (align != null && align.length() == 0)
			align = null;

		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final String clkattrs = getAllOnClickAttrs();
		final String attrs = super.getOuterAttrs();
		if (_align == null && clkattrs == null)
			return attrs;

		final StringBuffer sb = new StringBuffer(80).append(attrs);
		HTMLs.appendAttribute(sb, "align",  _align);
		if (clkattrs != null) sb.append(clkattrs);
		return sb.toString();
	}
}
