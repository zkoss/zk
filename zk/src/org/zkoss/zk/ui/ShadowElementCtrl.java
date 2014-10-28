/** ShadowElementCtrl.java.

	Purpose:
		
	Description:
		
	History:
		11:50:50 AM Oct 27, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui;

/**
 * An addition interface to {@link ShadowElement}
 * @author jumperchen
 * @since 8.0.0
 */
public interface ShadowElementCtrl {
	/**
	 * Sets the owner component that hosts this shadow element.
	 */
	public void setShadowHost(Component host);
	
	/** Called before the host removing a child.
	 * @param child the child to be removed (never null)
	 */
	public void beforeHostChildRemoved(Component child);
	
	/** Called before the host changing the parent.
	 * @param parent the new parent. If null, it means detachment.
	 */
	public void beforeHostParentChanged(Component parent);
	
	/** Called before the host adding a child.
	 * @param child the child to be added (never null).
	 * @param insertBefore another child component that the new child
	 * will be inserted before it. If null, the new child will be the
	 * last child.
	 */
	public void beforeHostChildAdded(Component child, Component insertBefore);
	
	/** Called when a child is added to the host.
	 */
	public void afterHostChildAdded(Component child);
	
	/** Called when a child is removed to the host.
	 */
	public void afterHostChildRemoved(Component child);
	
	/**
	 * Returns whether the shadow element contains a dynamic value, it means the
	 * shadow element cannot be destroyed after evaluated.
	 */
	public boolean isDynamicValue();

}
