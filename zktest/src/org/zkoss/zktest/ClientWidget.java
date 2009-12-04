/* ClientWidget.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 4, 2009 10:51:37 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest;

/**
 * The ZK client object skeleton.
 * @author jumperchen
 */
public abstract class ClientWidget {
	protected StringBuffer _out;
	/**
	 * Returns true if the string is null or empty.
	 */
	public static final boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
	/**
	 * Returns true if the string is null or empty or pure blank.
	 */
	public static final boolean isBlank(String s) {
		return s == null || s.trim().length() == 0;
	}
	
	protected String toUpperCase(String key, String name) {
		char[] buf = name.toCharArray();
		buf[0] = Character.toUpperCase(buf[0]);
		return key + new String(buf);
	}
	
	public String eval(String name) {
		return ZKTestCase.getCurrent().getEval(_out.toString() + "." + name);
	}
	
	public String toString() {
		return _out.toString();
	}
}
