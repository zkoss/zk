/* LoadBinding.java

	Purpose:
		
	Description:
		
	History:
		Aug 26, 2011 3:46:32 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.sys;

import org.zkoss.bind.BindContext;

/**
 * Binding for loading.
 * @author henrichen
 * @since 6.0.0
 */
public interface LoadBinding extends Binding {
	
	/**
	 * a object to indicate to ignore the load() in load-property-binding, it is usually return by a converter
	 */
	public Object LOAD_IGNORED = new Object();
	
	/**
	 * Load data into the source attribute from the target property.
	 * @param ctx the binding runtime context 
	 */
	public void load(BindContext ctx);
}
