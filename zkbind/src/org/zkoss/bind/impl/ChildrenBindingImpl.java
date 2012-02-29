/* ChildrenBindingImpl.java

	Purpose:
		
	Description:
		
	History:
		2012/1/2 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.ChildrenBinding;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;

/**
 * A base implementation of {@link ChildrenBinding}.
 * @author dennis
 * @since 6.0.0
 */
public abstract class ChildrenBindingImpl extends BindingImpl implements ChildrenBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	protected final AccessInfo _accessInfo;
	private final ExpressionX _converter;
	private final Map<String, Object> _converterArgs;

	/**
	 * @param binder
	 * @param comp
	 * @param accessExpr the binding expression , to access the bean
	 * @param conditionType the condition type
	 * @param command the command, if the conditionType is not prompt, then command must not null
	 * @param bindingArgs
	 */
	protected ChildrenBindingImpl(Binder binder, Component comp, String accessExpr, 
			ConditionType conditionType, String command, Map<String, Object> bindingArgs,
			String converterExpr, Map<String, Object> converterArgs) {
		super(binder,comp, bindingArgs);
		final BindEvaluatorX eval = binder.getEvaluatorX();
		final Class<Object> returnType = Object.class;
		this._accessInfo = AccessInfo.create(this, accessExpr, returnType, conditionType, command, ignoreTracker());
		_converterArgs = converterArgs;
		_converter = converterExpr==null?null:parseConverter(eval,converterExpr);
	}
	
	//should this binding set the ignore tracker attribute when evaluate the expression.
	protected boolean ignoreTracker(){
		return false;
	}
	
	private ExpressionX parseConverter(BindEvaluatorX eval, String converterExpr) {
		final BindContext ctx = BindContextUtil.newBindContext(getBinder(), this, false, null, getComponent(), null);
		//provide a bindcontext when pare expression of converter with this binding,
		//do so, the tracker will also tracking the converter dependence with this binding.
		return eval.parseExpressionX(ctx, converterExpr, Object.class);
	}
	
	public Converter getConverter() {
		if(_converter==null) return null;

		final BindContext ctx = BindContextUtil.newBindContext(getBinder(), this, false, null, getComponent(), null);
		final BindEvaluatorX eval = getBinder().getEvaluatorX();
		Object obj = eval.getValue(ctx, getComponent(), _converter);
		
		if(obj instanceof Converter){
			return (Converter)obj;
		}else if(obj instanceof String){
			return getBinder().getConverter((String)obj);//binder will throw exception if not found
		}else{
			throw new ClassCastException("result of expression '"+_converter.getExpressionString()+"' is not a Converter, is "+obj);
		}
	}
	
	public String getCommandName() {
		return this._accessInfo.getCommandName();
	}
	
	public String getPropertyString() {
		return getPureExpressionString(this._accessInfo.getProperty());
	}
	
	public ConditionType getConditionType() {
		return this._accessInfo.getConditionType();
	}
	
	/*package*/ ExpressionX getProperty() {
		return this._accessInfo.getProperty();
	}
	
	public String toString(){
		return new StringBuilder().append(getClass().getSimpleName()).append("@").append(Integer.toHexString(hashCode()))
		.append(",component:").append(getComponent())
		.append(",access:").append(getProperty().getExpressionString())
		.append(",condition:").append(getConditionType())
		.append(",command:").append(getCommandName()).toString();
	}
}
