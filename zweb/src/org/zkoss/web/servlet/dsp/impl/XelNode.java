/* XelNode.java

	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 15:47:54     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.impl;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.xel.Expression;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.XelException;
import org.zkoss.util.logging.Log;

/**
 * Represents an expression.
 *
 * @author tomyeh
 * @since 3.0.0
 */
class XelNode extends Node {
	private static final Log log = Log.lookup(XelNode.class);
	private final Expression _expr;

	XelNode(String expr, ParseContext ctx) throws XelException {
		_expr = ctx.getExpressionFactory()
			.parseExpression(ctx, expr, String.class);
	}

	//-- super --//
	void interpret(InterpretContext ic)
	throws IOException {
		try {
			final String result = (String)_expr.evaluate(ic.xelc);
			if (result != null)
				ic.dc.getOut().write(result);
		} catch (XelException ex) {
			log.realCauseBriefly(ex); //Web server might 'eat'
			throw ex;
		}
	}
	void addChild(Node node) {
		throw new IllegalStateException("No child allowed");
	}

	public String toString() {
		return "EL[" + _expr + ']';
	}
}
