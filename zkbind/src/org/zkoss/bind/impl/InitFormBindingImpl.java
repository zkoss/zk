/* InitPropertyBindingImpl

	Purpose:
		
	Description:
		
	History:
		Aug 1, 2011 2:43:33 PM, Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.FormExt;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.InitFormBinding;
import org.zkoss.bind.sys.InitPropertyBinding;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;

/**
 * Implementation of {@link InitPropertyBinding}.
 * @author Dennis
 * @since 6.0.0
 */
public class InitFormBindingImpl extends FormBindingImpl implements InitFormBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	@Override
	protected boolean ignoreTracker(){
		//init only loaded once, so it don't need to add to tracker.
		return true;
	}
	
	public InitFormBindingImpl(Binder binder, Component comp, String formId, String initExpr, Map<String, Object> bindingArgs) {
		super(binder, comp, formId, initExpr, ConditionType.PROMPT, null, bindingArgs);
	}

	public void load(BindContext ctx) {
		final Component comp = getComponent();
		final BindEvaluatorX eval = getBinder().getEvaluatorX();
		final Binder binder = getBinder();
		//get data from property
		Object value = eval.getValue(ctx, comp, _accessInfo.getProperty());
		if(!(value instanceof Form)){
			final Form form = getFormBean();
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
						final Object fieldval = eval.getValue(ctx, comp, expr);
						//ZK-911. Save into Form bean via expression(so will use form's AccessFieldName)
						final ExpressionX formExpr = getFormExpression(eval, field);
						eval.setValue(null, comp, formExpr, fieldval);
					}
				}
				fex.resetDirty(); //initial loading, mark form as clean
			}
			binder.notifyChange(form, "."); //notify change of fx and fx.*
			if(form instanceof FormExt){//after notify form
				binder.notifyChange(((FormExt)form).getStatus(), ".");//notify change of fxStatus and fxStatus.*
			}
			
			BindingExecutionInfoCollector collector = ((BinderCtrl)getBinder()).getBindingExecutionInfoCollector();
			if(collector!=null){
				collector.addLoadInfo(comp,"init-form","",
						getPropertyString(),getFormId(),value,getArgs(),"");
			}
		}else{
			((BinderCtrl)binder).storeForm(getComponent(), getFormId(), (Form)value);
		}
	}
}
