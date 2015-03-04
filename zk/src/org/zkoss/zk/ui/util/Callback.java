/* Callback.java

	Purpose:
		
	Description:
		
	History:
		Wed, Feb 25, 2015 11:21:22 AM, Created by jumperchen

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.util;

import java.io.Serializable;

/**
 * A callback interface to execute in some circumstance.
 * @author jumperchen
 * @since 7.0.5
 */
public interface Callback extends Serializable {
	/**
	 * Executes the callback method.
	 */
	public void call();
}
