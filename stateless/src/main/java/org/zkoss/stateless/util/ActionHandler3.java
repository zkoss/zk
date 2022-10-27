/* ActionHandler3.java

	Purpose:
		
	Description:
		
	History:
		11:17 AM 2021/10/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.util;

import org.zkoss.stateless.function.CheckedConsumer3;

/**
 * Represents an action handler with three arguments.
 * @author jumperchen
 */
@FunctionalInterface
public interface ActionHandler3<A, B, C> extends CheckedConsumer3<A, B, C>, ActionHandler {
	default int getParameterCount() {
		return 3;
	}
}