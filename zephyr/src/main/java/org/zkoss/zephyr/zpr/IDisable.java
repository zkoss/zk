/* IDisable.java

	Purpose:

	Description:

	History:
		Fri Oct 15 09:54:45 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

/**
 * Immutable {@link org.zkoss.zk.ui.ext.Disable} interface
 *
 * <p>Implemented with {@link IComponent} to indicate
 * that a component can be disabled.</p>
 *
 * @author katherine
 */
public interface IDisable<I extends IComponent> {

	/**
	 * Returns whether to disable the {@link I}'s control.
	 * <p>Default: {@code false}</p>
	 */
	default boolean isDisabled() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code disabled}.
	 *
	 * <p>Sets whether to disable the {@link I}'s control or not.
	 *
	 * @param disabled Whether to disable the {@link I}'s control or not.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withDisabled(boolean disabled);
}