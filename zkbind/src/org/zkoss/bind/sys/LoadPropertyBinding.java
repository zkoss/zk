/* LoadPropertyBinding.java

	Purpose:
		
	Description:
		
	History:
		Aug 1, 2011 2:39:56 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.sys;


/**
 * PropertyBinding for load into component attribute from backing bean property.
 * @author henrichen
 * @since 6.0.0
 */
public interface LoadPropertyBinding extends PropertyBinding, LoadBinding {
	/**
	 * a object to indicate to ignore the load() in load-property-binding, it is usually return by a converter
	 */
	public Object LOAD_IGNORED = new Object();
}
