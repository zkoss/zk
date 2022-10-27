/* IChildable.java

	Purpose:

	Description:

	History:
		11:49 AM 2021/10/1, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.immutable.StatelessOnly;

/**
 * An interface to indicate any child is allowed for {@link IComponent}.
 * @author jumperchen
 */
public interface IChildable<R, I extends IComponent> {

	/**
	 * Returns the children of this component.
	 */
	@StatelessOnly
	List<I> getChildren();

	/**
	 * Copy the current immutable object with elements that replace the content of {@link IChildable#getChildren() children}.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param elements An iterable of children elements to set
	 * @return A modified copy of {@code this} object
	 */
	R withChildren(Iterable<? extends I> elements);

	/**
	 * Copy the current immutable object with elements that replace the content of {@link IChildable#getChildren() children}.
	 * @param elements The children elements to set
	 * @return A modified copy of {@code this} object
	 */
	default R withChildren(I... elements) {
		return withChildren(Arrays.asList(elements));
	}

}
