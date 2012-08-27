/* LoadFormBinding.java

	Purpose:
		
	Description:
		
	History:
		Aug 9, 2011 6:25:44 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.FormExt;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.LoadFormBinding;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.xel.ExpressionX;
import org.zkoss.xel.ValueReference;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Implementation of {@link LoadFormBinding}
 * @author henrichen
 * @since 6.0.0
 */
public class LoadFormBindingImpl extends FormBindingImpl implements	LoadFormBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	private int _len;
	private Set<String> _doneDependsOn = new HashSet<String>(4);
	
	public LoadFormBindingImpl(Binder binder, Component comp, String formId, String loadExpr, 
			ConditionType conditionType,String command, Map<String, Object> bindingArgs) {
		super(binder, comp, formId, loadExpr, conditionType, command, bindingArgs);
	}

	public void load(BindContext ctx) {
		final Binder binder = getBinder();
		final BindEvaluatorX eval = binder.getEvaluatorX();
		final Component comp = getComponent();
		final Object bean = eval.getValue(ctx, comp, _accessInfo.getProperty());
		//ZK-1016 Nested form binding doesn't work.
		final ValueReference valref = eval.getValueReference(ctx, comp,  _accessInfo.getProperty());
		//value-reference is null if it is a simple node, ex ${vm}
		if( (valref!=null && valref.getBase() instanceof Form) || bean instanceof Form){
			throw new UiException("doesn't support to load a nested form , formId "+getFormId());
		}
		
		
		
		final Form form = getFormBean();
		final boolean activating = ((BinderCtrl)getBinder()).isActivating();
		if(form instanceof FormExt){
			FormExt fex = (FormExt)form;
			//ZK-1005 ZK 6.0.1 validation fails on nested bean
			//sets the last loaded bean express of the form
			comp.setAttribute(BinderImpl.LOAD_FORM_EXPRESSION, getPropertyString());
			
			for (String field : fex.getLoadFieldNames()) {
				final ExpressionX expr = getFieldExpression(eval, field);
				if (expr != null) {
					final Object value = eval.getValue(ctx, comp, expr);
					if(!activating){//don't load to form if activating
						//ZK-911. Save into Form bean via expression(so will use form's AccessFieldName)
						final ExpressionX formExpr = getFormExpression(eval, field);
						eval.setValue(null, comp, formExpr, value); //formExprform.setField(field, value);
					}
				}
			}
			if(activating) return;
			
			fex.resetDirty(); //initial loading, mark form as clean
		}
		if(activating) return;//don't notify change if activating
		
		binder.notifyChange(form, "."); //notify change of fx and fx.*
		if(form instanceof FormExt){
			binder.notifyChange(((FormExt)form).getStatus(), ".");//notify change of fxStatus and fxStatus.*
		}
	}
	
	public void setSeriesLength(int len) {
		_len = len;
	}
	
	public int getSeriesLength() {
		return _len;
	}

	/**
	 * Internal Use Only.
	 */
	public void addDependsOnTrackings(List<String> srcpath, String basepath, String[] props) {
		if (srcpath != null) {
			final String src = BindELContext.pathToString(srcpath);
			if (_doneDependsOn.contains(src)) { //this method has already done @DependsOn in this binding
				return;
			}
			_doneDependsOn.add(src); //mark method as done @DependsOn
		}
		for(String prop : props) {
			BindELContext.addDependsOnTracking(this, srcpath, basepath, prop);
		}
	}
}
