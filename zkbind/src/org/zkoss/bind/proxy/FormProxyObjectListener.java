/** FormProxyObjectChangeListener.java.

	Purpose:
		
	Description:
		
	History:
		12:30:46 PM Mar 16, 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.proxy;

/**
 * The interface implemented by form proxy classes.
 * @author jameschu
 * @since 8.0.0
 */
public interface FormProxyObjectListener {
	/**
	 * Call when the data in FormProxyObject change
	 */
	public void onDataChange(Object o);
	
	/**
	 * Call when setting form.dirty true
	 */
	public void onDirtyChange();
}
