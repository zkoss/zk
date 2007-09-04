/* MVELFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Sep  2 21:04:06     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.mvel;

import java.util.List;
import java.util.Iterator;

import org.mvel.MVEL;

import org.zkoss.lang.Classes;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.Expression;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
import org.zkoss.xel.util.ExpressionFragment;

/**
 * An implementation based on MVEL.
 *
 * <p>Note: MVEL is not completely compatible with JSP EL.
 * For example, it doesn't support {@link org.zkoss.xel.FunctionMapper#resolveFunction}.
 * Rather, it supports {@link org.zkoss.xel.FunctionMapper#resolveFunction}.
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
		final List frags = ExpressionFragment.parse(expression);
		final Object[] fs = new Object[frags.size()];
		int j = 0;
		for (Iterator it = frags.iterator(); it.hasNext();) {
			final Object o = it.next();
			fs[j++] = o instanceof ExpressionFragment ?
					MVEL.compileExpression(((ExpressionFragment)o).getExpression()):
					o;
		}
		return new MVELXelExpression(fs, expectedType);
	}
	public Object evaluate(XelContext ctx, String expression,
	Class expectedType)
	throws XelException {
		final XelMVELResolver resolver =
			new XelMVELResolver(ctx.getVariableResolver());

		final List frags = ExpressionFragment.parse(expression);
		if (frags.size() == 1) { //optimize this most common case
			final Object o = frags.get(0);
			return Classes.coerce(expectedType,
				o instanceof String ? o:
					MVEL.eval(((ExpressionFragment)o).getExpression(), resolver));
		}

		final StringBuffer sb = new StringBuffer(256);
		for (Iterator it = frags.iterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof String) {
				sb.append(o);
			} else {
				Object val = MVEL.eval(((ExpressionFragment)o).getExpression(), resolver);
				if (val != null)
					sb.append(val);
			}	
		}
		return Classes.coerce(expectedType, sb.toString());
	}
}
