/* RadioRenderer.java

	Purpose:
		
	Description:
		
	History:
		Jan 11, 2012 18:39:46 , Created by tonyq

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * Identifies components that can be used as "rubber stamps" to paint
 * the cells in a {@link RadioGroup}.
 *
 * @author tonyq
 * @see ListModel
 * @see Radio
 * @See RadioGroup
 *
 */
public interface RadioRenderer<T> {
	/** Renders the data to the specified radio.
	 *
	 * @param item the comboitem to render the result.
	 * @param data that is returned from {@link ListModel#getElementAt}
	 */
	public void render(Radio item, T data,int index) throws Exception;
}
