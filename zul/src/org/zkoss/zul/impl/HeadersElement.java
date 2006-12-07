/* HeadersElement.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec  7 09:43:48     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
abstract public class HeadersElement extends XulElement {
	private boolean _sizeable;

	/** Returns whether the width of the child column is sizeable.
	 */
	public boolean isSizeable() {
		return _sizeable;
	}
	/** Sets whether the width of the child column is sizeable.
	 * If true, an user can drag the border between two columns ({@link Column})
	 * to change the widths of adjacent columns.
	 * <p>Default: false.
	 */
	public void setSizeable(boolean sizeable) {
		if (_sizeable != sizeable) {
			_sizeable = sizeable;
			smartUpdate("z.sizeable", sizeable);
		}
	}

	//super//
	public String getOuterAttrs() {
		StringBuffer sb = _sizeable ? new StringBuffer(80): null;
		sb = appendAsapAttr(sb, ZulEvents.ON_COL_SIZE);

		final String attrs = super.getOuterAttrs();
		if (sb == null) return attrs;

		sb.append(attrs);
		if (_sizeable)
			sb.append(" z.sizeable=\"true\"");
		return sb.toString();
	}
}
