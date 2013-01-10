/* LoadFormBinding.java

	Purpose:
		
	Description:
		
	History:
		Aug 9, 2011 6:25:44 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollectorFactory;
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
		final BindingExecutionInfoCollector collector = ((BinderCtrl)getBinder()).getBindingExecutionInfoCollector();
		
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
			
			
			//ZK-1259, for the case of nested form expression in same loading, e.g. @load(fx.hash[fx.key]),
			//it will produce 2 loadField name, 'hash[fx.key]' & 'key', 
			//and fx + 'hash[fx.key]' will get null if fx + 'key' is not loaded yet and throw exception
			//i sort the field name to let inner value be loaded into form first.
			final String fomrid = getFormId();
			List<String> fields = new LinkedList<String>(fex.getLoadFieldNames());
			Collections.sort(fields, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					o1 = BindELContext.appendFields(fomrid, o1);
					o2 = BindELContext.appendFields(fomrid, o2);
					if(o1.indexOf(o2)>0) return 1;
					if(o2.indexOf(o1)>0) return -1;
					return 0;
				}
			});
			
			for (String field : fields) {
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
		
		if(collector!=null){
			collector.addLoadInfo(this,"load-form",getConditionString(ctx),
					getPureExpressionString(_accessInfo.getProperty()),getFormId(),bean,getArgs(),"");
		}
	}
	
	private String getConditionString(BindContext ctx){
		StringBuilder condition = new StringBuilder();
		if(getConditionType()==ConditionType.BEFORE_COMMAND){
			condition.append("before=").append(getCommandName()); 
		}else if(getConditionType()==ConditionType.AFTER_COMMAND){
			condition.append("after=").append(getCommandName()); 
		}
		return condition.toString();
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
