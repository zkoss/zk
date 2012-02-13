/* Openable.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Apr 17, 2011 13:39:21 PM, Created by jimmyshiau
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.ext;

import java.util.Collection;
import java.util.Set;

/**
 * Indicate an openable collection.
 *
 * <p>Since 6.0.0, {@link TreeOpenableModel} was introduced to handle
 * the open state of {@link org.zkoss.zul.TreeModel} for better performance.
 * Thus, for handling tree's open states, please use {@link TreeOpenableModel}
 * instead.
 *
 * @author jimmyshiau
 * @see org.zkoss.zul.TreeModel
 * @see org.zkoss.zul.Tree
 */
public interface Openable<E> {
	/**
	 * Returns the objects that are opened.
	 * It is readonly. Don't modify it directly
	 * @since 6.0.0
	 */
	public Set<E> getOpenObjects();
	/**
	 * Replace the current set of opened objects with the given set.
	 * @since 6.0.0
	 */
	public void setOpenObjects(Collection<? extends E> opens);
	/**
	 * Returns whether the specified object be opened.
	 * @param obj
	 */
	public boolean isObjectOpened(Object obj);
	/**
	 * Returns true if the open is currently empty.
	 * @since 6.0.0
	 */
	public boolean isOpenEmpty();
	/**
	 * Add the specified object into the collection of opened objects.
	 * @param obj the object to be as selection.
	 * @return true if it is added successfully; false if <code>obj</code>
	 * is not part of the data, or was already opened.
	 * @since 6.0.0
	 */
	public boolean addOpenObject(E obj);
	/**
	 * Remove the specified object from selection.
	 * @param obj the object to be remove from selection.
	 * @return whether it is removed successfully
	 * @since 6.0.0
	 */
	public boolean removeOpenObject(Object obj);
	/**
	 * Clear all open status.
	 * <p>Notice that this method is designed to be called by a component
	 * (such as {@link org.zkoss.zul.Tree}).
	 * If it is called by an application, the component's open status
	 * won't be changed.
	 */
	public void clearOpen();
}
