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


/**
 * Indicate an openable collection.
 *
 * <p>Since 6.0.0, {@link TreeOpenModel} was introduced to handle
 * the open state of {@link org.zkoss.zul.TreeModel} for better performance.
 * Thus, for handling tree's open states, please use {@link TreeOpenModel}
 * instead.
 *
 * @author jimmyshiau
 * @see TreeModel
 * @see Tree
 */
public interface Openable<E> {
	/**
	 * Sets the specified object into open.
	 * @param obj the object to be as open.
	 * @param open whether be opened
	 */
	public void setOpen(E obj, boolean open);
	/**
	 * Returns whether the specified object be opened.
	 * @param obj
	 */
	public boolean isOpen(Object obj);
	/**
	 * Clear all open status.
	 * <p>Notice that this method is designed to be called by a component
	 * (such as {@link org.zkoss.zul.Tree}).
	 * If it is called by an application, the component's open status
	 * won't be changed.
	 */
	public void clearOpen();
}
