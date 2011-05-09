/* BindingValidateEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 9, 2011 10:57:01 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * Event used when DataBinder send "onBindingValidate" events.
 * @author jumperchen
 * @since 5.0.7
 */
public class BindingValidateEvent extends Event {
	private final List _values;
	private final List _refs;
	private final List _bindings;

	/** Constructs a binding-relevant event.
	 * @param name the event name
	 * @param target the target that receive the event
	 * @param references the list of the reference components that "trigger" the event
	 * @param bindings the list of the associated bindings of this event.
	 * @param values the list of the values associated with the binding.
	 */
	public BindingValidateEvent(String name, Component target, List references, List bindings, List values) {
		super(name, target);
		_refs = references;
		_bindings = bindings;
		_values = values;
	}
	
	/** Gets the list of the reference components that "trigger" sending of this event.
	 */
	public List getReferences() {
		return _refs;
	}
	
	/** Gets the list of the associate bindings of this event.
	 * <p>Note: the binding instance is used for system-level developer, rarely
	 * for application developer.
	 */
	public List getBindings() {
		return _bindings;
	}
	
	/** Gets the list of the value to be saved after "onBindingValidate" event.
	 */
	public List getValues() {
		return _values;
	}
}
