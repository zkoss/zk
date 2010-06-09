/* IdSpace.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 29 10:41:31     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Collection;
import org.zkoss.zk.ui.ext.Scope;

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
public interface IdSpace extends Scope {
	/** Returns a component of the specified ID in this ID space.
	 * Components in the same ID space are called fellows.
	 *
	 * <p>Unlike {@link #getFellowIfAny(String)}, it throws {@link ComponentNotFoundException}
	 * if not found.
	 *
	 * @exception ComponentNotFoundException is thrown if
	 * this component doesn't belong to any ID space
	 */
	public Component getFellow(String id)
	throws ComponentNotFoundException;
	/** Returns a component of the specified ID in this ID space, or null
	 * if not found.
	 * <p>Unlike {@link #getFellow(String)}, it returns null if not found.
	 */
	public Component getFellowIfAny(String id);
	/** Returns all fellows in this ID space.
	 * The returned collection is readonly.
	 * @since 3.0.6
	 */
	public Collection getFellows();
	/** Returns whether there is a fellow named with the specified component ID.
	 * @since 3.5.2
	 */
	public boolean hasFellow(String id);

	/** Returns a component of the specified ID in this ID space.
	 *
	 * <p>Unlike {@link #getFellowIfAny(String, boolean)}, it throws {@link ComponentNotFoundException}
	 * if not found.
	 *
	 * @exception ComponentNotFoundException is thrown if
	 * this component doesn't belong to any ID space
	 * @param recurse whether to look up the parent ID space for the
	 * existence of the fellow
	 * @since 5.0.0
	 */
	public Component getFellow(String id, boolean recurse)
	throws ComponentNotFoundException;
	/** Returns a component of the specified ID in this ID space, or null
	 * if not found.
	 *
	 * <p>Unlike {@link #getFellow(String, boolean)}, it returns null
	 * if not found.
	 *
	 * @param recurse whether to look up the parent ID space for the
	 * existence of the fellow
	 * @since 5.0.0
	 */
	public Component getFellowIfAny(String id, boolean recurse);
	/** Returns whether there is a fellow named with the specified component ID.
	 *
	 * @param recurse whether to look up the parent ID space for the
	 * existence of the fellow
	 * @since 5.0.0
	 */
	public boolean hasFellow(String id, boolean recurse);
}
