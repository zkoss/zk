/* CheckedFunction2.java

	Purpose:
		
	Description:
		
	History:
		2:44 PM 2021/10/19, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.function;

import java.util.Objects;

/**
 * Represents an operation that accepts two arguments and returns single result.
 * @author jumperchen
 */
@FunctionalInterface
public interface CheckedFunction2<A, B, R> {
	R apply(A a, B b) throws Throwable;

	default <V> CheckedFunction2<A, B, V> andThen(
			CheckedFunction<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (A a, B b) -> after.apply(apply(a, b));
	}
}