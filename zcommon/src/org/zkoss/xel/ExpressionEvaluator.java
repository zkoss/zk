/* ExpressionEvaluator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Nov 23 10:38:39     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

/**
 * The abstract base class for an expression-language evaluator.
 * Classes that implement an expression language expose their
 * functionality via this abstract class.
 *
 * @author tomyeh
 * @since 2.4.2
 */
public abstract class ExpressionEvaluator {
	/** Prepare an expression for later evaluation.
	 */
	public abstract Expression parseExpression(String expression,
	Class expectedType, FunctionMapper fMapper) throws XelException;
	/** Evaluates an expression.
	 */
	public abstract Object evaluate(String expression,
	Class expectedType, VariableResolver vResolver, FunctionMapper fMapper)
	throws XelException;
}
