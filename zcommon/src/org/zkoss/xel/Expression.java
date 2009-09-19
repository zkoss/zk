/* Expression.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 09:53:39     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

/**
 * Represent a prepared (aka., compiled) expression.
 * It can be retrieved by use of {@link ExpressionFactory}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface Expression {
	/** Evaluates an expression that was previously prepared.
	 */
	public Object evaluate(XelContext ctx)
	throws XelException;
}
