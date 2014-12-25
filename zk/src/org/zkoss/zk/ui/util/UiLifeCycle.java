/* UiLifeCycle.java

	Purpose:
		
	Description:
		
	History:
		Mon May 19 14:03:23     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.ShadowElement;

/**
 * Used to intercept the life cycle of UI, such as {@link Component}
 * being attached.
 *
 * @author tomyeh
 * @since 3.0.6
 */
public interface UiLifeCycle {
	/** Called after a shadow is attached to a host.
	 *
	 * <p>If a tree of shadows are attached to a host, this method
	 * called only against the root one. For example, if shadow A
	 * has a child B and A is attached to host P, then
	 * afterShadowAttached(A, P) is called.
	 *
	 * <p>Note: exception thrown by this method is ignored (but logged).
	 * @since 8.0.0
	 */
	public void afterShadowAttached(ShadowElement shadow, Component host);
	/** Called after a shadow is detached from a host.
	 *
	 * <p>If a tree of shadows are detached to a host, this method
	 * called only against the root one. For example, if shadow A
	 * has a child B and A is detached from host P, then
	 * afterShadowDetached(A, P) is called.
	 *
	 * <p>Note: exception thrown by this method is ignored (but logged).
	 *
	 * @param prevpage the previous host that shadow belongs to.
	 * @since 8.0.0
	 */
	public void afterShadowDetached(ShadowElement shadow, Component prevhost);
	/** Called after a component is attached to a page.
	 *
	 * <p>If a tree of components are attached to a page, this method
	 * called only against the root one. For example, if component A
	 * has a child B and A is attached to page P, then
	 * afterComponentAttached(A, P) is called.
	 *
	 * <p>Note: exception thrown by this method is ignored (but logged).
	 */
	public void afterComponentAttached(Component comp, Page page);
	/** Called after a component is detached from a page.
	 *
	 * <p>If a tree of components are detached to a page, this method
	 * called only against the root one. For example, if component A
	 * has a child B and A is detached from page P, then
	 * afterComponentDetached(A, P) is called.
	 *
	 * <p>Note: exception thrown by this method is ignored (but logged).
	 *
	 * @param prevpage the previous page that comp belongs to.
	 */
	public void afterComponentDetached(Component comp, Page prevpage);
	/** Called after the parent/children relation is changed.
	 *
	 * <p>If a tree of components has become children of the other component,
	 * this method called only against the root one.
	 * For example, if component A
	 * has a child B and A becomes a child of component C, then
	 * aferComponentAttached(C, A, null) is called.
	 *
	 * <p>Note: exception thrown by this method is ignored (but logged).
	 *
	 * @param prevparent the previous parent. If it is the same as
	 * parent, comp is moved in the same parent.
	 */
	public void afterComponentMoved(Component parent, Component child, Component prevparent);

	/** Called after a page is attached to a desktop.
	 * <p>Note: exception thrown by this method is ignored (but logged).
	 */
	public void afterPageAttached(Page page, Desktop desktop);
	/** Called after a page is detached to a desktop.
	 */
	public void afterPageDetached(Page page, Desktop prevdesktop);
}
