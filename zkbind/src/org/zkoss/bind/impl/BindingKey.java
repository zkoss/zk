/* BindingKey.java

	Purpose:
		
	Description:
		
	History:
		2011/12/21 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
/**
 * 
 * @author dennis
 * @since 6.0.0
 */
public class BindingKey implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	//remove the WeakReference , http://tracker.zkoss.org/browse/ZK-869
	/** 
	 * The first key. 
	 **/
	private final Component x;
	
	/** The second key. */
	private final String y;
	
	public BindingKey(Component x, String y) {
		this.x = x;
		this.y = y;
	}
	
	//-- Object --//
	public final boolean equals(Object o) {
		if(o==this) return true;
		
		if (!(o instanceof BindingKey))
			return false;
		
		final BindingKey key = (BindingKey)o;
		return Objects.equals(x, key.x) &&
				Objects.equals(y, key.y);
	}
	public final int hashCode() {
		return Objects.hashCode(x) ^ Objects.hashCode(y);
	}

	public String toString() {
		return '(' + Objects.toString(x) + ", "  + Objects.toString(y) + ')';
	}
}
