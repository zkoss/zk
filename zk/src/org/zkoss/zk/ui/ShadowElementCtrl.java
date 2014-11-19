/** ShadowElementCtrl.java.

	Purpose:
		
	Description:
		
	History:
		11:50:50 AM Oct 27, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui;

import java.util.List;

/**
 * An addition interface to {@link ShadowElement}
 * @author jumperchen
 * @since 8.0.0
 */
public interface ShadowElementCtrl {
	/**
	 * Sets the owner component that hosts this shadow element.
	 * @param host the host of the shadow element
	 * @param insertBefore a component or a shadow element to indicate where the
	 * shadow's insertion point is added.
	 */
	public void setShadowHost(Component host, Component insertBefore);
	
	/** Called before the host removing a child.
	 * @param child the child to be removed (never null)
	 * @param indexOfChild the index of the removed child
	 */
	public void beforeHostChildRemoved(Component child, int indexOfChild);
	
	/** Called before the host changing the parent.
	 * @param parent the new parent. If null, it means detachment.
	 */
	public void beforeHostParentChanged(Component parent);
	
	/** Called before the host adding a child.
	 * @param child the child to be added (never null).
	 * @param insertBefore another child component that the new child
	 * will be inserted before it. If null, the new child will be the
	 * last child.
	 * @param indexOfInsertBefore the index of the insertBefore, if any. Otherwise -1 is assumed.
	 */
	public void beforeHostChildAdded(Component child, Component insertBefore, int indexOfInsertBefore);
	
	/** Called when a child is added to the host.
	 * @param child the child has been added (never null).
	 * @param indexOfChild the index of the added child.
	 */
	public void afterHostChildAdded(Component child, int indexOfChild);
	
	/** Called when a child is removed to the host.
	 * @param child the child has been added (never null).
	 */
	public void afterHostChildRemoved(Component child);
	
	/**
	 * Returns whether the shadow element contains a dynamic value, it means the
	 * shadow element cannot be destroyed after evaluated.
	 */
	public boolean isDynamicValue();

	/**
	 * <p>Tool or framework developer use only.
	 * 
	 * Rebuilds the shadow tree if the shadow element contains a dynamic value,
	 * it should be alive, otherwise, it will be detached.
	 * @return a set of shadow tree that contain a dynamic value, or empty list.
	 */
	public <T extends ShadowElement> List<T> rebuildShadowTree();
}
