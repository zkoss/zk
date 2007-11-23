/* Evals.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 27 17:47:03     2004, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.el;

import org.zkoss.xel.ExpressionEvaluator;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.XelException;

import org.zkoss.lang.Classes;

/**
 * Utilities to wrap the real implementation of Expression Language evaluators.
 *
 * @author tomyeh
 */
public class Evals {
	protected Evals() {}//prevent from instantiate

	/** Evaluates the expression with the specified resolver and 
	 * mapper, and the expected class is Object.class.
	 *
	 * <p>Unlike ExpressionEvaluator, null is returned if expr is null.
	 *
	 * @param expr the expression, which might contain zero, one or
	 * multiple ${...}.
	 */
	public static Object evaluate(String expr, Class expectedType,
	VariableResolver resolv, FunctionMapper funcs)
	throws XelException {
		if (expr == null || expr.length() == 0 || expr.indexOf("${") < 0) {
				if (expectedType == Object.class || expectedType == String.class)
				return expr;
			return Classes.coerce(expectedType, expr);
		}

    	return new EvaluatorImpl().evaluate(expr, expectedType, resolv, funcs);
	}
	
}
