/* ActionHandler5.java

	Purpose:
		
	Description:
		
	History:
		11:17 AM 2021/10/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.util;

import org.zkoss.zephyr.function.CheckedConsumer5;

/**
 * Represents an action handler with five arguments.
 * @author jumperchen
 */
@FunctionalInterface
public interface ActionHandler5<A, B, C, D, E> extends
		CheckedConsumer5<A, B, C, D, E>, ActionHandler {
	default int getParameterCount() {
		return 5;
	}
}