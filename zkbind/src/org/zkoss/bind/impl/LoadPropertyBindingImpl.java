/* LoadPropertyBindingImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 1, 2011 2:43:33 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;

/**
 * Implementation of {@link LoadPropertyBinding}.
 * @author henrichen
 * @since 6.0.0
 */
public class LoadPropertyBindingImpl extends PropertyBindingImpl implements
		LoadPropertyBinding {
	private static final long serialVersionUID = 1463169907348730644L;
	private Set<String> _doneDependsOn = new HashSet<String>(4);
	private Set<Class<? extends Converter>> _doneConverterDependsOn = new WeakHashSet<Class<? extends Converter>>(4);
	//ZK-682 Inputfields with constraints and ZK Bind throw wrong value exception
	private final Class<?> _attrType;
	
	public LoadPropertyBindingImpl(Binder binder, Component comp,
		String attr, String loadAttr,Class<?> attrType, String loadExpr, ConditionType conditionType,String command,  Map<String, Object> bindingArgs, 
		String converterExpr,Map<String, Object> converterArgs) {
		super(binder, comp, attr, "self."+loadAttr, loadExpr, conditionType, command, bindingArgs, converterExpr, converterArgs);
		_attrType = attrType == null ? Object.class : attrType;
	}
	
	public void load(BindContext ctx) {
		final Component comp = getComponent();//ctx.getComponent();
		final BindEvaluatorX eval = getBinder().getEvaluatorX();
		//get data from property
		Object value = eval.getValue(ctx, comp, _accessInfo.getProperty());
		
		//use _converter to convert type if any
		final Converter conv = getConverter();
		if (conv != null) {
				//if a converter depends on some property, we should also add tracker
				//TODO, Dennis, ISSUES, currently, a base path of a converter, is its binding path.
				//ex @bind(vm.person.firstName) , it's base path is 'vm.person.firstName', not 'vm.person'
				//this sepc is different with DependsOn of a property
			addConverterDependsOnTrackings(conv, ctx);
			value = conv.coerceToUi(value, comp, ctx);
		}
		value = Classes.coerce(_attrType, value);
		//set data into component attribute
		eval.setValue(null, comp, _fieldExpr, value);
	}
	
	private void addConverterDependsOnTrackings(Converter conv, BindContext ctx) {
		final Class<? extends Converter> convClz = conv.getClass();
		if (_doneConverterDependsOn.contains(convClz)) { //avoid to eval converter @DependsOn again if not exists
			return;
		}
		_doneConverterDependsOn.add(convClz);
		final Method m = getConverterMethod(convClz);
		final String srcpath = getPropertyString();
		BindELContext.addDependsOnTrackings(m, srcpath, null, this, ctx);
	}
	
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
