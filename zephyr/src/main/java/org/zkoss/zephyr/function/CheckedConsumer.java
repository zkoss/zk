/* CheckedConsumer1.java

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
 * Represents an operation that accepts single argument and returns no result.
 * @author jumperchen
 */
@FunctionalInterface
public interface CheckedConsumer<A> extends Serializable {
	
	void accept(A a) throws Throwable;
	
	default CheckedConsumer<A> andThen(
			CheckedConsumer<? super A> after) {
		Objects.requireNonNull(after);
		return (A a) -> { accept(a); after.accept(a); };
	}
}