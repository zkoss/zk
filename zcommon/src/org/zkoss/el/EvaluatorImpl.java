/* EvaluatorImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 28 15:38:57     2004, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.el;

import java.util.Map;

import org.zkforge.apache.commons.el.ExpressionEvaluatorImpl;
import org.zkforge.apache.commons.el.ExpressionApi;

import org.zkoss.lang.Classes;
import org.zkoss.lang.SystemException;
import org.zkoss.xel.Expression;
import org.zkoss.xel.ExpressionEvaluator;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.XelException;

/**
 * The evaluator that implements {@link ExpressionEvaluator}.
 * It encapsulates the expression evaluator come with the container.
 *
 * <p>To make it work, you have to specify the system property,
 * "org.zkoss.xel.ExpressionEvaluator.class", with the proper class name.
 * If you don't specify one, org.zkforge.apache.commons.el.ExpressionEvaluatorImpl
 * (of zcommons-el) is assumed.
 *
 * @author tomyeh
 */
public class EvaluatorImpl extends ExpressionEvaluator {
	private final ExpressionEvaluator _eval;

	public EvaluatorImpl() {
		final String clsnm;
		try {
			clsnm =	System.getProperty("org.zkoss.xel.ExpressionEvaluator.class", null);
		} catch (Throwable ex) { //permission might not be allowed
			_eval = new ZelEval();
			return;
		}

		if (clsnm == null || clsnm.length() == 0) {
			_eval = new ZelEval();
		} else {
			try {
				_eval = (ExpressionEvaluator)Classes.newInstanceByThread(clsnm);
			} catch (Exception ex) {
				throw new SystemException("Unable to construct ExpressionEvaluator from "+clsnm, ex);
			}
		}
	}
	//-- ExpressionEvaluator --//
	public Expression parseExpression(String expression,
	Class expectedType, FunctionMapper fMapper) throws XelException {
		return _eval.parseExpression(expression, expectedType, fMapper);
	}
	public Object evaluate(String expression, Class expectedType,
	VariableResolver vResolver, FunctionMapper fMapper) throws XelException {
		return _eval.evaluate(expression, expectedType, vResolver, fMapper);
	}
}
/** Evaluator based on zcommons-el.
 */
/*package*/ class ZelEval extends ExpressionEvaluator {
	private final ExpressionEvaluatorImpl _eval = new ExpressionEvaluatorImpl();

	//-- ExpressionEvaluator --//
	public Expression parseExpression(String expression,
	Class expectedType, FunctionMapper fMapper) throws XelException {
		return new Expr(_eval.parseExpression(expression, expectedType, fMapper));
	}
	public Object evaluate(String expression, Class expectedType,
	VariableResolver vResolver, FunctionMapper fMapper) throws XelException {
		return _eval.evaluate(expression, expectedType, vResolver, fMapper);
	}
	/** Expression based on zcommons-el.
	 */
	private static class Expr extends Expression {
		private final ExpressionApi _expr;
		Expr(ExpressionApi expr) {
			_expr = expr;
		}
		public Object evaluate(VariableResolver vResolver)
		throws XelException {
			return _expr.evaluate(vResolver);
		}
	}
}
