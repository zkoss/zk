/* IGridComposite.java

	Purpose:

	Description:

	History:
		Tue Dec 28 17:55:40 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

/**
 * Represents a composition of {@link IComponent} onto {@link IGrid}.
 *
 * @author katherine
 */
public interface IGridComposite<I extends IGridComposite> extends IMeshComposite<I> {
}