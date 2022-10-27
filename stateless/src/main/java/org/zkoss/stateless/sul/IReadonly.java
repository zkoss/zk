/* IReadonly.java

	Purpose:

	Description:

	History:
		Wed Nov 03 17:06:04 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

/**
 * Immutable {@link org.zkoss.zk.ui.ext.Readonly} interface.
 * <p>Indicate a component can be set to readonly</p>
 * @author katherine
 */
public interface IReadonly<I extends IComponent> {

	/**
	 * Returns whether it is readonly.
	 * <p>
	 * Default: {@code false}.
	 */
	default boolean isReadonly() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code readonly}.
	 *
	 * <p>Sets whether it's readonly.
	 *
	 * @param readonly {@code true} means readonly.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withReadonly(boolean readonly);
}