/* CheckedConsumer2.java

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
 * Represents an operation that accepts two arguments and returns no result.
 * @author jumperchen
 */
@FunctionalInterface
public interface CheckedConsumer2<A, B> extends Serializable {
	
	void accept(A a, B b) throws Throwable;
	
	default CheckedConsumer2<A, B> andThen(
			CheckedConsumer2<? super A, ? super B> after) {
		Objects.requireNonNull(after);
		return (A a, B b) -> { accept(a, b); after.accept(a, b); };
	}
}
