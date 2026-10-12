/* ExportedExpressionEvaluator.java

		Purpose:
				
		Description:
				
		History:
				Mon Sep 28 12:59:22 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.xel.el;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.zkforge.apache.commons.el.ArraySuffix;
import org.zkforge.apache.commons.el.BinaryOperatorExpression;
import org.zkforge.apache.commons.el.Coercions;
import org.zkforge.apache.commons.el.ComplexValue;
import org.zkforge.apache.commons.el.ConditionalExpression;
import org.zkforge.apache.commons.el.ExpressionEvaluatorImpl;
import org.zkforge.apache.commons.el.ExpressionString;
import org.zkforge.apache.commons.el.FunctionInvocation;
import org.zkforge.apache.commons.el.Literal;
import org.zkforge.apache.commons.el.Logger;
import org.zkforge.apache.commons.el.PropertySuffix;
import org.zkforge.apache.commons.el.UnaryOperatorExpression;
import org.zkforge.apache.commons.el.ValueSuffix;
import org.zkforge.apache.commons.el.parser.ELParser;
import org.zkforge.apache.commons.el.parser.ParseException;
import org.zkforge.apache.commons.el.parser.TokenMgrError;

import org.zkoss.lang.Classes;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;

/**
 * The zcommons-el evaluator of {@link ELFactory} that reads a property of an
 * object whose class is in a package its module does not export, such as
 * sun.util.calendar.ZoneInfo, through the same getter declared by a public
 * supertype in an exported package, such as java.util.TimeZone.
 * Every other evaluation is done by zcommons-el as is.
 *
 * @author peakerlee
 */
/*package*/ class ExportedExpressionEvaluator extends ExpressionEvaluatorImpl {
	/** Our own parsed copies, so the cache zcommons-el shares with every other caller is left untouched. */
	private static final Map<String, Object> PARSED = new ConcurrentHashMap<String, Object>();

	public Object parseExpressionString(String expression) throws XelException {
		if (expression.length() == 0)
			return super.parseExpressionString(expression);
		Object parsed = PARSED.get(expression);
		if (parsed == null) {
			try {
				parsed = wrap(new ELParser(new StringReader(expression)).ExpressionString());
			} catch (ParseException | TokenMgrError ex) {
				return super.parseExpressionString(expression);
			}
			PARSED.put(expression, parsed);
		}
		return parsed;
	}

	private static Object wrap(Object expr) {
		if (expr instanceof ExpressionString) {
			for (Object o : ((ExpressionString) expr).getElements())
				wrap(o);
		} else if (expr instanceof ComplexValue) {
			wrap(((ComplexValue) expr).getPrefix());
			for (ListIterator it = ((ComplexValue) expr).getSuffixes().listIterator(); it.hasNext();) {
				final Object suffix = it.next();
				if (suffix instanceof ArraySuffix) {
					wrap(((ArraySuffix) suffix).getIndex());
					it.set(new ExportedSuffix((ArraySuffix) suffix));
				}
			}
		} else if (expr instanceof BinaryOperatorExpression) {
			wrap(((BinaryOperatorExpression) expr).getExpression());
			wrapAll(((BinaryOperatorExpression) expr).getExpressions());
		} else if (expr instanceof UnaryOperatorExpression) {
			wrap(((UnaryOperatorExpression) expr).getExpression());
		} else if (expr instanceof ConditionalExpression) {
			wrap(((ConditionalExpression) expr).getCondition());
			wrap(((ConditionalExpression) expr).getTrueBranch());
			wrap(((ConditionalExpression) expr).getFalseBranch());
		} else if (expr instanceof FunctionInvocation) {
			wrapAll(((FunctionInvocation) expr).getArgumentList());
		}
		return expr;
	}

	private static void wrapAll(List exprs) {
		for (Object o : exprs)
			wrap(o);
	}

	/** Returns the getter of the given property as declared by a public
	 * supertype in an exported package, or null if the class's own getter is
	 * accessible (so what failed was the getter itself) or there is none.
	 */
	private static Method getExportedGetter(Class<?> cls, String property) {
		Method m = null;
		try {
			for (PropertyDescriptor pd : Introspector.getBeanInfo(cls).getPropertyDescriptors())
				if (pd.getName().equals(property))
					m = pd.getReadMethod();
		} catch (IntrospectionException ex) { //no getter then
		}
		if (m == null || isExported(m.getDeclaringClass()))
			return null;
		try {
			final Method pub = Classes.getMethodInPublic(cls, m.getName(), null);
			return isExported(pub.getDeclaringClass()) ? pub : null;
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}

	private static boolean isExported(Class<?> cls) {
		return Modifier.isPublic(cls.getModifiers())
				&& cls.getModule().isExported(cls.getPackageName(), ExportedExpressionEvaluator.class.getModule());
	}

	/** Evaluates a {@code .name} or {@code [index]} suffix as zcommons-el
	 * does, and retries a getter it cannot invoke through
	 * {@link #getExportedGetter}.
	 */
	private static class ExportedSuffix extends ValueSuffix {
		private final ArraySuffix _suffix;

		private ExportedSuffix(ArraySuffix suffix) {
			_suffix = suffix;
		}

		public String getExpressionString() {
			return _suffix.getExpressionString();
		}

		public Object evaluate(Object value, XelContext ctx, Logger logger) throws XelException {
			final boolean computed = value != null && !(_suffix instanceof PropertySuffix)
					&& !(_suffix.getIndex() instanceof Literal);
			//evaluate a computed index once, so the retry below does not evaluate it again
			final Object index = computed ? _suffix.getIndex().evaluate(ctx, logger) : null;
			final ArraySuffix suffix = !computed ? _suffix : new ArraySuffix(new Literal(index) {
				public String getExpressionString() {
					return _suffix.getIndex().getExpressionString();
				}
			});
			try {
				return suffix.evaluate(value, ctx, logger);
			} catch (XelException ex) {
				// ZK-6167: since JDK 17 zcommons-el cannot invoke a getter of a non-exported class such as sun.util.calendar.ZoneInfo
				if (value == null || !(ex.getCause() instanceof IllegalAccessException))
					throw ex;
				final Object name = _suffix instanceof PropertySuffix ? ((PropertySuffix) _suffix).getName()
						: computed ? index : ((Literal) _suffix.getIndex()).getValue();
				final Method getter = getExportedGetter(value.getClass(), Coercions.coerceToString(name, logger));
				if (getter == null)
					throw ex;
				try {
					return getter.invoke(value);
				} catch (InvocationTargetException e) {
					throw new XelException(ex.getMessage(), e.getTargetException());
				} catch (IllegalAccessException e) {
					throw ex;
				}
			}
		}
	}
}
