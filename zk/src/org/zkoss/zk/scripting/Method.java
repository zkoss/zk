/* Method.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jan 25 13:51:30     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

/**
 * Represents a method stored in an interpretor.
 *
 * @author tomyeh
 * @deprecated as release 3.0.0, replaced by {@link org.zkoss.xel.Function}.
 */
public interface Method {
	/**
	 * @deprecated as release 3.0.0, replaced by {@link org.zkoss.xel.Function}.
	 */
	public Class[] getParameterTypes();
	/**
	 * @deprecated as release 3.0.0, replaced by {@link org.zkoss.xel.Function}.
	 */
	public Class getReturnType();
	/**
	 * @deprecated as release 3.0.0, replaced by {@link org.zkoss.xel.Function}.
	 */
	public Object invoke(Object[] args) throws Exception;
}
