/* Render.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 18 23:46:02     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.ext;

import java.util.Set;

/**
 * Used to decorate a {@link com.potix.zk.ui.Component} object that
 * it supports render-on-demand. In other words, the component renders
 * its content only the client request for it.
 * A typical example is Listbox (it is similar to JList and ListModel in
 * Swing).
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface Render {
	/** Renders a set of specified items.
	 */
	public void renderItems(Set items);
}
