/* Evaluator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 15:43:48     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.xel;

import org.zkoss.xel.Expression;
import org.zkoss.xel.XelException;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;

/**
 * A ZK specific expression builder that is based on XEL.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface Evaluator extends java.io.Serializable {
	/**
	 * Prepares the expression.
	 *
	 * @param expression the expression to be prepared for being evaluated
	 * later.
	 * @param expectedType the expected type of the result of the evaluation
	 */
	public Expression parseExpression(String expression, Class expectedType)
	throws XelException;
	/**
	 * Evaluates the expression.
	 *
	 * @param page the page, or null to ignore
	 */
	public Object evaluate(Page page, Expression expression)
	throws XelException;
	/**
	 * Evaluates the expression.
	 *
	 * @param comp the component, or null to ignore
	 */
	public Object evaluate(Component comp, Expression expression)
	throws XelException;
}
