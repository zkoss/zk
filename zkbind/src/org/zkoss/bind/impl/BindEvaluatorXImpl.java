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
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.ValueReference;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
import org.zkoss.xel.util.SimpleXelContext;
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
		try{
			return expression.evaluate(newXelContext(ctx, comp));
		}catch(Exception x){
			throw MiscUtil.mergeExceptionInfo(x, comp);
		}
	}

	public void setValue(BindContext ctx, Component comp, ExpressionX expression, Object value)
	throws XelException {
		//ZK-1063 No exception if binding to a non-existed property
		//Dennis, Removed the try-catch PropertyNotFoundException, we don't have history to check why we did try-catch before
		//However, it should throw the property-not-found to let user be aware it. 
		try{
			expression.setValue(newXelContext(ctx, comp), value);
			
		}catch(Exception x){
			throw MiscUtil.mergeExceptionInfo(x, comp);
		}
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
		try{
			return (ExpressionX) getExpressionFactory()
				.parseExpression(newXelContext(ctx, comp), "${"+expression+"}", expectedType);
		}catch(Exception x){
			throw MiscUtil.mergeExceptionInfo(x, comp);
		}
	}
	
	public Class<?> getType(BindContext ctx, Component comp, ExpressionX expression)
	throws XelException {
		return expression.getType(newXelContext(ctx, comp));
	}
	
	public ValueReference getValueReference(BindContext ctx, Component comp, ExpressionX expression)
	throws XelException {
		try{
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
		}catch(Exception x){
			throw MiscUtil.mergeExceptionInfo(x, comp);
		}
	}

	//utility to create an XelContext associated to the reference
	protected XelContext newXelContext(BindContext ctx, final Component comp) {	
		final FunctionMapper mapper = getFunctionMapper(comp);
		//ZK-1795MVVM nested template may cause exception
		//Dennis: Shouldn't get the real variable-resolver and keep it, it will set ref as evaluator's self, 
		//When nested MVVM templates, if there are a ref-binding, it will newXelContext multiple time to do new evaluation in this evaluation,
		//this cause the real variable-resolver's self be changed when newXelContext called.
		final VariableResolver resolver = new VariableResolver(){
			public Object resolveVariable(String name) throws XelException {
				VariableResolver vr = getVariableResolver(comp);
				return vr==null?null:vr.resolveVariable(name);
			}};
		
//		final XelContext xelc = super.newXelContext(comp);
		final XelContext xelc = new SimpleXelContext(resolver, mapper);//super.newXelContext(comp);
		
		xelc.setAttribute(BinderImpl.BINDCTX, ctx);
		if (ctx != null) {
			xelc.setAttribute(BinderImpl.BINDING, ctx.getBinding());
			//Dennis, a special control flag to ignore adding tracker when doing el,
			//see BindELContext#ignoreTracker
			xelc.setAttribute(BinderImpl.IGNORE_TRACKER, ctx.getAttribute(BinderImpl.IGNORE_TRACKER));
		}
		return xelc;
	}

	
	public boolean isReadOnly(BindContext ctx, Component comp,
			ExpressionX expression) throws XelException {
		return expression.isReadOnly(newXelContext(ctx, comp));
	}
}
