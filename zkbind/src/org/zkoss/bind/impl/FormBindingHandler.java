/* FormBindingHelper.java

	Purpose:
		
	Description:
		
	History:
		2011/11/14 Created by Dennis Chen

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
import org.zkoss.bind.Property;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.InitFormBinding;
import org.zkoss.bind.sys.LoadFormBinding;
import org.zkoss.bind.sys.SaveFormBinding;
import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * to help form-binding implementation of BinderImpl 
 * @author dennis
 *
 */
/*package*/ class FormBindingHandler extends AbstractBindingHandler{
	
	private static final long serialVersionUID = 1L;

	private static final Log _log = Log.lookup(FormBindingHandler.class);
	
	private final Map<BindingKey, List<InitFormBinding>> _initFormBindings; //comp+formid -> bindings (load form _prompt)
	private final Map<BindingKey, List<LoadFormBinding>> _loadFormPromptBindings; //comp+formid -> bindings (load form _prompt)
	private final Map<String, List<LoadFormBinding>> _loadFormAfterBindings; //command -> bindings (load form after command)
	private final Map<String, List<SaveFormBinding>> _saveFormAfterBindings; //command -> bindings (save form after command)
	private final Map<String, List<LoadFormBinding>> _loadFormBeforeBindings; //command -> bindings (load form before command)
	private final Map<String, List<SaveFormBinding>> _saveFormBeforeBindings; //command -> bindings (save form before command)
	
	FormBindingHandler(BinderImpl binder){
		super(binder);
		_initFormBindings = new HashMap<BindingKey, List<InitFormBinding>>();
		_loadFormPromptBindings = new HashMap<BindingKey, List<LoadFormBinding>>();
		_loadFormAfterBindings = new HashMap<String, List<LoadFormBinding>>();
		_saveFormAfterBindings = new HashMap<String, List<SaveFormBinding>>();
		_loadFormBeforeBindings = new HashMap<String, List<LoadFormBinding>>();
		_saveFormBeforeBindings = new HashMap<String, List<SaveFormBinding>>();
	}

	void addLoadPromptBinding(BindingKey bkey, LoadFormBinding binding) {
		List<LoadFormBinding> bindings = _loadFormPromptBindings.get(bkey); 
		if (bindings == null) {
			bindings = new ArrayList<LoadFormBinding>();
			_loadFormPromptBindings.put(bkey, bindings);
		}
		bindings.add(binding);
	}
	
	void addInitBinding(BindingKey bkey, InitFormBinding binding) {
		List<InitFormBinding> bindings = _initFormBindings.get(bkey); 
		if (bindings == null) {
			bindings = new ArrayList<InitFormBinding>();
			_initFormBindings.put(bkey, bindings);
		}
		bindings.add(binding);
	}

	void addLoadBeforeBinding(String command, LoadFormBinding binding) {
		List<LoadFormBinding> bindings = _loadFormBeforeBindings.get(command);
		if (bindings == null) {
			bindings = new ArrayList<LoadFormBinding>();
			_loadFormBeforeBindings.put(command, bindings);
		}
		bindings.add(binding);
	}

	void addLoadAfterBinding(String command, LoadFormBinding binding) {
		List<LoadFormBinding> bindings = _loadFormAfterBindings.get(command);
		if (bindings == null) {
			bindings = new ArrayList<LoadFormBinding>();
			_loadFormAfterBindings.put(command, bindings);
		}
		bindings.add(binding);
	}

	void addSaveBeforeBinding(String command, SaveFormBinding binding) {
		List<SaveFormBinding> bindings = _saveFormBeforeBindings.get(command);
		if (bindings == null) {
			bindings = new ArrayList<SaveFormBinding>();
			_saveFormBeforeBindings.put(command, bindings);
		}
		bindings.add(binding);
	}
	
	void addSaveAfterBinding(String command, SaveFormBinding binding) {
		List<SaveFormBinding> bindings = _saveFormAfterBindings.get(command);
		if (bindings == null) {
			bindings = new ArrayList<SaveFormBinding>();
			_saveFormAfterBindings.put(command, bindings);
		}
		bindings.add(binding);
	}

	Map<String, List<SaveFormBinding>> getSaveFormBeforeBindings() {
		return _saveFormBeforeBindings;
	}

	Map<String, List<SaveFormBinding>> getSaveFormAfterBindings() {
		return _saveFormAfterBindings;
	}
	
	void doSaveBefore(Component comp, String command, Event evt, Set<Property> notifys) {
		final List<SaveFormBinding> bindings = _saveFormBeforeBindings.get(command);
		if (bindings != null) {
			for (SaveFormBinding binding : bindings) {
				doSaveBinding(comp, binding, command, evt, notifys);
			}
		}
	}
	
	//generic operation to save a property binding
	private void doSaveBinding(Component comp, SaveFormBinding binding, String command, Event evt, Set<Property> notifys) {
		final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, true, command, binding.getComponent(), evt);
		BindContextUtil.setValidatorArgs(_binder, binding.getComponent(), ctx, binding);
		//TODO converter args when we support converter in form
		try {
			if(_log.debugable()){
				_log.debug("doSaveFormBinding:binding.save() comp=[%s],binding=[%s],command=[%s],evt=[%s],notifys=[%s]",comp,binding,command,evt,notifys);
			}
			doPrePhase(Phase.SAVE_BINDING, ctx);
			binding.save(ctx);
		} finally {
			doPostPhase(Phase.SAVE_BINDING, ctx);
		}
		
		final Set<Property> xnotifys = getNotifys(ctx);
		if (xnotifys != null) {
			notifys.addAll(xnotifys);
		}
	}
	
	//generic operation to load a property binding
	private void doLoadBinding(Component comp, LoadFormBinding binding, String command) {
		final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, false, command, binding.getComponent(), null);
		if(binding instanceof InitFormBindingImpl){
			ctx.setAttribute(BinderImpl.IGNORE_TRACKER, Boolean.TRUE);//ignore tracker when doing el , we don't need to track the init
		}
		//TODO converter args when we support converter in form
		try {
			if(_log.debugable()){
				_log.debug("doLoadFormBinding:binding.load(),component=[%s],binding=[%s],context=[%s],command=[%s]",comp,binding,ctx,command);
			}
			doPrePhase(Phase.LOAD_BINDING, ctx);
			binding.load(ctx);
		} finally {
			doPostPhase(Phase.LOAD_BINDING, ctx);
		}
	}

	void doSaveAfter(Component comp, String command, Event evt, Set<Property> notifys) {
		final List<SaveFormBinding> bindings = _saveFormAfterBindings.get(command);
		if (bindings != null) {
			for (SaveFormBinding binding : bindings) {
				doSaveBinding(comp, binding, command, evt, notifys);
			}
		}
	}

	void doLoadBefore(Component comp, String command) {
		final List<LoadFormBinding> bindings = _loadFormBeforeBindings.get(command);
		if (bindings != null) {
			for (LoadFormBinding binding : bindings) {
				doLoadBinding(comp, binding, command);
			}
		}
	}

	void doLoadAfter(Component comp, String command) {
		final List<LoadFormBinding> bindings = _loadFormAfterBindings.get(command);
		if (bindings != null) {
			for (LoadFormBinding binding : bindings) {
				doLoadBinding(comp, binding, command);
			}
		}
	}
	
	void removeBindings(BindingKey bkey,Set<Binding> removed) {
		List<? extends Binding> bindingx;
		if((bindingx = _initFormBindings.remove(bkey)) !=null){
			removed.addAll(bindingx); //comp+_fieldExpr -> bindings (load _prompt)
		}
		if((bindingx = _loadFormPromptBindings.remove(bkey)) !=null){
			removed.addAll(bindingx); //comp+formid -> bindings (load form _prompt)
		}
	}

	void removeBindings(Collection<Binding> bindings) {
		_loadFormAfterBindings.values().removeAll(bindings); //command -> bindings (load form after command)
		_saveFormAfterBindings.values().removeAll(bindings); //command -> bindings (save form after command)
		_loadFormBeforeBindings.values().removeAll(bindings); //command -> bindings (load form before command)
		_saveFormBeforeBindings.values().removeAll(bindings); //command -> bindings (save form before command)
	}

	void loadComponentProperties(Component comp, BindingKey bkey) {
		final List<LoadFormBinding> formBindings = _loadFormPromptBindings.get(bkey);
		if (formBindings != null) {
			for (LoadFormBinding binding : formBindings) {
				doLoadBinding(comp,binding,null);
			}
		}
	}
	
	void initComponentProperties(Component comp,BindingKey bkey) {
		final List<InitFormBinding> initBindings = _initFormBindings.get(bkey);
		if (initBindings != null) {
			for (InitFormBinding binding : initBindings) {
				doLoadBinding(comp, binding,null);
			}
		}
	}
}
