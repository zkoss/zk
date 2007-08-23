/* ClientInputSupport.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 9, 2007 4:51:13 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.impl;

/**
 * Define a set of interface to support client side input naming & decoding.
 * 
 * @author Dennis.Chen
 */
public interface ClientInputSupport {

	/**
	 * Return attribute name of ZUL component to generate [name] attribute of the [input] element in HTML,
	 * in general case, return 'name'; 
	 * @return name of attribute 
	 */
	public String getInputAttributeName();
	/**
	 * Get attribute value of ZUL component to generate [name] attribute's value of [input] element in HTML,
	 * @return value of attribute
	 */
	public String getInputAttributeValue();
}
