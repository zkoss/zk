/* CheckedFunction.java

	Purpose:
		
	Description:
		
	History:
		5:02 PM 2021/10/7, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.function;

import java.util.Objects;

/**
 * Represents an operation that accepts single argument and returns single result.
 * @author jumperchen
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {
	R apply(T t) throws Throwable;

	default <V> CheckedFunction<T, V> andThen(
			CheckedFunction<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (T t) -> after.apply(apply(t));
	}
}