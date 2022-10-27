/* CheckedConsumer1.java

	Purpose:
		
	Description:
		
	History:
		12:05 PM 2021/10/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.function;

import java.io.Serializable;

/**
 * Represents an operation that accepts no arguments and returns no result.
 * @author jumperchen
 */
@FunctionalInterface
public interface CheckedRunnable extends Serializable {
	
	void run() throws Throwable;
}