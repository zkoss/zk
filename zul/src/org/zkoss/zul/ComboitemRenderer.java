/* ComboitemRenderer.java

	Purpose:
		
	Description:
		
	History:
		Dec 27, 2007 11:25:46 AM , Created by jumperchen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * Identifies components that can be used as "rubber stamps" to paint
 * the cells in a {@link Combobox}.
 *
 * <p>If you need better control, your renderer can also implement
 * {@link ComboitemRendererExt}.
 *
 * <p>In addition, you could also
 * implement {@link RendererCtrl}. For example, starts an transaction,
 * and uses it to render all items for the same request.
 *
 * @author jumperchen
 * @see ListModel
 * @see Combobox
 * @see ComboitemRendererExt
 *
 */
public interface ComboitemRenderer {
	/** Renders the data to the specified comboitem.
	 *
	 * @param item the comboitem to render the result.
	 * @param data that is returned from {@link ListModel#getElementAt}
	 */
	public void render(Comboitem item, Object data) throws Exception;
}
