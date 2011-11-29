/* OptionRenderer.java

	Purpose:
		
	Description:
		
	History:
		Fri Sep 30 10:53:25 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * Identifies components that can be used to render the content of the cells in
 * a {@link Selectbox}.
 * 
 * @author jumperchen
 * @since 6.0.0
 */
public interface OptionRenderer<T> {
	
	/** Renders the data to the specific cell.
	 *
	 * @param data that is returned from {@link ListModel#getElementAt}
	 */
	public String render(T data) throws Exception;
}
