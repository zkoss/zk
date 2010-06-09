/* Progressmeter.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 14 16:38:24     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.xml.HTMLs;
import org.zkoss.xml.XMLs;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.HtmlPageRenders;
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
			smartUpdate("value", _val);
		}
	}
	/** Returns the current value of the progress meter.
	 */
	public int getValue() {
		return _val;
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-progressmeter" : _zclass;
	}

	//-- Component --//
	protected boolean isChildable() {
		return false;
	}
	
	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws IOException {
		super.renderProperties(renderer);
		render(renderer, "value", ""+_val);
		
	}
}
