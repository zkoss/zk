/* OGNLFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 16 15:51:03     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.ognl;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import ognl.OgnlException;

import org.zkoss.lang.Classes;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.Expression;
import org.zkoss.xel.Expressions;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;

import org.zkoss.zkmax.xel.util.ExpressionFragment;

/**
 * An implementation based on OGNL.
 *
 * <p>Note: OGNL is not completely compatible with JSP EL.
 *
 * <p>See also <a href="http://www.ognl.org/">OGNL website</a>.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class OGNLFactory implements ExpressionFactory {
	public OGNLFactory() {
	}

	//ExpressionFactory//
	public boolean isSupported(int feature) {
		return feature == FEATURE_CLASS;
	}
	public Expression parseExpression(XelContext ctx, String expression,
	Class expectedType)
	throws XelException {
		try {
			final List frags = ExpressionFragment.parse(expression);
			final Object[] fs = new Object[frags.size()];
			int j = 0;
			for (Iterator it = frags.iterator(); it.hasNext(); ++j) {
				final Object o = it.next();
				if (o instanceof ExpressionFragment) {
					fs[j] = Ognl.parseExpression(((ExpressionFragment)o).getExpression());
				} else {
					fs[j] = o;
				}
			}
			return new OGNLXelExpression(fs, expectedType);
		} catch (OgnlException ex) {
			throw new XelException(ex);
		}
	}
	public Object evaluate(XelContext ctx, String expression,
	Class expectedType)
	throws XelException {
		final Map ognlctx = getContext(ctx);
		final Object root = OGNLFactory.getRoot(ctx);

		try {
			final List frags = ExpressionFragment.parse(expression);
			if (frags.size() == 1) { //optimize this most common case
				final Object o = frags.get(0);
				return Classes.coerce(expectedType,
					o instanceof String ? o:
						Ognl.getValue(((ExpressionFragment)o).getExpression(),
							ognlctx, root, null));
			}

			final StringBuffer sb = new StringBuffer(256);
			for (Iterator it = frags.iterator(); it.hasNext();) {
				final Object o = it.next();
				if (o instanceof String) {
					sb.append(o);
				} else {
					Object val =
						Ognl.getValue(((ExpressionFragment)o).getExpression(),
							ognlctx, root, null);
					if (val != null)
						sb.append(val);
				}	
			}
			return Classes.coerce(expectedType, sb.toString());
		} catch (OgnlException ex) {
			throw new XelException(ex);
		}
	}
	/**	Returns an OGNL context for the specified XEL context.
	 */
	public static Map getContext(XelContext ctx) {
		final FunctionMapper mapper = ctx.getFunctionMapper();
		return Ognl.addDefaultContext(null,
			mapper != null ? new MapperClassResolver(mapper): null,
			Collections.EMPTY_MAP);
			//Note: we always pass null as the context, since
			//we use ResolverAccessor as the root
	}
	/** Returns the root object.
	 */
	public static Object getRoot(XelContext ctx) {
		final VariableResolver resolver = ctx.getVariableResolver();
		return resolver != null ? resolver: Expressions.EMPTY_RESOLVER;
	}

	/**
	 * OGNL assumes the OGNL context is a map -- it copies all variables from 
	 * the map to the context, so we cannot implement the OGNL context on
	 * top of {@link VariableResolver} (since no way to retrieve all variables).
	 *
	 * Thus, we have to let OGNL
	 * resolve the variable thru the root object and PropertyAccessor.
	 */
	static {
		OgnlRuntime.setPropertyAccessor(
			VariableResolver.class,
			new PropertyAccessor() {
				public Object getProperty(Map context, Object target, Object name)
				throws OgnlException {
					return target instanceof VariableResolver && name instanceof String ?
						((VariableResolver)target).resolveVariable((String)name): null;
				}
				public void setProperty(Map context, Object target, Object name, Object value)
				throws OgnlException {
					throw new UnsupportedOperationException();
				}
			});
	}
}
