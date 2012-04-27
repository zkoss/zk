/* LoadChildrenBindingImpl.java

	Purpose:
		
	Description:
		
	History:
		2012/1/2 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.LoadChildrenBinding;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Implementation of {@link LoadChildrenBinding}.
 * @author dennis
 * @since 6.0.0
 */
public class LoadChildrenBindingImpl extends ChildrenBindingImpl implements
		LoadChildrenBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	private final Set<String> _doneDependsOn;
//	private final Set<Class<? extends Converter>> _doneConverterDependsOn;
	
	public LoadChildrenBindingImpl(Binder binder, Component comp,
		String loadExpr, ConditionType conditionType,String command,  Map<String, Object> bindingArgs,
		String converterExpr,Map<String, Object> converterArgs) {
		super(binder, comp, loadExpr, conditionType, command, bindingArgs,converterExpr,converterArgs);
		_doneDependsOn = new HashSet<String>(4);
//		_doneConverterDependsOn = new HashSet<Class<? extends Converter>>(4);
	}
	
	@SuppressWarnings("unchecked")
	public void load(BindContext ctx) {
		final Component comp = getComponent();//ctx.getComponent();
		final BindEvaluatorX eval = getBinder().getEvaluatorX();
		//get data from property
		Object value = eval.getValue(ctx, comp, _accessInfo.getProperty());
		
		final boolean activating = ((BinderCtrl)getBinder()).isActivating();	
		
		//use _converter to convert type if any
		final Converter conv = getConverter();
		if (conv != null) {
//			//if a converter depends on some property, we should also add tracker
//			//TODO, Dennis, ISSUES, currently, a base path of a converter, is its binding path.
//			//ex @bind(vm.person.firstName) , it's base path is 'vm.person.firstName', not 'vm.person'
//			//this sepc is different with DependsOn of a property
//			addConverterDependsOnTrackings(conv, ctx);
			
			if(activating) return;//don't load to component if activating
			
			value = conv.coerceToUi(value, comp, ctx);
			if(value == Converter.IGNORED_VALUE) return;
		}
		if(activating) return;//don't load to component if activating
		
		comp.getChildren().clear();
		BindELContext.removeModel(comp);
		if(value!=null){
			Collection<Object> data = null;
			if(value instanceof Collection){
				data = (Collection<Object>)value;
			}else{
				//handle other type in converter
				throw new UiException("it is not a list, is "+value.getClass()+":"+value);
			}
			BindChildRenderer renderer = new BindChildRenderer();
			BindELContext.addModel(comp, data); //ZK-758. @see AbstractRenderer#addItemReference
			int size = data.size();
			int i = 0;
			for(Object obj:data){
				renderer.render(comp, obj , i++, size);
			}
		}
	}
	
//	private void addConverterDependsOnTrackings(Converter conv, BindContext ctx) {
//		final Class<? extends Converter> convClz = conv.getClass();
//		if (_doneConverterDependsOn.contains(convClz)) { //avoid to eval converter @DependsOn again if not exists
//			return;
//		}
//		_doneConverterDependsOn.add(convClz);
//		final Method m = getConverterMethod(convClz);
//		final String srcpath = getPropertyString();
//		BindELContext.addDependsOnTrackings(m, srcpath, null, this, ctx);
//	}
	
	private Method getConverterMethod(Class<? extends Converter> cls) {
		try {
			return cls.getMethod("coerceToUi", new Class[] {Object.class, Component.class, BindContext.class});
		} catch (NoSuchMethodException e) {
			//ignore
		}
		return null; //shall never come here
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
