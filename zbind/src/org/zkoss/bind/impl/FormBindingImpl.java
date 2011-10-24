/* FormBindingImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 9, 2011 5:52:35 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;

/**
 * Base implementation of {@link FormBinding}.
 * @author henrichen
 *
 */
public class FormBindingImpl extends BindingImpl implements FormBinding {
	final protected Form _form;
	final protected AccessInfo _accessInfo;
	final private Map<String, ExpressionX> _fieldExprs;

	protected FormBindingImpl(Binder binder, Component comp, Form form, String accessScript, Map<String, Object> args) {
		super(binder, comp, args);
		this._form = form;
		this._accessInfo = AccessInfo.create(this, accessScript, Form.class, ignoreTracker());
		_fieldExprs = new HashMap<String, ExpressionX>();
	}

	//should this binding set the ignore tracker attribute when evaluate the expression.
	protected boolean ignoreTracker(){
		return false;
	}
	
	public void addPropertyBinding() { //add associated property binding in this form
		
	}
	
	public Form getFormBean() {
		return _form;
	}

//	public String getFormId() {
//		return _form.getId();
//	}

	public String getPropertyString() {
		return getPureExpressionString(this._accessInfo.getProperty());
	}
	
	public boolean isAfter() {
		return this._accessInfo.isAfter();
	}
	
	public String getCommandName() {
		return this._accessInfo.getCommandName();
	}
	
	protected ExpressionX getFieldExpression(BindEvaluatorX eval, String field) {
		ExpressionX expr  = _fieldExprs.get(field);
		if (expr  == null) {
			final String property = getPropertyString();
			final String script = BindELContext.appendFields(property, field);
			expr = eval.parseExpressionX(null, script, Object.class);
			_fieldExprs.put(field, expr);
		}
		return expr;
	}
	
	protected ExpressionX getBaseExpression(BindEvaluatorX eval) {
		//TODO, Dennis potential bug if a field name same as form id
		final String property = getPropertyString();
		ExpressionX expr = _fieldExprs.get(property);
		if (expr == null) {
			final String script = property;
			expr = eval.parseExpressionX(null, script, Object.class);
			_fieldExprs.put(property, expr);
		}
		return expr;
	}
	
	public String toString(){
		return new StringBuilder().append(getClass().getSimpleName()).append("@").append(Integer.toHexString(hashCode()))
		.append(",component:").append(getComponent())
//		.append(",id:").append(getFormId())
		.append(",access:").append(getPropertyString())
		.append(",command:").append(getCommandName()).toString();
	}
}
