/* ItemRenderer.java

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

import org.zkoss.zk.ui.Component;

/**
 * Identifies components that can be used to render the content of the html.
 * 
 * @author jumperchen
 * @since 6.0.0
 */
public interface ItemRenderer<T> {
	
	/** Renders the data to the specific html.
	 *
	 * @param owner the comopnent that the renderer belongs to.
	 * @param data that is returned from {@link ListModel#getElementAt}
	 */
	public String render(Component owner, T data) throws Exception;
}
