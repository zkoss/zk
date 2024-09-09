/* FormBindingHelper.java

	Purpose:

	Description:

	History:
		2011/11/14 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Phase;
import org.zkoss.bind.Property;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.InitFormBinding;
import org.zkoss.bind.sys.LoadFormBinding;
import org.zkoss.bind.sys.SaveFormBinding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * to help form-binding implementation of BinderImpl
 * @author dennis
 * @since 6.0.0
 */
/*package*/ class FormBindingHandler extends AbstractBindingHandler {

	private static final long serialVersionUID = 1L;

	private static final Logger _log = LoggerFactory.getLogger(FormBindingHandler.class);

	private final Map<BindingKey, List<InitFormBinding>> _initFormBindings; //comp+formid -> bindings (load form _prompt)
	private final Map<BindingKey, List<LoadFormBinding>> _loadFormPromptBindings; //comp+formid -> bindings (load form _prompt)
	private final Map<String, List<LoadFormBinding>> _loadFormAfterBindings; //command -> bindings (load form after command)
	private final Map<String, List<SaveFormBinding>> _saveFormAfterBindings; //command -> bindings (save form after command)
	private final Map<String, List<LoadFormBinding>> _loadFormBeforeBindings; //command -> bindings (load form before command)
	private final Map<String, List<SaveFormBinding>> _saveFormBeforeBindings; //command -> bindings (save form before command)

	FormBindingHandler() {
		_initFormBindings = new HashMap<BindingKey, List<InitFormBinding>>();
		_loadFormPromptBindings = new HashMap<BindingKey, List<LoadFormBinding>>();
		_loadFormAfterBindings = new HashMap<String, List<LoadFormBinding>>();
		_saveFormAfterBindings = new HashMap<String, List<SaveFormBinding>>();
		_loadFormBeforeBindings = new HashMap<String, List<LoadFormBinding>>();
		_saveFormBeforeBindings = new HashMap<String, List<SaveFormBinding>>();
	}

	void addLoadPromptBinding(BindingKey bkey, LoadFormBinding binding) {
		addBinding(_loadFormPromptBindings, bkey, binding); //ZK-2289
	}

	void addInitBinding(BindingKey bkey, InitFormBinding binding) {
		addBinding(_initFormBindings, bkey, binding); //ZK-2289
	}

	void addLoadBeforeBinding(String command, LoadFormBinding binding) {
		addBinding(_loadFormBeforeBindings, command, binding); //ZK-2289
	}

	void addLoadAfterBinding(String command, LoadFormBinding binding) {
		addBinding(_loadFormAfterBindings, command, binding); //ZK-2289
	}

	void addSaveBeforeBinding(String command, SaveFormBinding binding) {
		addBinding(_saveFormBeforeBindings, command, binding); //ZK-2289
	}

	void addSaveAfterBinding(String command, SaveFormBinding binding) {
		addBinding(_saveFormAfterBindings, command, binding); //ZK-2289
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
	private void doSaveBinding(Component comp, SaveFormBinding binding, String command, Event evt,
			Set<Property> notifys) {
		final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, true, command, binding.getComponent(),
				evt);
		BindContextUtil.setValidatorArgs(_binder, binding.getComponent(), ctx, binding);
		//TODO converter args when we support converter in form
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(getSaveBindingDebugInfo("doSaveFormBinding", comp, binding, command, evt, notifys));
			}
			doPrePhase(Phase.SAVE_BINDING, ctx);
			binding.save(ctx);
		} catch (Exception ex) {
			throw new RuntimeException(getSaveBindingDebugInfo("doSaveFormBinding", comp, binding, command, evt, notifys), ex);
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
		final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, false, command, binding.getComponent(),
				null);
		if (binding instanceof InitFormBindingImpl) {
			ctx.setAttribute(BinderImpl.IGNORE_TRACKER, Boolean.TRUE); //ignore tracker when doing el , we don't need to track the init
		}
		//TODO converter args when we support converter in form
		try {
			if (_log.isDebugEnabled()) {
				_log.debug(getLoadBindingDebugInfo("doLoadFormBinding", comp, binding, ctx, command));
			}
			doPrePhase(Phase.LOAD_BINDING, ctx);
			binding.load(ctx);

			//if there is a validator, clear the validation message after load
			if (((BinderImpl) binding.getBinder()).hasValidator(binding.getComponent(), binding.getFormId())) {
				clearValidationMessages(binding.getBinder(), binding.getComponent(), binding.getFormId());
			}
		} catch (Exception ex) {
			throw new RuntimeException(getLoadBindingDebugInfo("doLoadFormBinding", comp, binding, ctx, command), ex);
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

	void removeBindings(BindingKey bkey, Set<Binding> removed) {
		List<? extends Binding> bindingx;
		if ((bindingx = _initFormBindings.remove(bkey)) != null) {
			removed.addAll(bindingx); //comp+_fieldExpr -> bindings (load _prompt)
		}
		if ((bindingx = _loadFormPromptBindings.remove(bkey)) != null) {
			removed.addAll(bindingx); //comp+formid -> bindings (load form _prompt)
		}
	}

	void removeBindings(Collection<Binding> removes) {
		for (List<LoadFormBinding> bindings : _loadFormAfterBindings.values()) {
			bindings.removeAll(removes); //command -> bindings (load form after command)
		}
		for (List<SaveFormBinding> bindings : _saveFormAfterBindings.values()) {
			bindings.removeAll(removes); //command -> bindings (save form after command)
		}
		for (List<LoadFormBinding> bindings : _loadFormBeforeBindings.values()) {
			bindings.removeAll(removes); //command -> bindings (load form before command)
		}
		for (List<SaveFormBinding> bindings : _saveFormBeforeBindings.values()) {
			bindings.removeAll(removes); //command -> bindings (save form before command)
		}
	}

	void doLoad(Component comp, BindingKey bkey) {
		final List<LoadFormBinding> formBindings = _loadFormPromptBindings.get(bkey);
		if (formBindings != null) {
			for (LoadFormBinding binding : formBindings) {
				doLoadBinding(comp, binding, null);
			}
		}
	}

	void doInit(Component comp, BindingKey bkey) {
		final List<InitFormBinding> initBindings = _initFormBindings.get(bkey);
		if (initBindings != null) {
			for (InitFormBinding binding : initBindings) {
				doLoadBinding(comp, binding, null);
			}
		}
	}
}
