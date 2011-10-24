/* BindContext.java

	Purpose:
		
	Description:
		
	History:
		Jun 22, 2011 9:50:12 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.util.Map;

import org.zkoss.bind.sys.Binding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * Binding Context
 * @author henrichen
 *
 */
public interface BindContext {
	/** 
	 * Returns associated Binder of this Bind context.
	 * @return associated Binder of this Bind context.
	 */
	public Binder getBinder();
	/**
	 * Returns associated Binding of this Bind context.
	 * @return associated Binding of this Bind context.
	 */
	public Binding getBinding();
	/**
	 * Returns value of the given key in this Bind Context.
	 * @param key the key to the value.
	 * @return value of the given key in this Bind Context.
	 */
	public Object getAttribute(Object key);
	/**
	 * Sets given value to the given key in this Bind context. 
	 * @param key the key to the value
	 * @param value the value
	 * @return previous value that associated with the given key.
	 */
	public Object setAttribute(Object key, Object value);
	/**
	 * Returns a copy of all attributes in this Bind context.
	 * @return a copy of all attributes in this Bind context.
	 */
	public Map<Object, Object> getAttributes();
	/**
	 * Returns whether currently is doing save operation.
	 * @return whether currently is doing save operation.
	 */
	public boolean isSave(); //is doing a save
	/**
	 * Returns associated command name of this Bind Context; null if not involved.
	 * @return associated command name of this Bind Context; null if not involved.
	 */
	public String getCommandName(); //the command that trigger the binding process
	
	/**
	 * Returns the associated component context.
	 * @return the associated component context.
	 */
	public Component getComponent();
	
	/**
	 * Returns associated event that trigger the associated command; null if not involved.
	 * @return associated event that trigger the associated command; null if not involved.
	 */
	public Event getTriggerEvent();
	
	/**
	 * Returns command arg value of the given key in this Bind Context.
	 * @param key the key to the value.
	 * @return value of command arg of the given key in this Bind Context.
	 */
	public Object getCommandArg(String key); 
	
	/**
	 * Returns binding arg value of the given key in this Bind Context.
	 * @param key the key to the value.
	 * @return value of binding arg of the given key in this Bind Context.
	 */
	public Object getBindingArg(String key);
	
	/**
	 * Returns converter arg value of the given key in this Bind Context.
	 * @param key the key to the value.
	 * @return value of converter arg of the given key in this Bind Context.
	 */
	public Object getConverterArg(String key);
	
	/**
	 * Returns validator arg value of the given key in this Bind Context.
	 * @param key the key to the value.
	 * @return value of validator arg of the given key in this Bind Context.
	 */
	public Object getValidatorArg(String key);
}
