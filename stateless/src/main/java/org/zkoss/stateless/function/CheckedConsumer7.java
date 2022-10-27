/* CheckedConsumer7.java

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
 * Represents an operation that accepts seven arguments and returns no result.
 *
 * @author jumperchen
 */
@FunctionalInterface
public interface CheckedConsumer7<A, B, C, D, E, F, G> extends Serializable {

	void accept(A a, B b, C c, D d, E e, F f, G g) throws Throwable;

	default CheckedConsumer7<A, B, C, D, E, F, G> andThen(
			CheckedConsumer7<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c, D d, E e, F f, G g) -> {
			accept(a, b, c, d, e, f, g);
			after.accept(a, b, c, d, e, f, g);
		};
	}
}
