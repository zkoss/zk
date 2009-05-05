/* Groupfoot.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * GroupFoot serves as a summary row of group.
 * 
 * <p>
 * Default {@link #getZclass}: z-groupfoot (since 5.0.0)
 * 
 *<p>
 * Note: All the child of this component are automatically applied the
 * group-cell CSS, if you don't want this CSS, you can invoke the
 * {@link Label#setSclass(String)} after the child added.
 * 
 * @author robbiecheng
 * @since 3.5.0
 */
public interface Groupfoot extends Row {
	/**
	 * Returns the value of the {@link Label} it contains, or null if no such
	 * cell.
	 */
	public String getLabel();

	/**
	 * Sets the value of the {@link Label} it contains.
	 * 
	 * <p>
	 * If it is not created, we automatically create it.
	 */
	public void setLabel(String label);

}
