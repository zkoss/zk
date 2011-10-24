/* EvaluatorExt.java

	Purpose:
		
	Description:
		
	History:
		Jul 29, 2011 9:11:31 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.sys;

import org.zkoss.bind.BindContext;
import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.ValueReference;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.xel.Evaluator;

/**
 * An extension of {@link Evaluator} to have more control to handle Binding via EL.
 *
 * <p>With {@link BindEvaluatorX}, you are allow to parse a Method expression or set value into property
 * resolved by the provided expression.</p>
 * @author henrichen
 * @since 6.0.0
 */
public interface BindEvaluatorX extends Evaluator {
	/**
	 * Prepares the expressionX.
	 *
	 * @param expression the expression to be prepared for being evaluated
	 * later.
	 * @param expectedType the expected type of the result of the evaluation
	 */
	public ExpressionX parseExpressionX(BindContext ctx, String expression, Class<?> expectedType)
	throws XelException;

	/**
	 * Sets the specified value to the property resolved from the specified expression.
	 *
	 * @param xelc XelContext for this operation
	 * @param expression the expression that will resolve a property
	 * @param value the value to be set into the resolved property
	 */
	public void setValue(BindContext ctx, Component comp, ExpressionX expression, Object value)
	throws XelException;

	/**
	 * Sets the specified value to the property resolved from the specified expression.
	 *
	 * @param xelc XelContext for this operation
	 * @param expression the expression that will resolve a property
	 * @param value the value to be set into the resolved property
	 */
	public Object getValue(BindContext ctx, Component comp, ExpressionX expression)
	throws XelException;

	/**
	 * Returns the result type of the specified expression.
	 *
	 * @param ctx BindContext
	 * @param comp evaluation context 
	 * @param expression the expression that will resolve a property
	 */
	public Class<?> getType(BindContext ctx, Component comp, ExpressionX expression)
	throws XelException;

	/**
	 * Returns the result reference of the specified expression.
	 *
	 * @param ctx BindContext
	 * @param comp evaluation context 
	 * @param expression the expression that will resolve a property
	 */
	public ValueReference getValueReference(BindContext ctx, Component comp, ExpressionX expression)
	throws XelException;
}
