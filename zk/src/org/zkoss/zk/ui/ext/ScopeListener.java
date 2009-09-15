/* ScopeListener.java

	Purpose:
		
	Description:
		
	History:
		Fri Sep 11 09:32:53     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.ext;

/**
 * <p>A listener used to listen whether a scope ({@link Scope}) is changed.
 *
 * <p>To add a listener to the scope, invoke
 * {@link Scope#addChangeListener}.
 * 
 * @author tomyeh
 * @since 5.0.0
 */
public interface ScopeListener {
	/** Called when an attribute is going to be added to {@link Scope}.
	 *
	 * @param scope the scope where a new attribute is added
	 * @param value the new value.
	 */
	public void attributeAdded(Scope scope, String name, Object value);
	/** Called when an attribute is going to be replaced in {@link Scope}.
	 *
	 * @param scope the scope where a new attribute is replaced
	 * @param value the new value.
	 */
	public void attributeReplaced(Scope scope, String name, Object value);
	/** Called when an attribute is going to be removed from {@link Scope}.
	 * @param scope the scope where a new attribute is removed
	 */
	public void attributeRemoved(Scope scope, String name);
	/** Called when the parent has been changed.
	 *
	 * @param scope the scope whose parent is changed
	 * @param newparent the new parent.
	 */
	public void parentChanged(Scope scope, Scope newparent);
}
