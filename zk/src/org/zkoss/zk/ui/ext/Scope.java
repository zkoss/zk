/* Scope.java

	Purpose:
		
	Description:
		
	History:
		Thu Feb 19 09:45:14     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.ext;

import java.util.Map;

/**
 * Represents a scope of attributes.
 *
 * @author tomyeh
 * @since 3.6.0
 * @see org.zkoss.zk.ui.impl.SimpleScope
 */
public interface Scope {
	/** Returns all custom attributes associated with this object (scope).
	 */
	public Map getAttributes();
	/** Returns the custom attribute associated with this object (scope).
	 */
	public Object getAttribute(String name);
	/** Returns if a custom attribute is associated with this object (scope).
	 * <p>Notice that <code>null</code> is a valid value, so you can
	 * tell if an attribute is assoicated by examining the return value
	 * of {@link #getAttribute}.
	 * @since 5.0.0
	 */
	public boolean hasAttribute(String name);
	/** Sets (aka., associates) the value for a custom attribute with this object (scope).
	 * @return the previous value associated with the attribute, if any
	 * @since 5.0.0
	 */
	public Object setAttribute(String name, Object value);
	/** Removes the attribute from the current scope if any.
	 * @return the previous value associated with the attribute, if any,
	 * @since 5.0.0
	 */
	public Object removeAttribute(String name);

	/** Returns the custom attribute associated with this object.
	 *
	 * @param recurse whether to search its ancestor scope.
	 * If true and the current scope doen't define the attribute,
	 * it searches up its ancestor to see
	 * any of them has defined the specified attribute.
	 * @since 5.0.0
	 */
	public Object getAttribute(String name, boolean recurse);
	/** Returns if a custom attribute is associated with this object.
	 * <p>Notice that <code>null</code> is a valid value, so you can
	 * tell if an attribute is assoicated by examining the return value
	 * of {@link #getAttribute}.
	 *
	 * @param recurse whether to search its ancestor scope.
	 * If true and the current scope doen't define the attribute,
	 * it searches up its ancestor to see
	 * any of them has defined the specified attribute.
	 * @since 5.0.0
	 */
	public boolean hasAttribute(String name, boolean recurse);
	/** Sets the custom attribute associated with this scope, or the parent
	 * scope.
	 * @param recurse whether to look up the parent scope for the
	 * existence of the attribute.<br/>
	 * If recurse is true and the attribute is defined in
	 * one of its ancestor (including page), the attribute is replaced.
	 * Otherwise, it is the same as {@link #setAttribute(String,Object)}.
	 * @since 5.0.0
	 */
	public Object setAttribute(String name, Object value, boolean recurse);
	/** Removes the custom attribute associated with this scope.
	 * @param recurse whether to look up the parent scope for the
	 * existence of the attribute.<br/>
	 * If recurse is true and the attribute is defined in
	 * one of its ancestor (including page), the attribute is removed.
	 * Otherwise, it is the same as {@link #removeAttribute(String)}.
	 * @since 5.0.0
	 */
	public Object removeAttribute(String name, boolean recurse);

	/** Adds a listener to listen whether this scope is changed.
	 * The listener is called when a custom attribute is added, removed, or
	 * the parent is changed.
	 *
	 * @return wether the listener is added successfully.
	 * Note: if the resolver was added before, it won't be added again
	 * and this method returns false.
	 * @since 5.0.0
	 */
	public boolean addScopeListener(ScopeListener listener);
	/** Removes a change listener from this scope.
	 *
	 * @return false if listener is not added before.
	 * @since 5.0.0
	 */
	public boolean removeScopeListener(ScopeListener listener);
}
