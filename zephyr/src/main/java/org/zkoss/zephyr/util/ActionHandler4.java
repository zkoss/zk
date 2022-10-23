/* ActionHandler4.java

	Purpose:
		
	Description:
		
	History:
		11:17 AM 2021/10/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.util;

import org.zkoss.zephyr.function.CheckedConsumer4;

/**
 * Represents an action handler with four arguments.
 * @author jumperchen
 */
@FunctionalInterface
public interface ActionHandler4<A, B, C, D> extends
		CheckedConsumer4<A, B, C, D>, ActionHandler {
	default int getParameterCount() {
		return 4;
	}
}