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
 * Used to generated the HTML fragment for the data associated
 * with a component, such as {@link Selectbox}.
 * 
 * @author jumperchen
 * @since 6.0.0
 */
public interface ItemRenderer<T> {
	
	/** Renders the data to the corresponding HTML fragment, and returns
	 * the HTML fragment.
	 *
	 * @param owner the comopnent that this renderer belongs to (never null).
	 * @param data that is returned from {@link ListModel#getElementAt}
	 * @return the HTML fragment representing the data. It depends
	 * on the component this renderer belongs to.
	 */
	public String render(Component owner, T data) throws Exception;
}
