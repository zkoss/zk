/* SimpleEvaluator.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 22:43:08     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.xel.impl;

import org.zkoss.web.servlet.xel.RequestContext;
import org.zkoss.web.servlet.xel.RequestContexts;
import org.zkoss.xel.Expression;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.Expressions;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
import org.zkoss.xel.util.DualFunctionMapper;
import org.zkoss.xel.util.SimpleXelContext;
import org.zkoss.zel.ImportHandler;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.xel.Evaluator;

/**
 * A simple implementation of {@link Evaluator}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class SimpleEvaluator implements Evaluator {
	private transient ExpressionFactory _expf;
	private final Class<? extends ExpressionFactory> _expfcls;
	private final FunctionMapper _mapper;

	/**
	 * @param expfcls the class that implements the expression factory.
	 * If null, the default one is used.
	 */
	public SimpleEvaluator(FunctionMapper mapper, Class<? extends ExpressionFactory> expfcls) {
		_expfcls = expfcls;
		_mapper = mapper;
	}

	//Evaluator//
	public Expression parseExpression(String expression, Class expectedType) throws XelException {
		return getExpressionFactory().parseExpression(newXelContext(null), expression, expectedType);
	}

	public Object evaluate(Page page, Expression expression) throws XelException {
		return expression.evaluate(newXelContext(page));
	}

	public Object evaluate(Component comp, Expression expression) throws XelException {
		return expression.evaluate(newXelContext(comp));
	}

	/** Returns the implementation class of the expression factory,
	 * or null to use the default.
	 */
	public Class<? extends ExpressionFactory> getExpressionFactoryClass() {
		return _expfcls;
	}

	/** Returns the expression factory. */
	protected ExpressionFactory getExpressionFactory() {
		if (_expf == null)
			_expf = Expressions.newExpressionFactory(_expfcls);
		return _expf;
	}

	/** Instantiate a XEL context.
	 * Don't reuse it since it has attributes (that shall not be kept
	 * after evaluation).
	 */
	protected XelContext newXelContext(Object ref) {
		final FunctionMapper mapper = getFunctionMapper(ref);
		final VariableResolver resolver = getVariableResolver(ref);
		SimpleXelContext context = new SimpleXelContext(resolver, mapper);
		Page page = null;

		// ZK-5032
		if (ref instanceof ShadowElement) {
			Component host = ((ShadowElement) ref).getShadowHost();
			if (host != null) {
				page = host.getPage();
			}
		} else if (ref instanceof Component) {
			page = ((Component) ref).getPage();
		} else if (ref instanceof Page) {
			page = (Page) ref;
		}
		if (page != null) {
			context.setAttribute(ImportHandler.PageClassResolver.class.getName(),
					(ImportHandler.PageClassResolver) page::resolveClass);
		}
		return context;
		//Bug 1814838: don't cache the instance
	}

	/** Returns the function mapper, or null if not available.
	 *
	 * <p>Default: returns the function mapper passed thru
	 * {@link #SimpleEvaluator}.
	 *
	 * @param ref the object,either page, component, or null.
	 * It is passed to {@link #evaluate}
	 */
	public FunctionMapper getFunctionMapper(Object ref) {
		if (ref instanceof Component)
			ref = ((Component) ref).getPage();
		if (ref == null) {
			ExecutionCtrl execCtrl = ExecutionsCtrl.getCurrentCtrl();
			if (execCtrl != null)
				ref = execCtrl.getCurrentPage();
		}
		return DualFunctionMapper.combine(ref instanceof Page ? ((Page) ref).getFunctionMapper() : null, _mapper);
	}

	/** Returns the variable resolver, or null if not available.
	 *
	 * <p>Default: it returns the variable resolver of the
	 * current execution, if any.
	 * Otherwise, it returns the current {@link RequestContext}, if any.
	 * If both not available, null is returned.
	 *
	 * @param ref the object,either page, component, or null.
	 * It is passed to {@link #evaluate}
	 */
	public VariableResolver getVariableResolver(Object ref) {
		final Execution exec = Executions.getCurrent();
		VariableResolver resolver = exec.getVariableResolver();
		if (resolver == null) {
			final RequestContext rc = RequestContexts.getCurrent();
			if (rc != null)
				resolver = rc.getVariableResolver();
			if (resolver == null)
				return null;
		}
		if (resolver instanceof ExecutionResolver)
			return new ExecutionResolver.Wrapper(((ExecutionResolver) resolver), ref);
		return resolver;
	}
}
