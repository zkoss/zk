/* ExpressionFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 10:29:09     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

/**
 * Used to prepare expressions ({@link Expression}).
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface ExpressionFactory {
	/** Used with {@link #isSupported} to know whether the factory
	 * supports {@link FunctionMapper#resolveFunction}
	 */
	public static final int FEATURE_FUNCTION = 0x0001;
	/** Used with {@link #isSupported} to know whether the factory
	 * supports {@link FunctionMapper#resolveClass}
	 */
	public static final int FEATURE_CLASS = 0x0002;
	/** Returns whether an feature is supported.
	 *
	 * @param feature which feature to query.
	 * It can be a combination of {@link #FEATURE_FUNCTION}
	 * and {@link #FEATURE_CLASS}.
	 */
	public boolean isSupported(int feature);

	/** Prepares (aka., compiles) an expression.
	 *
     * @param ctx the context infomation to prepare the expression.
     * It can be null, in which case no functions are supported for this
     * invocation.
	 * @param expression the expression to be evaluated.
	 * @param expectedType the expected type of the result of the evaluation
	 */
	public Expression parseExpression(XelContext ctx, String expression,
	Class expectedType)
	throws XelException;
	/** Evaluates an expression.
	 *
	 * @param ctx the context information to evaluate an expression.
     * It can be null, in which case no functions are supported for this
     * invocation.
	 * @param expression the expression to be evaluated.
	 * Note: the expression is enclosed
	 * with ${ and }, regardingless what implemetnation is used.
	 * @param expectedType the expected type of the result of the evaluation
	 */
	public Object evaluate(XelContext ctx, String expression,
	Class expectedType)
	throws XelException;
}
