/* CheckedConsumer4.java

	Purpose:
		
	Description:
		
	History:
		12:05 PM 2021/10/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.function;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an operation that accepts four arguments and returns no result.
 * @author jumperchen
 */
@FunctionalInterface
public interface CheckedConsumer4<A, B, C, D> extends Serializable {
	
	void accept(A a, B b, C c, D d) throws Throwable;
	
	default CheckedConsumer4<A, B, C, D> andThen(
			CheckedConsumer4<? super A, ? super B, ? super C, ? super D> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c, D d) -> { accept(a, b, c, d); after.accept(a, b, c, d); };
	}
}
