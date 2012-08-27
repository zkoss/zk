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
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.ReferenceBinding;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.ValueReference;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
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
		//ZK-1063 No exception if binding to a non-existed property
		//Dennis, Removed the try-catch PropertyNotFoundException, we don't have history to check why we did try-catch before
		//However, it should throw the property-not-found to let user be aware it. 
		expression.setValue(newXelContext(ctx, comp), value);
	}

	public ExpressionX parseExpressionX(BindContext ctx, String expression, Class<?> expectedType)
	throws XelException {
		Component comp = null;
		if (ctx != null) {
			comp = ctx.getComponent();
			if (comp == null) {
				final Binding binding = ctx.getBinding(); 
				if (binding != null) {
					comp = binding.getComponent();
				}
			}
		}
		return (ExpressionX) getExpressionFactory()
			.parseExpression(newXelContext(ctx, comp), "${"+expression+"}", expectedType);
	}
	
	public Class<?> getType(BindContext ctx, Component comp, ExpressionX expression)
	throws XelException {
		return expression.getType(newXelContext(ctx, comp));
	}
	
	public ValueReference getValueReference(BindContext ctx, Component comp, ExpressionX expression)
	throws XelException {
		ValueReference ref = expression.getValueReference(newXelContext(ctx, comp));
		//bug 1129-ref NPE, no value reference if it is a SimpleNode
		if(ref==null){
			XelContext xctx = newXelContext(ctx, comp);
			//Dennis, a special control flag to ignore ref-binding getValue in BindELResolver
			xctx.setAttribute(BinderImpl.IGNORE_REF_VALUE, Boolean.TRUE);
			Object val = expression.evaluate(xctx);
			if(val instanceof ReferenceBindingImpl){//get value-reference from ref-binding
				ref = ((ReferenceBindingImpl)val).getValueReference();
			}
		}
		return ref;
	}

	//utility to create an XelContext associated to the reference
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

	@Override
	public boolean isReadOnly(BindContext ctx, Component comp,
			ExpressionX expression) throws XelException {
		return expression.isReadOnly(newXelContext(ctx, comp));
	}
}
