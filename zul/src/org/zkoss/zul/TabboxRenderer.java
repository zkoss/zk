/** TabboxRenderer.java.

	Purpose:
		
	Description:
		
	History:
		11:48:37 AM Nov 6, 2013, Created by jumperchen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul;

/**
 * Identifies components that can be used as "rubber stamps" to paint
 * the tab and tabpanel in a {@link Tabbox}.
 *
 * @author jumperchen
 * @see ListModel
 * @since 7.0.0
 */
public interface TabboxRenderer<T> {
	/** Renders the data to the specified tab.
	 *
	 * @param tab the tab to render the result.
	 *
	 * @param data that is returned from {@link ListModel#getElementAt}
	 * @param index the index of the data that is currently being rendered 
	 */
	public void renderTab(Tab tab, T data, int index) throws Exception;
	/** Renders the data to the specified tabpanel.
	 *
	 * @param tabpanel the tabpanel to render the result.
	 *
	 * @param data that is returned from {@link ListModel#getElementAt}
	 * @param index the index of the data that is currently being rendered 
	 */
	public void renderTabpanel(Tabpanel tabpanel, T data, int index) throws Exception;

}
