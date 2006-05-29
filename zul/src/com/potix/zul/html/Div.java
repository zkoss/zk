/* Div.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Dec 30 17:49:49     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.lang.Objects;
import com.potix.xml.HTMLs;
import com.potix.zul.html.impl.XulElement;

/**
 * The same as HTML DIV tag.
 *
 * <p>An extension. It has the same effect as
 * <code>&lt;h:div xmlns:h="http://www.w3.org/1999/xhtml"&gt;</code>.
 * Note: a {@link Window} without title and caption has the same visual effect
 * as {@link Div}, but {@link Div} doesn't implement IdSpace.
 * In other words, {@link Div} won't affect the uniqueness of identifiers.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/05/29 04:28:22 $
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
		final String attrs = super.getOuterAttrs();
		final boolean bOnClick = isAsapRequired("onClick");
		if (_align == null && !bOnClick)
			return attrs;

		final StringBuffer sb = new StringBuffer(64).append(attrs);
		HTMLs.appendAttribute(sb, "align",  _align);
		if (bOnClick)
			sb.append(" zk_onClick=\"true\" zk_type=\"zul.html.widget.Div\"");
		return sb.toString();
	}

	//-- Component --//
	public void smartUpdate(String attr, String value) {
		//We have to ask the client to re-initialize it (to observe onclick)
		if ("zk_onClick".equals(attr) && "true".equals(value))
			invalidate(OUTER);
		else
			super.smartUpdate(attr, value);
	}
}
