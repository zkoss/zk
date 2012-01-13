/* LoadChildrenBindingImpl.java

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
import org.zkoss.bind.sys.LoadChildrenBinding;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;

/**
 * Implementation of {@link LoadChildrenBinding}.
 * @author dennis
 * @since 6.0.0
 */
public class LoadChildrenBindingImpl extends ChildrenBindingImpl implements
		LoadChildrenBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	public LoadChildrenBindingImpl(Binder binder, Component comp,
		String loadExpr, ConditionType conditionType,String command,  Map<String, Object> bindingArgs) {
		super(binder, comp, loadExpr, conditionType, command, bindingArgs);
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
			comp.setAttribute(BinderImpl.MODEL, data); //ZK-758. @see AbstractRenderer#addItemReference
			int size = data.size();
			for(int i=0;i<size;i++){
				renderer.render(comp, data.get(i),i);
			}
		}
	}
}
