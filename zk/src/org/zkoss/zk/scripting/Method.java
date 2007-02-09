/* Method.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jan 25 13:51:30     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

/**
 * Represents a method stored in an interpretor.
 *
 * @author tomyeh
 * @see Interpreter#getMethod
 */
public interface Method {
	/** Returns an array of Class objects that represent the formal parameter types,
	 * in declaration order, of the method represented by this Method object.
	 * Returns an array of length 0 if the underlying method takes no parameters.
	 */
	public Class[] getParameterTypes();
	/** Returns a Class object that represents the formal return type of the method
	 * represented by this Method object.
	 */
	public Class getReturnType();
	/** Invokes this method with the specified arguments.
	 *
	 * @param args the arguments used for the method call.
	 * If null, Object[0] is assumed.
	 */
	public Object invoke(Object[] args) throws Exception;
}
