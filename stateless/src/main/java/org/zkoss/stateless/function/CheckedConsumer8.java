/* CheckedConsumer8.java

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
 * Represents an operation that accepts eight arguments and returns no result.
 *
 * @author jumperchen
 */
@FunctionalInterface
public interface CheckedConsumer8<A, B, C, D, E, F, G, H> extends Serializable {

	void accept(A a, B b, C c, D d, E e, F f, G g, H h) throws Throwable;

	default CheckedConsumer8<A, B, C, D, E, F, G, H> andThen(
			CheckedConsumer8<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c, D d, E e, F f, G g, H h) -> {
			accept(a, b, c, d, e, f, g, h);
			after.accept(a, b, c, d, e, f, g, h);
		};
	}
}
