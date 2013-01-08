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
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.InitChildrenBinding;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollectorFactory;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Implementation of {@link InitChildrenBinding}.
 * @author Dennis
 * @since 6.0.0
 */
public class InitChildrenBindingImpl extends ChildrenBindingImpl implements
	InitChildrenBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	
	public InitChildrenBindingImpl(Binder binder, Component comp, String initExpr,Map<String, Object> bindingArgs,
			String converterExpr,Map<String, Object> converterArgs) {
		super(binder, comp, initExpr, ConditionType.PROMPT, null, bindingArgs,converterExpr,converterArgs);
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
		
		//use _converter to convert type if any
		final Converter conv = getConverter();
		if (conv != null) {			
			value = conv.coerceToUi(value, comp, ctx);
			if(value == Converter.IGNORED_VALUE) {
				BindingExecutionInfoCollector collector = BindingExecutionInfoCollectorFactory.getDefaultCollector();
				if(collector!=null){
					collector.addExecutionInfo(this,"init-children",
							getPureExpressionString(_accessInfo.getProperty()),"[converter handled]",value,getArgs());
				}
				return;
			}
		}
		
		comp.getChildren().clear();
		BindELContext.removeModel(comp);
		if(value!=null){
			List<Object> data = null;
			if(value instanceof List){
				data = (List<Object>)value;
			}else{
				throw new UiException(value+" is not a List, is "+value.getClass());
			}
			BindChildRenderer renderer = new BindChildRenderer();
			BindELContext.addModel(comp, data); //ZK-758. @see AbstractRenderer#addItemReference
			int size = data.size();
			for(int i=0;i<size;i++){
				renderer.render(comp, data.get(i),i,size);
			}
		}
		
		BindingExecutionInfoCollector collector = BindingExecutionInfoCollectorFactory.getDefaultCollector();
		if(collector!=null){
			collector.addExecutionInfo(this,"init-children",
					getPureExpressionString(_accessInfo.getProperty()),"",value,getArgs());
		}
	}
}
