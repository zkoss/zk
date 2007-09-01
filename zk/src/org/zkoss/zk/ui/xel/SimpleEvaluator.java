/* SimpleEvaluator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 22:43:08     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.xel;

import org.zkoss.xel.XelContext;
import org.zkoss.xel.SimpleXelContext;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.Expression;
import org.zkoss.xel.Expressions;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.XelException;

import org.zkoss.web.servlet.xel.RequestContext;
import org.zkoss.web.servlet.xel.RequestContexts;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;

/**
 * A simple implementation of {@link Evaluator}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class SimpleEvaluator implements Evaluator {
	private transient SimpleXelContext _xelc;
	private transient ExpressionFactory _expf;
	private Class _expfcls;

	/**
	 * @param expfcls the class that implements the expression factory.
	 * If null, the default one is used.
	 */
	public SimpleEvaluator(Class expfcls) {
		_expfcls = expfcls;
	}

	//Evaluator//
	public Expression parseExpression(String expression, Class expectedType)
	throws XelException {
		return getExpressionFactory()
			.parseExpression(getXelContext(null), expression, expectedType);
	}
	public Object evaluate(Page page, Expression expression)
	throws XelException {
		return expression.evaluate(getXelContext(page));
	}
	public Object evaluate(Component comp, Expression expression)
	throws XelException {
		return expression.evaluate(getXelContext(comp));
	}

	/** Returns the expression factory. */
	private ExpressionFactory getExpressionFactory() {
		if (_expf == null)
			_expf = Expressions.newExpressionFactory(_expfcls);
		return _expf;
	}
	/** Returns the XEL context.
	 */
	private XelContext getXelContext(Object ref) {
		final FunctionMapper mapper = getFunctionMapper(ref);
		final VariableResolver resolver = getVariableResolver(ref);
		if (_xelc == null) {
			_xelc = new SimpleXelContext(resolver, mapper);
		} else {
			_xelc.setVariableResolver(resolver);
			_xelc.setFunctionMapper(mapper);
		}
		return _xelc;
	}

	/** Returns the function mapper, or null if not available.
	 *
	 * <p>Default: always null.
	 *
	 * @param ref the object,either page, component, or null.
	 * It is passed to {@link #evaluate}
	 */
	public FunctionMapper getFunctionMapper(Object ref) {
		return null;
	}
	/** Returns the variable resolver, or null if not available.
	 * <p>Default: it returns the variable resolver of
	 * the current {@link RequestContext}, if any.
	 *
	 * @param ref the object,either page, component, or null.
	 * It is passed to {@link #evaluate}
	 */
	public VariableResolver getVariableResolver(Object ref) {
		final RequestContext rc = RequestContexts.getCurrent();
		return rc != null ? rc.getVariableResolver(): null;
	}
}
