/* InitChildrenBindingImpl

	Purpose:
		
	Description:
		
	History:
		2012/1/2 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.InitChildrenBinding;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;

/**
 * Implementation of {@link InitChildrenBinding}.
 * @author Dennis
 * @since 6.0.0
 */
public class InitChildrenBindingImpl extends ChildrenBindingImpl implements
	InitChildrenBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	
	public InitChildrenBindingImpl(Binder binder, Component comp, String initExpr,Map<String, Object> bindingArgs) {
		
		super(binder, comp, initExpr, ConditionType.PROMPT, null, bindingArgs);
	}
	
	@Override
	protected boolean ignoreTracker(){
		//init only loaded once, so it don't need to add to tracker.
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void load(BindContext ctx) {
		final Component comp = getComponent();//ctx.getComponent();
		final BindEvaluatorX eval = getBinder().getEvaluatorX();
		
		//get data from property
		Object value = eval.getValue(ctx, comp, _accessInfo.getProperty());

		comp.getChildren().clear();
		if(value!=null){
			BindChildRenderer renderer = new BindChildRenderer();
			List<Object> data = (List<Object>)Classes.coerce(List.class, value);
			int size = data.size();
			for(int i=0;i<size;i++){
				renderer.render(comp, data.get(i),i);
			}
		}
	}
}
