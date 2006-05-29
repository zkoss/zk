/* HeaderElement.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jan 11 11:55:13     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html.impl;

import com.potix.lang.Objects;
import com.potix.xml.HTMLs;
import com.potix.zk.ui.UiException;

/**
 * A skeletal implementation for headers.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.9 $ $Date: 2006/05/29 04:28:29 $
 */
abstract public class HeaderElement extends LabelImageElement {
	private String _align, _valign;

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
			invalidateWhole();
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
			invalidateWhole();
		}
	}
	/** Called when the definition is changed.
	 * <p>Derived must override it to either do nothing, or invalidate
	 * parent or others.
	 */
	abstract protected void invalidateWhole();

	/** Returns the attributes used to generate HTML TD tag for each
	 * cell of the rows contained in the parent control,
	 * e.g., {@link com.potix.zul.html.Listcell}.
	 * <p>Used by component developers.
	 */
	public String getColAttrs() {
		final StringBuffer sb = new StringBuffer(32);
		HTMLs.appendAttribute(sb, "align", _align);
		HTMLs.appendAttribute(sb, "valign", _valign);
		HTMLs.appendAttribute(sb, "width", getWidth());
		return sb.toString();
	}

	//-- super --//
	public String getOuterAttrs() {
		return super.getOuterAttrs() + getColAttrs();
	}

	//-- Component --//
	/** Children are not allowed. */
	public boolean isChildable() {
		return false;
	}
}
