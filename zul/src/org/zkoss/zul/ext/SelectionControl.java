/* SelectionControl.java

	Purpose:
		
	Description:
		
	History:
		4:36 PM 8/3/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul.ext;

import java.io.Serializable;

/**
 * Indicate a selection control for {@link Selectable}
 * @author jumperchen
 * @since 8.0.0
 */
public interface SelectionControl<E> extends Serializable {
	/**
	 * Returns whether the element can be selected.
	 * @param e
	 */
	public boolean isSelectable(E e);

	/**
	 * Sets to select all elements or deselect all elements.
	 */
	public void setSelectAll(boolean selectAll);

	/**
	 * Returns whether it's all elements selected
	 */
	public boolean isSelectAll();
}
