/* ISelector.java

	Purpose:
		
	Description:
		
	History:
		4:13 PM 2022/8/18, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui;

import java.util.List;

import org.zkoss.zephyr.zpr.IComponent;

/**
 * A selector context to hold a root {@link IComponent} for seeking icomponents.
 * @author jumperchen
 */
public interface ISelector {

	/**
	 * Returns the target icomponent by its given id if matched.
	 * @param id the id of the icomponent for seeking
	 */
	<T extends IComponent<?>> T get(String id);

	/**
	 * Returns the target icomponent by its given path if matched.
	 * @param path the path of the icomponent for seeking
	 */
	<T extends IComponent<?>> T get(int[] path);

	/**
	 * Returns all the ancestor from top to bottom for the given path.
	 * @param path the path of the icomponent for seeking
	 */
	<T extends IComponent<?>> List<T> getAncestor(int[] path);

	/**
	 * Returns all the ancestor from top to bottom for the given ichild.
	 * @param ichild the icomponent for seeking
	 */
	<T extends IComponent<?>> List<T> getAncestor(IComponent ichild);

	/**
	 * Returns the target parent.
	 * <p>Note: If not found, {@code null} is assumed</p>
	 * @param ichild the icomponent for seeking
	 */
	<T extends IComponent<?>> T getParent(IComponent ichild);

	/**
	 * Returns the target path.
	 * <p>Note: If not found, {@code null} is assumed</p>
	 * @param ichild the icomponent for seeking
	 */
	public int[] getPath(IComponent ichild);
}
