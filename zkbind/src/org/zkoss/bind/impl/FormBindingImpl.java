/* FormBindingImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 9, 2011 5:52:35 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.annotation.ImmutableFields;
import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.proxy.ProxyHelper;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.FormBinding;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;

/**
 * Base implementation of {@link FormBinding}.
 * @author henrichen
 * @since 6.0.0
 */
public class FormBindingImpl extends BindingImpl implements FormBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	final protected String _formId;
	final protected AccessInfo _accessInfo;
	private Map<String, ExpressionX> _fieldExprs;

	protected FormBindingImpl(Binder binder, Component comp, String formId,
			String accessExpr, ConditionType conditionType, String command,Map<String, Object> bindingArgs) {
		super(binder, comp, bindingArgs);
		this._formId = formId;
		this._accessInfo = AccessInfo.create(this, accessExpr, Object.class, conditionType, command, ignoreTracker());
	}

	//should this binding set the ignore tracker attribute when evaluate the expression.
	protected boolean ignoreTracker(){
		return false;
	}
	
	public Form getFormBean() {
		return ((BinderCtrl)getBinder()).getForm(getComponent(), _formId);
	}
	
	public <T> Form initFormBean(Object bean, Class<Object> class1, BindContext bindContext) {
		Form form = ((BinderCtrl)getBinder()).getForm(getComponent(), _formId);
		if (form == null) {
			Class[] interfaces = null;

			// Bug ZK-2787
			if (bindContext.getAttribute(String.valueOf(Method.class)) != null) {
				Annotation[] annotations = (Annotation[]) bindContext.getAttribute(String.valueOf(Method.class));

				if (annotations != null) {
					boolean found = false;
					for (Annotation annot : annotations) {
						if (annot.annotationType() == ImmutableFields.class) {
							found = true;
							break;
						}
					}
					if (found)
						interfaces = new Class[] {ImmutableFields.class};
				}
			}
			form = (Form) ProxyHelper.createFormProxy(bean, class1, interfaces);
			((BinderCtrl)getBinder()).storeForm(getComponent(), _formId, form);
		}
		if (!(bean instanceof Form) && form instanceof FormProxyObject)
			((FormProxyObject)form).setFormOwner(bean, this);
		return form;
	}

	public String getFormId() {
		return _formId;
	}

	public String getPropertyString() {
		return BindEvaluatorXUtil.getExpressionString(this._accessInfo.getProperty());
	}
	
	public ConditionType getConditionType() {
		return this._accessInfo.getConditionType();
	}
	
	public String getCommandName() {
		return this._accessInfo.getCommandName();
	}
	
	public ExpressionX getFieldExpression(BindEvaluatorX eval, String field) {
		ExpressionX expr  = _fieldExprs == null ? null : _fieldExprs.get(field);
		if (expr  == null) {
			final String property = getPropertyString();
			final String script = BindELContext.appendFields(property, field);
			expr = eval.parseExpressionX(null, script, Object.class);
			_fieldExprs = AllocUtil.inst.putMap(_fieldExprs, field, expr); //ZK-2289
		}
		return expr;
	}
	
	public ExpressionX getFormExpression(BindEvaluatorX eval, String field) {
		final String script = BindELContext.appendFields(getFormId(), field);
		ExpressionX expr  = _fieldExprs == null ? null : _fieldExprs.get(script);
		if (expr  == null) {
			expr = eval.parseExpressionX(null, script, Object.class);
			_fieldExprs = AllocUtil.inst.putMap(_fieldExprs, script, expr); //ZK-2289
		}
		return expr;
	}
	
	public ExpressionX getBaseExpression(BindEvaluatorX eval) {
		//TODO, Dennis potential bug if a field name same as form id
		final String property = getPropertyString();
		ExpressionX expr = _fieldExprs == null ? null : _fieldExprs.get(property);
		if (expr == null) {
			final String script = property;
			expr = eval.parseExpressionX(null, script, Object.class);
			_fieldExprs = AllocUtil.inst.putMap(_fieldExprs, property, expr); //ZK-2289
		}
		return expr;
	}
	
	public String toString(){
		return new StringBuilder().append(getClass().getSimpleName()).append("@").append(Integer.toHexString(hashCode()))
		.append(",component:").append(getComponent())
		.append(",id:").append(getFormId())
		.append(",access:").append(getPropertyString())
		.append(",condition:").append(getConditionType())		
		.append(",command:").append(getCommandName()).toString();
	}
}
