/* SharedListModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 9, 2007 3:09:13 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkex.zul;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zul.ListModel;

/**
 * This interface is created for sharing model to different desktop's component.<br/>
 * The implementation of this interface should provide built-in Server Push mechanism.
 * 
 * 
 * @author Dennis.Chen
 * @since 3.0.0
 */
public interface ListModelSharer {

	
	/**
	 * Get a proxy which is to be used in listbox or grid of a desktop.
	 * @param desktop a desktop
	 * @return a ListModel proxy
	 */
	ListModel getProxy(Desktop desktop);
	
	/**
	 * Get the count of created proxy.
	 * @return the created proxy count
	 */
	int getProxyCount();
}
