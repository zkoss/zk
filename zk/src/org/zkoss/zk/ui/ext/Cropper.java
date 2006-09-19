/* Cropper.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 22 16:43:40     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

import java.util.Set;

/**
 * Represents a component that not all of its children are available at the client.
 * Example: only children of the active page of ZUL's grid are available
 * at the client, if it is in the paging mold.
 *
 * <p>{@link org.zkoss.zk.ui.sys.Visualizer} detects this interface
 * and then ignores any update to
 * components that don't have counterpart at the client.
 *
 * <p>Note: Visualizer assumes 
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface Cropper {
	/** Returns a set of child components that are available at the client,
	 * or null if all available.
	 *
	 * <p>Note: for better performance, it is better to return null if
	 * all children are available at the client.
	 */
	public Set getAvailableAtClient();
}
