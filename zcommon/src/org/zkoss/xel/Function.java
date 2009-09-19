/* Function.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 10:24:49     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

import java.lang.reflect.Method;

/**
 * Represents a XEL function.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface Function {
	/** Returns an array of Class objects that represent the formal
	 * parameter types, in declaration order, of the method represented
	 * by this Method object.
	 *
	 * <p>Returns an array of length 0 if the underlying method
	 * takes no parameters.
	 */
	public Class[] getParameterTypes();
	/** Returns a Class object that represents the formal return type
	 * of the method
	 * represented by this Method object.
	 */
	public Class getReturnType();
	/** Invokes this method with the specified arguments.
	 *
	 * @param obj the object the underlying method is invoked from.
	 * It is always null if this function is invoked in an XEL expression.
	 * It is reserved for more sophisticated expressions, such as
	 * ZK Spreadsheet's expressions.
	 * @param args the arguments used for the method call.
	 * If null, an Object array with zero length is assumed.
	 */
	public Object invoke(Object obj, Object[] args) throws Exception;

	/** Converts this function to a method, or null if unable to convert.
	 */
	public Method toMethod();
}
