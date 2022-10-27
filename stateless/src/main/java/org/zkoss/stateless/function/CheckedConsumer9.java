/* CheckedConsumer9.java

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
public interface CheckedConsumer9<A, B, C, D, E, F, G, H, I>
		extends Serializable {

	void accept(A a, B b, C c, D d, E e, F f, G g, H h, I i) throws Throwable;

	default CheckedConsumer9<A, B, C, D, E, F, G, H, I> andThen(
			CheckedConsumer9<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? super G, ? super H, ? super I> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c, D d, E e, F f, G g, H h, I i) -> {
			accept(a, b, c, d, e, f, g, h, i);
			after.accept(a, b, c, d, e, f, g, h, i);
		};
	}
}
