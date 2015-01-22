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
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.InitChildrenBinding;
import org.zkoss.bind.sys.debugger.BindingExecutionInfoCollector;
import org.zkoss.bind.sys.debugger.impl.info.LoadInfo;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;

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
	
	
	protected boolean ignoreTracker(){
		//init only loaded once, so it don't need to add to tracker.
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void load(BindContext ctx) {
		final Component comp = getComponent();//ctx.getComponent();
		final BindEvaluatorX eval = getBinder().getEvaluatorX();
		final BindingExecutionInfoCollector collector = ((BinderCtrl)getBinder()).getBindingExecutionInfoCollector();
		
		//get data from property
		Object value = eval.getValue(ctx, comp, _accessInfo.getProperty());
		
		//use _converter to convert type if any
		final Converter conv = getConverter();
		Object old = value;
		if (conv != null) {
			value = conv.coerceToUi(value, comp, ctx);
			if(value == Converter.IGNORED_VALUE) {
				if(collector!=null){
					collector.addInfo(new LoadInfo(LoadInfo.CHILDREN_INIT,comp,null,
							getPropertyString(),null,old,getArgs(),"*Converter.IGNORED_VALUE"));
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
			//ZK-2545 - Children binding support list model
			boolean isUsingListModel = value != null && value instanceof ListModel;
			if (isUsingListModel) {
				((ListModel<?>) value).addListDataListener(new ChildrenBindingListDataListener(comp, ctx, conv));
				if (comp.hasAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS))
					comp.removeAttribute(BinderCtrl.CHILDREN_BINDING_RENDERED_COMPONENTS);
			}
			int size = data.size();
			for(int i = 0; i < size; i++) {
				renderer.render(comp, data.get(i), i, size, isUsingListModel);
			}
		}
		
		if(collector!=null){
			collector.addInfo(new LoadInfo(LoadInfo.CHILDREN_INIT,comp,null,
					getPropertyString(),null,value,getArgs(),null));
		}
	}
}
