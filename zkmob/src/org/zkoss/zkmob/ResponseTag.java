/* ResponseTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 31, 2007 12:36:41 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

/**
 * A marker interface for Response tags.
 * @author henrichen
 *
 */
public class ResponseTag {
	private String _value;
	
	/**
	 * Set the value of this ResponseTag.
	 * @param val the value.
	 */
	public void setValue(String val) {
		_value = val;
	}
	
	/**
	 * Get the value of this ResponseTag.
	 *
	 */
	public String getValue() {
		return _value;
	}
}
