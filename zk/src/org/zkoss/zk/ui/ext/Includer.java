/* Includer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Oct 20 14:02:51     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

import org.zkoss.zk.ui.Page;

/**
 * Implemented by a component to indicate that
 * it might include another ZUML page.
 *
 * <p>The owner of the included page is this page
 * (see {@link org.zkoss.zk.ui.sys.PageCtrl#getOwner}).
 *
 * @author tomyeh
 * @since 5.0.0
 * @see org.zkoss.zk.ui.sys.PageCtrl#getOwner
 */
public interface Includer {
	/** Returns the child page.
	 */
	public Page getChildPage();
	/** Sets the child page.
	 * Used only internally.
	 * <P>Note: the child page is actually maintained by
	 * the included page, so the implementation of this method
	 * needs only to store the page in a transient member.
	 */
	public void setChildPage(Page page);
}
