/* HeadersElement.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec  7 09:43:48     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import org.zkoss.zul.event.ZulEvents;

/**
 * A skeletal implementation for headers, the parent of
 * a group of {@link HeaderElement}.
 *
 * @author tomyeh
 */
abstract public class HeadersElement extends XulElement implements org.zkoss.zul.impl.api.HeadersElement{
	private boolean _sizable;

	/** Returns whether the width of the child column is sizable.
	 */
	public boolean isSizable() {
		return _sizable;
	}
	/** Sets whether the width of the child column is sizable.
	 * If true, an user can drag the border between two columns (e.g., {@link org.zkoss.zul.Column})
	 * to change the widths of adjacent columns.
	 * <p>Default: false.
	 */
	public void setSizable(boolean sizable) {
		if (_sizable != sizable) {
			_sizable = sizable;
			smartUpdate("z.sizable", sizable);
		}
	}

	//super//
	public String getOuterAttrs() {
		StringBuffer sb = _sizable ? new StringBuffer(80): null;
		sb = appendAsapAttr(sb, ZulEvents.ON_COL_SIZE);

		final String attrs = super.getOuterAttrs();
		if (sb == null) return attrs;

		sb.append(attrs);
		if (_sizable)
			sb.append(" z.sizable=\"true\"");
		return sb.toString();
	}
}
