/* NamespaceChangeListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Feb  8 09:28:17     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

/** @deprecated As of release 5.0.0, the concept of namespace is
 * deprecated.
 *
 * <p>A listener used to listen whether {@link Namespace} is changed.
 *
 * <p>To add a listener to the namespace, invoke
 * {@link Namespace#addChangeListener}.
 *
 * @author tomyeh
 */
public interface NamespaceChangeListener {
	/** Called when a variable is set to {@link Namespace}.
	 *
	 * @param value the new value.
	 */
	public void onAdd(String name, Object value);
	/** Called when a variable is removed from {@link Namespace}.
	 */
	public void onRemove(String name);
	/** @deprecated As of release 5.0.0, the concept of namespace is
	 * deprecated.
	 * <p>Called when the parent is changed.
	 *
	 * @param newparent the new parent.
	 */
	public void onParentChanged(Namespace newparent);
}
