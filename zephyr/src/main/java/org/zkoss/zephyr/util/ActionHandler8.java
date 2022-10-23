/* ActionHandler8.java

	Purpose:
		
	Description:
		
	History:
		11:17 AM 2021/10/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.util;

import org.zkoss.zephyr.function.CheckedConsumer8;

/**
 * Represents an action handler with eight arguments.
 * @author jumperchen
 */
@FunctionalInterface
public interface ActionHandler8<A, B, C, D, E, F, G, H> extends
		CheckedConsumer8<A, B, C, D, E, F, G, H>, ActionHandler {
	default int getParameterCount() {
		return 8;
	}
}