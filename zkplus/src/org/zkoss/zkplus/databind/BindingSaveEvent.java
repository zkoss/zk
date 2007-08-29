/* BindingSaveEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Aug  1 08:34:40 TST 2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * Event used when DataBinder send "onBindingSave" events.
 * @author henrichen
 * @since 3.0.0
 */
public class BindingSaveEvent extends Event {
	private final Object _value;
	private final Component _ref;
	private final Binding _binding;

	/** Constructs a binding-relevant event.
	 * @param name the event name
	 * @param target the target that receive the event
	 * @param reference the reference component that "trigger" the event
	 * @param binding the associated binding of this event.
	 * @param value the value associated with the binding.
	 */
	public BindingSaveEvent(String name, Component target, Component reference, Binding binding, Object value) {
		super(name, target);
		_ref = reference;
		_binding = binding;
		_value = value;
	}
	
	/** Gets the reference component that "trigger" sending of this event.
	 */
	public Component getReference() {
		return _ref;
	}
	
	/** Gets the associate binding of this event.
	 */
	public Binding getBinding() {
		return _binding;
	}
	
	/** Gets the value to be saved after "onBindingSave" event.
	 */
	public Object getValue() {
		return _value;
	}
}
