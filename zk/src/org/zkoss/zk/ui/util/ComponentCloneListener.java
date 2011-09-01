/* ComponentCloneListener.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun  7 15:57:44     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Component;

/**
 * Used to notify an object stored in a component, when the
 * component is cloned.
 *
 * <p>When a component is cloned, it checks every
 * attribute ({@link Component#setAttribute})
 * and listener ({@link Component#addEventListener})
 * to see whether this interface is implemented.
 * If implemented, {@link #willClone} will be called. Then, the object
 * can either return itself, if it can be shared by two components,
 * or clone itself, if an independent instance must be used for
 * each component.
 *
 * @author tomyeh
 * @since 2.4.0
 */
public interface ComponentCloneListener {
	/** Called when a component is going to be cloned.
	 * If the object is OK to be shared by the cloned and original components,
	 * it can return itself.
	 * If an independent instance must be created for the cloned component,
	 * it can clone itesef and return the cloned object.
	 *
	 * @param comp the cloned component (not the orginal one)
	 * @return the object to be used in the cloned component.
	 * If this object is returned, the same object is shared by the
	 * cloned and original components.
	 * If other object is returned, it is used by the cloned component.
	 * If null is returned, it is not used by the cloned component at all.
	 * @since 5.0.0
	 */
	public Object willClone(Component comp);
}
