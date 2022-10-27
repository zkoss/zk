/* CheckedConsumer6.java

	Purpose:
		
	Description:
		
	History:
		12:05 PM 2021/10/5, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.function;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an operation that accepts six arguments and returns no result.
 *
 * @author jumperchen
 */
@FunctionalInterface
public interface CheckedConsumer6<A, B, C, D, E, F> extends Serializable {

	void accept(A a, B b, C c, D d, E e, F f) throws Throwable;

	default CheckedConsumer6<A, B, C, D, E, F> andThen(
			CheckedConsumer6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c, D d, E e, F f) -> {
			accept(a, b, c, d, e, f);
			after.accept(a, b, c, d, e, f);
		};
	}
}
