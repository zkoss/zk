/* InitPropertyBindingImpl

	Purpose:
		
	Description:
		
	History:
		Aug 1, 2011 2:43:33 PM, Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.InitFormBinding;
import org.zkoss.bind.sys.InitPropertyBinding;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.impl.info.LoadInfo;
import org.zkoss.zk.ui.Component;

/**
 * Implementation of {@link InitPropertyBinding}.
 * @author Dennis
 * @since 6.0.0
 */
public class InitFormBindingImpl extends FormBindingImpl implements InitFormBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	
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
		if (!(value instanceof Form)) {
			initFormBean(value, (Class<Object>) (value != null ? value.getClass() : eval.getType(ctx, comp, _accessInfo.getProperty())), ctx);
			
			BindingExecutionInfoCollector collector = ((BinderCtrl)getBinder()).getBindingExecutionInfoCollector();
			if(collector!=null){
				collector.addInfo(new LoadInfo(LoadInfo.FORM_INIT,comp,null,
						getPropertyString(),getFormId(),value,getArgs(),null));
			}
		}else{
			((BinderCtrl)binder).storeForm(getComponent(), getFormId(), (Form)value);
		}
	}
}
