/** AggregationListener.java.

	Purpose:
		
	Description:
		
	History:
		4:11:31 PM Feb 3, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.util;

/**
 * A listener to gather some non-predefined listeners 
 * @author jumperchen
 * @since 8.0.0
 */
public interface AggregationListener {

	/**
	 * Returns whether the given class is handled.
	 */
	public boolean isHandled(Class<?> klass);
}
