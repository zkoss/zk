/* EvaluatorImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 28 15:38:57     2004, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.el;

import java.util.Map;

import javax.servlet.jsp.el.Expression;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.ELException;

import org.apache.commons.el.ExpressionEvaluatorImpl;

import com.potix.lang.Classes;
import com.potix.lang.SystemException;
import com.potix.util.prefs.Apps;

/**
 * Our evaluator that implements ExpressionEvaluator.
 * It encapsulates the expression evaluator come with the container.
 *
 * <p>To make it work, you have to specify the application property,
 * "com.potix.el.ExpressionEvaluator.class", with the proper class name.
 * If you don't specify one, "org.apache.commons.el.ExpressionEvaluatorImpl"
 * is assumed.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/05/29 04:27:17 $
 */
public class EvaluatorImpl extends ExpressionEvaluator {
	private final ExpressionEvaluator _eval;

	public EvaluatorImpl() {
		final String clsnm =
			Apps.getProperty("com.potix.el.ExpressionEvaluator.class", null);
		if (clsnm == null || clsnm.length() == 0) {
			_eval = new ExpressionEvaluatorImpl();
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
	Class expectedType, FunctionMapper fMapper) throws ELException {
		return _eval.parseExpression(expression, expectedType, fMapper);
	}
	public Object evaluate(String expression, Class expectedType,
	VariableResolver vResolver, FunctionMapper fMapper) throws ELException {
		return _eval.evaluate(expression, expectedType, vResolver, fMapper);
	}
}
