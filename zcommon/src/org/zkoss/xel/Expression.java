/* Expression.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Nov 23 10:43:32     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

/**
 * The abstract class for a prepared expression.
 *
 * @author tomyeh
 * @since 2.4.2
 */
public abstract class Expression {
	/** Evaluates an expression that was previously prepared.
	 */
	public abstract Object evaluate(VariableResolver vResolver)
	throws XelException;
}
