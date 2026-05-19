/* StubonlyControl.java

        Purpose:
                
        Description:
                
        History:
                Tue Oct 20 12:38:20 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext.render;

/**
 * An extra control returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}
 * to retain server-side state when applying component-name stub-only policies.
 * This control affects {@link org.zkoss.zk.ui.Component#setStubonlyDescendants(String)},
 * without changing the behavior of {@link org.zkoss.zk.ui.Component#setStubonly(String)}.
 *
 * @since 11.0.0
 */
public interface StubonlyControl {
	/** Returns whether component-name stub-only matching is allowed.
	 * A false result excludes this component and all its descendants, including
	 * descendants that declare their own component-name policies.
	 *
	 * @return whether name-based stub-only matching is allowed for this subtree
	 * @since 11.0.0
	 */
	public boolean isStubonlyDescendantsAllowed();
}
