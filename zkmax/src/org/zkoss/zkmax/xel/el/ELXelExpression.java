/* ELXelExpression.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 31 17:12:56     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.el;

import org.zkoss.xel.Expression;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;

import javax.servlet.jsp.el.ELException;

/**
 * A XEL expression that is based on a EL expression.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ELXelExpression implements Expression {
	private final javax.servlet.jsp.el.Expression _expr;
	/**
	 * @param expr the expression. It cannot be null.
	 */
	public ELXelExpression(javax.servlet.jsp.el.Expression expr) {
		if (expr == null)
			throw new IllegalArgumentException();
		_expr = expr;
	}
	public Object evaluate(XelContext ctx) {
		try {
			final VariableResolver resolver = ctx.getVariableResolver();
			return _expr.evaluate(
				resolver != null ? new XelELResolver(resolver): null);
		} catch (ELException ex) {
			throw new XelException(ex);
		}
	}

	//Object//
	public boolean equals(Object o) {
		return o instanceof ELXelExpression &&
			((ELXelExpression)o)._expr.equals(_expr);
	}
	public int hashCode() {
		return _expr.hashCode();
	}
	public String toString() {
		return _expr.toString();
	}
}
