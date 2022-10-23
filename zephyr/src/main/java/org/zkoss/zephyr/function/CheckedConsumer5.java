/* CheckedConsumer5.java

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
 * Represents an operation that accepts five arguments and returns no result.
 * @author jumperchen
 */
@FunctionalInterface
public interface CheckedConsumer5<A, B, C, D, E> extends Serializable {
	
	void accept(A a, B b, C c, D d, E e) throws Throwable;
	
	default CheckedConsumer5<A, B, C, D, E> andThen(
			CheckedConsumer5<? super A, ? super B, ? super C, ? super D, ? super E> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c, D d, E e) -> { accept(a, b, c, d, e); after.accept(a, b, c, d, e); };
	}
}
