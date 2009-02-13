/* IdSpace.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 29 10:41:31     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Collection;

/**
 * Implemented by a component ({@link Component}) and a page
 * ({@link Page})
 * to denote that all its descendant and itself forms an independent ID space.
 *
 * <p>In an ID space, ID of each component ({@link Component#getId}) must be
 * unique.
 * And, you could retrieve any component in the same ID space by calling
 * {@link #getFellow} upon any component of the same ID space.
 *
 * <p>For sake of description, components in the same ID space are called
 * fellows.
 * If the component has an ancestor which implements {@link IdSpace},
 * we say a component belongs to the ID space owned by the ancestor.
 * If no such ancestor exists, the component belongs to the ID space
 * owned by the page.
 * The ancestor and the page is called the owner
 * ({@link Component#getSpaceOwner}).
 *
 * <p>For sake of description, we also call the ID space as space X, if
 * the owner is component X (or page X).
 * If component Y is a child of X and also implements {@link IdSpace}
 * (aka., another owner), then space X includes component Y, but
 * EXCLUDES descendants of Y. In other words, Y belongs to space X and space Y.
 * Thus, to get a child Z in the Y space, you shall X.getFellow('Y').getFellow('Z').
 *
 * <p>Example: Assumes component A is a child of B, B a child of C and
 * C a child of D. If only C implements {@link IdSpace}, A, B and C are all
 * belonged to the C space. D doesn't belong any space.
 *
 * <p>If both C and D implements {@link IdSpace}, C and D belongs
 * to the D space while A, B and C belongs to the C space.
 *
 * <p>Note: to make a component (deriving from {@link AbstractComponent})
 * an ID space owner, all it needs to do is to implement this interface.
 *
 * @author tomyeh
 */
public interface IdSpace {
	/** Returns a component of the specified ID in the same ID space.
	 * Components in the same ID space are called fellows.
	 *
	 * <p>Unlike {@link #getFellowIfAny}, it throws an exception if not found.
	 *
	 * @exception ComponentNotFoundException is thrown if
	 * this component doesn't belong to any ID space
	 */
	public Component getFellow(String id);
	/** Returns a component of the specified ID in the same ID space, or null
	 * if not found.
	 * <p>Unlike {@link #getFellow}, it returns null if not found.
	 */
	public Component getFellowIfAny(String id);
	/** Returns all fellows in this ID space.
	 * The returned collection is readonly.
	 * @since 3.0.6
	 */
	public Collection getFellows();
}
