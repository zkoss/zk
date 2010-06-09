/* CollectionItemEx.java

	Purpose:
		
	Description:
		
	History:
		Feb 29, 2008 6:20:34 PM, Created by henrichen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import java.util.List;

import org.zkoss.zk.ui.Component;

/**
 * This interface is an enhanced version of the original
 * {@link CollectionItem} interface that 
 * provides a way that will speed up the processing under some 
 * collection in collection condition.
 * 
 * @author henrichen
 * @see CollectionItem
 * @see DataBinder
 * @since 3.0.6
 */
public interface CollectionItemExt extends CollectionItem {
	/**
	 * <p>
	 * Returns all children items of the given Collection owner such as <i>Grid</i>.
	 * </p>
	 * @param owner Collection owner component such as <i>Grid</i>.
	 * @return children items of the given Collection owner.
	 * @since 3.0.6 
	 */
	public List getItems(Component owner);
}
