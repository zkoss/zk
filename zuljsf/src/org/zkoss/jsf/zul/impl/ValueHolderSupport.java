/* ValueHolderSupport.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 13, 2007 1:01:24 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.impl;
/**
 * Define a set of interface to support {@link javax.faces.component.ValueHolder}
 * 
 * @author Dennis.Chen
 */
public interface ValueHolderSupport {

	/**
	 * Return the attribute name of a zul component.
	 * A Handler which process this interface will set the value of {@link javax.faces.component.ValueHolder} into the attribute of ZUL Component. 
	 * return null means do not set the value of ValueHolder into zul component.
	 * @return name of attribute of zul component.
	 */
	public String getMappedAttributeName();
	
	/**
	 * The value from a converter or from request submitting is a String.
	 * In some ZUL Component doen't have the suitable attribute to set a String as it's value,
	 * such as Calendar, So we need Component implementation transfer String to suitable object.
	 * @param value a String value to transfer to a component suitable Object.
	 * @return the suitable Object to set to attribute of zul component.
	 */
	public Object transferValueForAttribute(String value);
	
	
}
