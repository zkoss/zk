/* CheckedFunction3.java

	Purpose:
		
	Description:
		
	History:
		6:08 PM 2022/4/25, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.function;

import java.util.Objects;

/**
 * Represents an operation that accepts three arguments and returns single result.
 * @author jumperchen
 */
public interface CheckedFunction3<A, B, C, R> {
	R apply(A a, B b, C c) throws Throwable;

	default <V> CheckedFunction3<A, B, C, V> andThen(
			CheckedFunction<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c) -> after.apply(apply(a, b, c));
	}
}