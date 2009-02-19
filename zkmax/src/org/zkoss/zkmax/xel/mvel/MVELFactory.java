/* MVELFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Sep  2 21:04:06     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.mvel;

import java.util.Collection;
import java.util.List;
import java.util.Iterator;

import org.mvel.MVEL;
import org.mvel.ParserContext;
import org.mvel.ExpressionCompiler;
import org.mvel.util.ParseTools;

import org.zkoss.lang.Classes;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.Expression;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.XelException;

import org.zkoss.zkmax.xel.util.ExpressionFragment;

/**
 * An implementation based on MVEL.
 *
 * <p>Note: MVEL is not completely compatible with JSP EL.
 *
 * <p>See also <a href="http://mvel.codehaus.org/">MVEL website</a>.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class MVELFactory implements ExpressionFactory {
	//ExpressionFactory//
	public boolean isSupported(int feature) {
		return feature == FEATURE_CLASS;
	}
	public Expression parseExpression(XelContext ctx, String expression,
	Class expectedType)
	throws XelException {
		final ParserContext pctx = getParserContext(ctx);
		final List frags = ExpressionFragment.parse(expression);
		final Object[] fs = new Object[frags.size()];
		int j = 0;
		for (Iterator it = frags.iterator(); it.hasNext(); ++j) {
			final Object o = it.next();
			if (o instanceof ExpressionFragment) {
				fs[j] = compile(((ExpressionFragment)o).getExpression(), pctx);
			} else {
				fs[j] = o;
			}
		}
		return new MVELXelExpression(fs, expectedType);
	}
	public Object evaluate(XelContext ctx, String expression,
	Class expectedType)
	throws XelException {
		final XelMVELResolver resolver =
			new XelMVELResolver(ctx.getVariableResolver());

		final ParserContext pctx = getParserContext(ctx);

		final List frags = ExpressionFragment.parse(expression);
		if (frags.size() == 1) { //optimize this most common case
			final Object o = frags.get(0);
			return Classes.coerce(expectedType,
				o instanceof String ? o:
					eval(((ExpressionFragment)o).getExpression(), resolver, pctx));
		}

		final StringBuffer sb = new StringBuffer(256);
		for (Iterator it = frags.iterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof String) {
				sb.append(o);
			} else {
				Object val = eval(((ExpressionFragment)o).getExpression(), resolver, pctx);
				if (val != null)
					sb.append(val);
			}	
		}
		return Classes.coerce(expectedType, sb.toString());
	}
	private
	Object eval(String expr, XelMVELResolver resolver, ParserContext pctx) {
		return pctx == null ? MVEL.eval(expr, resolver):
			MVEL.executeExpression(compile(expr, pctx), resolver);
	}
	private Object compile(String expr, ParserContext pctx) {
		return pctx == null ?
			MVEL.compileExpression(expr):
			ParseTools.optimizeTree(
				new ExpressionCompiler(expr).compile(pctx));
	}
	private ParserContext getParserContext(XelContext ctx) {
		if (ctx != null) {
			final FunctionMapper mapper = ctx.getFunctionMapper();
			if (mapper != null) {
				final Collection c = mapper.getClassNames();
				if (c != null && !c.isEmpty()) {
					final ParserContext pctx = new ParserContext();
					for (Iterator it = c.iterator(); it.hasNext();) {
						final String nm = (String)it.next();
						pctx.addImport(nm, mapper.resolveClass(nm));
					}
					return pctx;
				}
			}
		}
		return null;
	}
}
