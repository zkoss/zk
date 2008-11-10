/* Progressmeter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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

	/**
	 * Returns the style class of the SPAN tag that representing the progress
	 * status.
	 * 
	 * <p>
	 * It is equivalent to "pmc-img", where pmc is assumed to be the return
	 * value of {@link #getSclass}. If {@link #getSclass} returns null,
	 * "progressmeter-img" is assumed.
	 * 
	 * @deprecated As of release 3.5.0
	 */
	public String getIconSclass();

}
