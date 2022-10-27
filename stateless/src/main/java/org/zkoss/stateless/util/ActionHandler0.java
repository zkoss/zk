/* ActionHandler0.java

	Purpose:
		
	Description:
		
	History:
		11:17 AM 2021/10/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.util;

import org.zkoss.stateless.function.CheckedRunnable;

/**
 * Represents an action handler with no arguments.
 * @author jumperchen
 */
@FunctionalInterface
public interface ActionHandler0 extends CheckedRunnable, ActionHandler {
	default int getParameterCount() {
		return 0;
	}
}