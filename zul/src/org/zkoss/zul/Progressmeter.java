/* Progressmeter.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 14 16:38:24     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.IOException;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.XulElement;

/**
 * A progress meter is a bar that indicates how much of a task has been completed. 
 *
 * <p>Default {@link #getZclass}: z-progressmeter. (since 3.5.0)
 *
 * @author tomyeh
 */
public class Progressmeter extends XulElement {
	private int _val;
	private boolean _resetWidth = true; //B80-ZK-2895

	public Progressmeter() {
		super.setWidth("100px");
	}
	public Progressmeter(int value) {
		this();
		setValue(value);
	}

	/** Overrides the method in HtmlBasedComponent, to avoid misuse hflex and width at the same time.
	 * @since 8.0.1
	 */
	@Override
	public void setWidth(String width) { //B80-ZK-2895
		_resetWidth = false;
		super.setWidth(width);
	}
	/** Overrides the method in HtmlBasedComponent, to avoid misuse hflex and width at the same time.
	 * @since 8.0.1
	 */
	@Override
	public void setHflex(String flex) { //B80-ZK-2895
		if (_resetWidth)
			super.setWidth("");
		super.setHflex(flex);
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
