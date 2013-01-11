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
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.InitPropertyBinding;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollectorFactory;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;

/**
 * Implementation of {@link InitPropertyBinding}.
 * @author Dennis
 * @since 6.0.0
 */
public class InitPropertyBindingImpl extends PropertyBindingImpl implements
	InitPropertyBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	//ZK-682 Inputfields with constraints and ZK Bind throw wrong value exception
	private final Class<?> _attrType;
	
	public InitPropertyBindingImpl(Binder binder, Component comp,
		String attr, String loadAttr, Class<?> attrType, String initExpr,Map<String, Object> bindingArgs,
		String converterExpr, Map<String, Object> converterArgs) {
		
		super(binder, comp, attr, "self."+loadAttr, initExpr, ConditionType.PROMPT, null, bindingArgs, converterExpr, converterArgs);
		_attrType = attrType == null ? Object.class : attrType;
	}
	
	@Override
	protected boolean ignoreTracker(){
		//init only loaded once, so it don't need to add to tracker.
		return true;
	}
	
	public void load(BindContext ctx) {
		final Component comp = getComponent();//ctx.getComponent();
		final BindEvaluatorX eval = getBinder().getEvaluatorX();
		final BindingExecutionInfoCollector collector = ((BinderCtrl)getBinder()).getBindingExecutionInfoCollector();
		
		//get data from property
		Object value = eval.getValue(ctx, comp, _accessInfo.getProperty());
		
		//use _converter to convert type if any
		@SuppressWarnings("unchecked")
		final Converter<Object, Object, Component> conv = getConverter();
		if (conv != null) {
			Object old;
			value = conv.coerceToUi(old = value, comp, ctx);
			if(value == Converter.IGNORED_VALUE) {
				if(collector!=null){
					collector.addLoadInfo(comp,"init-property","",
							getPureExpressionString(_accessInfo.getProperty()),getPureExpressionString(_fieldExpr),old,getArgs(),"By converter");
				}
				return;
			}
		}
		value = Classes.coerce(_attrType, value);
		//set data into component attribute
		eval.setValue(null, comp, _fieldExpr, value);
		
		if(collector!=null){
			collector.addLoadInfo(comp,"init-property","",
					getPureExpressionString(_accessInfo.getProperty()),getPureExpressionString(_fieldExpr),value,getArgs(),"");
		}
	}
}
