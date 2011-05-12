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
	private final List<Object> _values;
	private final List<Component> _refs;
	private final List<Binding> _bindings;

	/** Constructs a binding-relevant event.
	 * @param name the event name
	 * @param target the target that receive the event
	 * @param references the list of the reference components that "trigger" the event
	 * @param bindings the list of the associated bindings of this event.
	 * @param values the list of the values associated with the binding.
	 */
	public BindingValidateEvent(String name, Component target,
	List<Component> references, List<Binding> bindings, List<Object> values) {
		super(name, target);
		_refs = references;
		_bindings = bindings;
		_values = values;
	}
	
	/** Gets the list of the reference components that "trigger" sending of this event.
	 */
	public List<Component> getReferences() {
		return _refs;
	}
	
	/** Gets the list of the associate bindings of this event ({@link Binding}).
	 * The property that causes the binding can be found by use of
	 * {@link Binding#getExpression}.
	 * Furthermore, the bean can be retrieved by use of 
	 * {@link Binding#getBean}.
	 */
	public List<Binding> getBindings() {
		return _bindings;
	}
	
	/** Gets the list of the value to be saved after "onBindingValidate" event.
	 */
	public List<Object> getValues() {
		return _values;
	}
}
