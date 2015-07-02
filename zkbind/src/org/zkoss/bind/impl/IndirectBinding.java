/** IndirectBinding.java.

	Purpose:
		
	Description:
		
	History:
		4:58:52 PM Jun 26, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.Map;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.sys.ReferenceBinding;

/**
 * An indirect binding, a kind of reference binding, but it won't create a sub-tracking
 * tree to get two way bindings, and it is used for children binding internally.
 * i.e. this class is designed for some specific cases.
 * @author jumperchen
 * @since 8.0.0
 */
public abstract class IndirectBinding implements ReferenceBinding {
	
	/**
	 * do nothing for this method
	 */
	public void load(BindContext ctx) {
		// do nothing
	}
	/**
	 * Null is returned by default. (never be used)
	 */
	public Map<String, Object> getArgs() {
		return null;
	}
	/**
	 * Null is returned by default. (never be used)
	 */
	public String getPropertyString() {
		return null;
	}
	/**
	 * do nothing for this method
	 */
	public void invalidateCache() {
	}

}
