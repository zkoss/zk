/* Progressmeter.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * A progress meter is a bar that indicates how much of a task has been
 * completed.
 * 
 * <p>
 * Default {@link #getZclass}: z-progressmeter. (since 3.5.0)
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Progressmeter extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Sets the current value of the progress meter.
	 * <p>
	 * Range: 0~100.
	 */
	public void setValue(int value);

	/**
	 * Returns the current value of the progress meter.
	 */
	public int getValue();

}
