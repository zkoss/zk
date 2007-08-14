/* TreeitemRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 10 2007, Created by Jeff Liu
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;
/**
 * Identifies components that can be used as "rubber stamps" to paint
 * the cells in a {@link Tree}.
 *
 * @author Jeff Liu
 * @see TreeModel
 * @see Tree
 */
public interface TreeitemRenderer {
	/** Renders the data to the specified tree item.
	 *
	 * @param item the Treeitem to render the result.
	 * Note: when this method is called, the listitem has no child
	 * at all, unless you don't return
	 *
	 * @param data that is used to render the Treeitem
	 */
	public void render(Treeitem item, Object data) throws Exception;
}
