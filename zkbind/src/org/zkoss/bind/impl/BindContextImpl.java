/* BindContextImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 2, 2011 1:09:16 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.sys.Binding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * Implementation of {@link BindContext}.
 * @author henrichen
 * @since 6.0.0
 */
public class BindContextImpl implements BindContext {
	private final Binder _binder;
	private final Binding _binding;
	private final boolean _save;
	private final String _command;
	private final Component _component; //ZK context
	private final Event _event; //ZK event
	private final Map<Object, Object> _attrs;
	
	public static final String COMMAND_ARGS = "$BC_CMDARGS$";
	public static final String BINDING_ARGS = "$BC_BINDARGS$";
	public static final String VALIDATOR_ARGS = "$BC_VALIDARGS$";
	public static final String CONVERTER_ARGS = "$BC_CONVARGS$";
	
	public BindContextImpl(Binder binder, Binding binding, boolean save, String command, Component comp, Event event) {
		this._binder = binder;
		this._binding = binding;
		this._save = save;
		this._command = command;
		this._component = comp;
		this._event = event;
		this._attrs = new HashMap<Object, Object>();
	}
	public Binder getBinder() {
		return this._binder;
	}

	public Binding getBinding() {
		return this._binding;
	}

	public Object getAttribute(Object key) {
		return this._attrs.get(key);
	}

	public Object setAttribute(Object key, Object value) {
		return value == null ? 
				this._attrs.remove(key) : this._attrs.put(key, value);
	}

	public Map<Object, Object> getAttributes() {
		return Collections.unmodifiableMap(_attrs); 
	}
	
	public Object getCommandArg(String key){
		Map<?, ?> m = (Map<?, ?>)getAttribute(COMMAND_ARGS);
		return m==null?null:m.get(key);
	}
	public Object getBindingArg(String key){
		Map<?, ?> m = (Map<?, ?>)getAttribute(BINDING_ARGS);
		return m==null?null:m.get(key);
	}
	public Object getConverterArg(String key){
		Map<?, ?> m = (Map<?, ?>)getAttribute(CONVERTER_ARGS);
		return m==null?null:m.get(key);
	}
	public Object getValidatorArg(String key){
		Map<?, ?> m = (Map<?, ?>)getAttribute(VALIDATOR_ARGS);
		return m==null?null:m.get(key);
	}

	public boolean isSave() {
		return this._save;
	}

	public String getCommandName() {
		return this._command;
	}

	public Component getComponent() {
		return this._component;
	}
	
	public Event getTriggerEvent() {
		return this._event;
	}
}
