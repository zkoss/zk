/* Progressmeter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug 14 16:38:24     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.XulElement;

/**
 * A progress meter is a bar that indicates how much of a task has been completed. 
 *
 * <p>Default {@link #getZclass}: z-progressmeter. (since 3.5.0)
 *
 * @author tomyeh
 */
public class Progressmeter extends XulElement implements org.zkoss.zul.api.Progressmeter {
	private int _val;

	public Progressmeter() {
		setWidth("100px");
	}
	public Progressmeter(int value) {
		this();
		setValue(value);
	}

	/** Sets the current value of the progress meter.
	 * <p>Range: 0~100.
	 */
	public void setValue(int value) {
		if (value < 0 || value > 100)
			throw new UiException("Illegal value: "+value+". Range: 0 ~ 100");
		if (_val != value) {
			_val = value;
			smartUpdate("z.value", _val);
		}
	}
	/** Returns the current value of the progress meter.
	 */
	public int getValue() {
		return _val;
	}

	/** Returns the style class of the SPAN tag that representing
	 * the progress status.
	 *
	 * <p>It is equivalent to "pmc-img", where pmc is assumed to be
	 * the return value of {@link #getSclass}.
	 * If {@link #getSclass} returns null, "progressmeter-img" is assumed.
	 *
	 * @since 3.0.1
	 * @deprecated As of release 3.5.0
	 */
	public String getIconSclass() {
		return null;
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-progressmeter" : _zclass;
	}

	//-- Component --//
	protected boolean isChildable() {
		return false;
	}
}
