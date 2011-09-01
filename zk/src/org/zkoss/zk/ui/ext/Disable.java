/* Disable.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 28 14:15:40     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.ext;

/**
 * Implemented with {@link org.zkoss.zk.ui.Component} to indicate
 * that a component can be disabled.
 *
 * @author tomyeh
 * since 5.0.0
 */
public interface Disable {
	/** Returns whether it is disabled.
	 * <p>Default: false.
	 */
	public boolean isDisabled();
	/** Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled);
}
