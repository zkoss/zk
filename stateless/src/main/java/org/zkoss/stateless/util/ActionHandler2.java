/* ActionHandler2.java

	Purpose:
		
	Description:
		
	History:
		11:17 AM 2021/10/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.util;

import org.zkoss.stateless.function.CheckedConsumer2;

/**
 * Represents an action handler with two arguments.
 * @author jumperchen
 */
@FunctionalInterface
public interface ActionHandler2<A, B> extends CheckedConsumer2<A, B>, ActionHandler {
	default int getParameterCount() {
		return 2;
	}
}