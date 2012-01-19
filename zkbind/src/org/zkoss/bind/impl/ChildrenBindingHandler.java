/* ChildrenBindingHandler.java

	Purpose:
		
	Description:
		
	History:
		2012/1/2 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Phase;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.InitChildrenBinding;
import org.zkoss.bind.sys.LoadChildrenBinding;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Component;

/**
 * to help children-binding implementation of  BinderImpl 
 * @author dennis
 * @since 6.0.0
 */
/*package*/ class ChildrenBindingHandler extends AbstractBindingHandler{
	private static final long serialVersionUID = 1L;

	private static final Log _log = Log.lookup(ChildrenBindingHandler.class);
	
	private final Map<BindingKey, List<InitChildrenBinding>> _initBindings; //comp+_fieldExpr -> bindings (load when init)
	private final Map<BindingKey, List<LoadChildrenBinding>> _loadPromptBindings; //comp+_fieldExpr -> bindings (load _prompt | load on property change)
//	private final Map<BindingKey, List<LoadChildrenBinding>> _loadEventBindings; //comp+evtnm -> bindings (load on event)
	private final Map<String, List<LoadChildrenBinding>> _loadAfterBindings; //command -> bindings (load after command)
	private final Map<String, List<LoadChildrenBinding>> _loadBeforeBindings; //command -> bindings (load before command)
	
	
	ChildrenBindingHandler(BinderImpl binder) {
		super(binder);
		_initBindings = new HashMap<BindingKey, List<InitChildrenBinding>>();
		_loadPromptBindings = new HashMap<BindingKey, List<LoadChildrenBinding>>();
//		_loadEventBindings = new HashMap<BindingKey, List<LoadChildrenBinding>>();
		_loadAfterBindings = new HashMap<String, List<LoadChildrenBinding>>();
		_loadBeforeBindings = new HashMap<String, List<LoadChildrenBinding>>();
	}
	
//	void addLoadEventBinding(Component comp, BindingKey bkey, LoadChildrenBinding binding) {
//		List<LoadChildrenBinding> bindings = _loadEventBindings.get(bkey); 
//		if (bindings == null) {
//			bindings = new ArrayList<LoadChildrenBinding>();
//			_loadEventBindings.put(bkey, bindings);
//		}
//		bindings.add(binding);
//	}
	
	void addLoadPromptBinding(Component comp, BindingKey bkey, LoadChildrenBinding binding) {
		List<LoadChildrenBinding> bindings = _loadPromptBindings.get(bkey); 
		if (bindings == null) {
			bindings = new ArrayList<LoadChildrenBinding>();
			_loadPromptBindings.put(bkey, bindings);
		}
		bindings.add(binding);
	}
	void addInitBinding(BindingKey bkey, InitChildrenBinding binding) {
		List<InitChildrenBinding> bindings = _initBindings.get(bkey); 
		if (bindings == null) {
			bindings = new ArrayList<InitChildrenBinding>();
			_initBindings.put(bkey, bindings);
		}
		bindings.add(binding);
	}
	
	void addLoadBeforeBinding(String command, LoadChildrenBinding binding) {
		List<LoadChildrenBinding> bindings = _loadBeforeBindings.get(command);
		if (bindings == null) {
			bindings = new ArrayList<LoadChildrenBinding>();
			_loadBeforeBindings.put(command, bindings);
		}
		bindings.add(binding);
	}
	
	void addLoadAfterBinding(String command, LoadChildrenBinding binding) {
		List<LoadChildrenBinding> bindings = _loadAfterBindings.get(command);
		if (bindings == null) {
			bindings = new ArrayList<LoadChildrenBinding>();
			_loadAfterBindings.put(command, bindings);
		}
		bindings.add(binding);
	}
	
	List<LoadChildrenBinding> getLoadPromptBindings(BindingKey bkey) {
		return _loadPromptBindings.get(bkey);
	}

	
	//generic operation to load a property binding
	private void doLoadBinding(Component comp, LoadChildrenBinding binding, String command) {
		final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, false, command, binding.getComponent(), null);

		if(binding instanceof InitChildrenBindingImpl){
			ctx.setAttribute(BinderImpl.IGNORE_TRACKER, Boolean.TRUE);//ignore tracker when doing el , we don't need to track the init
		}
		try { 
			if(_log.debugable()){
				_log.debug("doLoadChildrenBinding:binding.load(),component=[%s],binding=[%s],context=[%s],command=[%s]",comp,binding,ctx,command);
			}
			doPrePhase(Phase.LOAD_BINDING, ctx);
			binding.load(ctx);
		} finally {
			doPostPhase(Phase.LOAD_BINDING, ctx);
		}
	}
	
//	//for event -> prompt only, no command
//	void doLoadEvent(BindingKey bkey,Component comp, String evtnm) {
//		final List<LoadChildrenBinding> bindings = _loadEventBindings.get(bkey);
//		if (bindings != null) {
//			for (LoadChildrenBinding binding : bindings) {
//				doLoadBinding(comp, binding, null);
//			}
//		}
//	}
	
	void doLoadBefore(Component comp, String command) {
		final List<LoadChildrenBinding> bindings = _loadBeforeBindings.get(command);
		if (bindings != null) {
			for (LoadChildrenBinding binding : bindings) {
				doLoadBinding(comp, binding, command);
			}
		}
	}
	
	void doLoadAfter(Component comp, String command) {
		final List<LoadChildrenBinding> bindings = _loadAfterBindings.get(command);
		if (bindings != null) {
			for (LoadChildrenBinding binding : bindings) {
				doLoadBinding(comp, binding, command);
			}
		}
	}
	
	void removeBindings(BindingKey bkey, Set<Binding> removed) {
		List<? extends Binding> bindingx;
		if((bindingx = _initBindings.remove(bkey)) !=null){
			removed.addAll(bindingx); //comp+_fieldExpr -> bindings (load _prompt)
		}
		if((bindingx = _loadPromptBindings.remove(bkey)) !=null){
			removed.addAll(bindingx); //comp+_fieldExpr -> bindings (load _prompt)
		}
//		if((bindingx = _loadEventBindings.remove(bkey)) !=null){
//			removed.addAll(bindingx); //comp+evtnm -> bindings (load on event)
//		}
	}

	void removeBindings(Collection<Binding> removes) {
		for(List<LoadChildrenBinding> bindings:_loadAfterBindings.values()){
			bindings.removeAll(removes); //command -> bindings (load after command)
		}
		for(List<LoadChildrenBinding> bindings:_loadBeforeBindings.values()){
			bindings.removeAll(removes); //command -> bindings (load before command)
		}
	}

	void doLoad(Component comp, BindingKey bkey) {
		final List<LoadChildrenBinding> bindings = _loadPromptBindings.get(bkey);
		if (bindings != null) {
			for (LoadChildrenBinding binding : bindings) {
				doLoadBinding(comp,binding,null);
			}
		}
	}
	
	void doInit(Component comp,BindingKey bkey) {
		final List<InitChildrenBinding> initBindings = _initBindings.get(bkey);
		if (initBindings != null) {
			for (InitChildrenBinding binding : initBindings) {
				doLoadBinding(comp, binding,null);
			}
		}
	}

	public boolean hasLoadBinding(BindingKey bkey) {
		return _initBindings.size() > 0 || _loadPromptBindings.size() > 0
				|| _loadAfterBindings.size() > 0 || _loadBeforeBindings.size() > 0;
	}
	
}
