/* Tabpanel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * A tab panel.
 * 
 * <p>
 * Default {@link #getSclass}:
 * <table border="1" cellspacing="0">
 * <tr>
 * <td>sclass</td>
 * <td>tabbox's mold</td>
 * <td>tabbox's orient {@link Tabbox#getOrient}</td>
 * </tr>
 * <tr>
 * <td>tabpanel</td>
 * <td>default</td>
 * <td>horizontal</td>
 * </tr>
 * <tr>
 * <td>tabpanel-<em>something</em></td>
 * <td><em>something</em></td>
 * <td>horizontal</td>
 * </tr>
 * <tr>
 * <td>vtabpanel</td>
 * <td>default</td>
 * <td>vertical</td>
 * </tr>
 * <tr>
 * <td>vtabpanel-<em>something</em></td>
 * <td><em>something</em></td>
 * <td>vertical</td>
 * </tr>
 * </table>
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Tabpanel extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the tabbox owns this component.
	 */
	public org.zkoss.zul.api.Tabbox getTabboxApi();

	/**
	 * Returns the tab associated with this tab panel.
	 */
	public org.zkoss.zul.api.Tab getLinkedTabApi();

	/**
	 * Returns the index of this panel, or -1 if it doesn't belong to any
	 * tabpanels.
	 */
	public int getIndex();

}
