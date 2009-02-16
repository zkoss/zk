/* FormatInputElement.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul  5 09:27:34     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
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
			smartUpdate("z.fmt", getFormat());

			try {
				smartUpdate("value", getText());
				//Yes, the value attribute is changed! (no format attr in client)
			} catch (WrongValueException ex) {
				//ignore it (safe because it will keep throwing exception)
			}
		}
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String fmt = getFormat();
		return fmt != null && fmt.length() != 0 ?
			attrs + " z.fmt=\""+fmt+'"': attrs;
	}
	/** Returns whether the server-side formatting shall take place.
	 *
	 * <p>It returns true if and only if {@link #getFormat} is not null.
	 * In other words, if the format is specified, ON_CHANGE is sent
	 * back immediately to the server to retrieve back the data
	 * in the correct format.
	 * @since 3.5.0
	 */
	protected boolean shallServerFormat() {
		return getFormat() != null;
	}
}
