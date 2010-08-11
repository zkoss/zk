/* FormatInputElement.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul  5 09:27:34     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;

/**
 * A skeletal implementation for an input box with format.
 *
 * @author tomyeh
 */
abstract public class FormatInputElement extends InputElement {
	private String _format;

	/** Returns the format.
	 * <p>Default: null (used what is defined in the format sheet).
	 */
	public String getFormat() {
		return _format;
	}
	/** Sets the format.
	 */
	public void setFormat(String format) throws WrongValueException {
		if (!Objects.equals(_format, format)) {
			final String old = _format;
			_format = format;
			smartUpdate("format", _format);
			//bug #2998196: Problem with dynamic setting of format pattern
			smartUpdate("value", this.coerceToString(_value));
		}
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		Object old = _value;
		try {
			_value = null;
			super.renderProperties(renderer);
		} finally {
			_value = old;
		}

		render(renderer, "format", _format);
		render(renderer, "value", this.coerceToString(_value));
	}
}
