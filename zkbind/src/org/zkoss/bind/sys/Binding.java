/* Binding.java

	Purpose:
		
	Description:
		
	History:
		Jun 22, 2011 9:56:43 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys;

import java.util.Map;

import org.zkoss.bind.Binder;
import org.zkoss.zk.ui.Component;

/**
 * A Binding represent a relation between a source object
 * (usually an UI component) and a target object(usually a backing bean).
 * @author henrichen
 * @since 6.0.0
 */
public interface Binding {
	/**
	 * Returns the owner binder of this binding.
	 * @return the owner binder of this binding.
	 */
	public Binder getBinder();
	
	/**
	 * Returns the associated component of this binding.
	 * @return the associated component of this binding.
	 */
	public Component getComponent();
	
	/**
	 * Returns an argument <tags, object> pairs map(read only). 
	 * @return an argument <tags, object> pairs map(read only).
	 */
	public Map<String,Object> getArgs();
}
