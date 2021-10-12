/* Readonly.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 12 15:25:07 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.ext;

/**
 * Implemented with {@link org.zkoss.zk.ui.Component} to indicate
 * that a component can be set to readonly.
 * 
 * @author tomyeh
 * @since 5.0.7
 */
public interface Readonly {
	/**
	 * Returns whether it is readonly.
	 * <p>
	 * Default: false.
	 */
	public boolean isReadonly();

	/**
	 * Sets whether it is readonly.
	 */
	public void setReadonly(boolean readonly);
}
