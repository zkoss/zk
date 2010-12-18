/* MainLayoutAPI.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 12, 2008 3:26:47 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListitemRenderer;

/**
 * @author jumperchen
 *
 */
public interface MainLayoutAPI {
	public Category[] getCategories();
	public ListModel getSelectedModel();
	public ListitemRenderer getItemRenderer();
}
