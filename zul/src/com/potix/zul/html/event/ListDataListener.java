/* ListDataListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 18:05:15     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html.event;

import com.potix.zk.ui.UiException;

/**
 * Defines the methods used to listener when the content of
 * {@link com.potix.zul.html.ListModel} is changed.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/05/29 04:28:29 $
 * @see com.potix.zul.html.ListModel
 * @see ListDataEvent
 */
public interface ListDataListener {
	/** Sent when the contents of the list has changed.
	 */
	public void onChange(ListDataEvent event);
}
