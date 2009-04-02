/* AuScrollIntoView.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr  1 19:29:40     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;

/**
 * Scrolls the ancestors to make the component visible.
 *
 * <p>data[0]: the component's UUID.
 *
 * @author tomyeh
 * @since 3.6.1
 */
public class AuScrollIntoView extends org.zkoss.zk.au.AuResponse {
	/** Constructor.
	 * @param cmp the component
	 */
	public AuScrollIntoView(Component cmp) {
		super("scrollIntoView", cmp.getUuid());
			//component-independent (so we can scroll multiple elements at once)
	}
}
