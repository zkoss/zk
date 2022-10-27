/* CheckedConsumer3.java

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
 * Represents an operation that accepts three arguments and returns no result.
 * @author jumperchen
 */
@FunctionalInterface
public interface CheckedConsumer3<A, B, C> extends Serializable {
	
	void accept(A a, B b, C c) throws Throwable;
	
	default CheckedConsumer3<A, B, C> andThen(
			CheckedConsumer3<? super A, ? super B, ? super C> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c) -> { accept(a, b, c); after.accept(a, b, c); };
	}
}
