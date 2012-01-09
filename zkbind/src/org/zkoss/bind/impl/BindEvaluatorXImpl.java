/* SimpleEvaluatorX.java

	Purpose:
		
	Description:
		
	History:
		Jul 29, 2011 9:30:30 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.ValueReference;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
import org.zkoss.zel.PropertyNotFoundException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.xel.impl.SimpleEvaluator;

/**
 * A simple implementation of {@link BindEvaluatorX}.
 * 
 * @author henrichen
 * @since 6.0.0
 */
public class BindEvaluatorXImpl extends SimpleEvaluator implements BindEvaluatorX {
	private static final long serialVersionUID = 1L;

	public BindEvaluatorXImpl(FunctionMapper mapper, Class<? extends ExpressionFactory> expfcls) {
		super(mapper, expfcls);
	}

	public Object getValue(BindContext ctx, Component comp, ExpressionX expression)
	throws XelException {
		return expression.evaluate(newXelContext(ctx, comp));
	}

	public void setValue(BindContext ctx, Component comp, ExpressionX expression, Object value)
	throws XelException {
		try {
			expression.setValue(newXelContext(ctx, comp), value);
		} catch (PropertyNotFoundException ex) {
			//TODO, Henri, shall log here
			//ignore if fail to locate base
		}
	}

	public ExpressionX parseExpressionX(BindContext ctx, String expression, Class<?> expectedType)
	throws XelException {
		return (ExpressionX) getExpressionFactory()
			.parseExpression(newXelContext(ctx, null), "${"+expression+"}", expectedType);
	}
	
	public Class<?> getType(BindContext ctx, Component comp, ExpressionX expression)
	throws XelException {
		return expression.getType(newXelContext(ctx, comp));
	}
	
	public ValueReference getValueReference(BindContext ctx, Component comp, ExpressionX expression)
	throws XelException {
		return expression.getValueReference(newXelContext(ctx, comp));
	}

	//utility to create an XelContext associated to the refrence (could be Component or Page)
	protected XelContext newXelContext(BindContext ctx, Component comp) {
		final XelContext xelc = super.newXelContext(comp);
		xelc.setAttribute(BinderImpl.BINDCTX, ctx);
		if (ctx != null) {
			xelc.setAttribute(BinderImpl.BINDING, ctx.getBinding());
			//Dennis, a special control flag to ignore adding tracker when doing el,
			//see BindELContext#ignoreTracker
			xelc.setAttribute(BinderImpl.IGNORE_TRACKER, ctx.getAttribute(BinderImpl.IGNORE_TRACKER));
		}
		return xelc;
	}
}
