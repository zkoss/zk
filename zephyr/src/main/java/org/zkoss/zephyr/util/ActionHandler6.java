/* ActionHandler6.java

	Purpose:
		
	Description:
		
	History:
		11:17 AM 2021/10/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.util;

import org.zkoss.zephyr.function.CheckedConsumer6;

/**
 * Represents an action handler with six arguments.
 * @author jumperchen
 */
@FunctionalInterface
public interface ActionHandler6<A, B, C, D, E, F> extends
		CheckedConsumer6<A, B, C, D, E, F>, ActionHandler {
	default int getParameterCount() {
		return 6;
	}
}