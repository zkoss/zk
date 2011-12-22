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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Implementation of {@link InitPropertyBinding}.
 * @author Dennis
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
		final Component comp = getComponent();//ctx.getComponent();
		final BindEvaluatorX eval = getBinder().getEvaluatorX();
		
		//get data from property
		Object value = eval.getValue(ctx, comp, _accessInfo.getProperty());
		
		if(!(value instanceof Form)){
			throw new UiException("the return value of init expression is not a From is :" + value);
		}
		
		((BinderCtrl)getBinder()).storeForm(getComponent(), getFormId(), (Form)value);
	}
}
