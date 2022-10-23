/* ActionHandler7.java

	Purpose:
		
	Description:
		
	History:
		11:17 AM 2021/10/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.util;

import org.zkoss.zephyr.function.CheckedConsumer7;

/**
 * Represents an action handler with seven arguments.
 * @author jumperchen
 */
@FunctionalInterface
public interface ActionHandler7<A, B, C, D, E, F, G> extends
		CheckedConsumer7<A, B, C, D, E, F, G>, ActionHandler {
	default int getParameterCount() {
		return 7;
	}
}