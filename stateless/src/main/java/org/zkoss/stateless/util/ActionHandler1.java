/* ActionHandler1.java

	Purpose:
		
	Description:
		
	History:
		11:17 AM 2021/10/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.util;

import org.zkoss.stateless.function.CheckedConsumer;

/**
 * Represents an action handler with one argument.
 * @author jumperchen
 */
@FunctionalInterface
public interface ActionHandler1<A> extends CheckedConsumer<A>, ActionHandler {
	default int getParameterCount() {
		return 1;
	}
}