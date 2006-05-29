/* ListitemRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 17:44:13     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

/**
 * Identifies components that can be used as "rubber stamps" to paint
 * the cells in a {@link Listbox}.
 *
 * <p>If you need further control about rendering, you could also
 * implement {@link RendererCtrl}. For example, starts an transaction,
 * and uses it to render all items for the same request.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/05/29 04:28:24 $
 * @see ListModel
 * @see Listbox
 */
public interface ListitemRenderer {
	/** Renders the data to the list item.
	 *
	 * @param item the listitem to render the result.
	 * Note: when this method is called, the listitem has exactly one
	 * {@link Listcell} (with an empty label).
	 * If you want to render to multiple column, you have to create
	 * the second and following {@link Listcell}.
	 * @param data that is returned from {@link ListModel#getElementAt}
	 * if this method is called by {@link Listbox}.
	 */
	public void render(Listitem item, Object data) throws Exception;
}
