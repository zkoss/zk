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
	 * Sets whether the shadow element contains a dynamic value, if true means the
	 * shadow element cannot be destroyed after evaluated, if false it will detect
	 * its attribute automatically.
	 * <p>Default: false (auto detection)</p>
	 * @since 8.0.1
	 */
	public void setDynamicValue(boolean dynamicValue);

	/**
	 * Return the shadow host from it or its ancestor, if any. 
	 * @return null or a host component
	 */
	public Component getShadowHostIfAny();

	/** Returns the variable associated with this base component or null if not found.
	 *
	 * <p>Notice that it doesn't check any variable defined in
	 * {@link org.zkoss.xel.VariableResolver}
	 * (of {@link Page#addVariableResolver}).
	 *
	 * @param child the child component of the shadow host
	 * @param recurse whether to look up the parent shadow for the
	 * existence of the variable.<br/>
	 * If recurse is true, it will look up all parents until found.
	 */
	public Object resolveVariable(Component child, String name, boolean recurse);

	/**
	 * Returns the next insertion point, it may be a component, a shadow element, or null.
	 */
	public Component getNextInsertion();

	/**
	 * Returns the previous insertion point, it may be a component, a shadow element, or null.
	 */
	public Component getPreviousInsertion();

	/**
	 * Returns the first component of its insertion range.
	 */
	public Component getFirstInsertion();

	/**
	 * Returns the last component of its insertion range.
	 */
	public Component getLastInsertion();

	/**
	 * Called when a child of a host is added into this shadow element.
	 */
	public void onHostChildAdded(Component child);

	/**
	 * Called when a child of a host is added into this shadow element.
	 */
	public void onHostChildRemoved(Component child);
}
