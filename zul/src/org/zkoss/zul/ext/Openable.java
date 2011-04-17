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

import org.zkoss.zul.Tree;
import org.zkoss.zul.TreeModel;


/**
 * Indicate a openable collection or component. Generally used with {@link TreeModel}
 * and {@link Tree}.
 * @author jimmyshiau
 * @see TreeModel
 * @see Tree
 */
public interface Openable {
	/**
	 * Sets the specified object into open.
	 * <p>Notice that this method is designed to be called by a component
	 * (such as {@link org.zkoss.zul.Treeitem}).
	 * If it is called by an application, the component's open status
	 * won't be changed.
	 * @param obj the object to be as open.
	 * @param open whether be opened
	 */
	public void open(Object obj, boolean open);
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
