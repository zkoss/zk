/* Binder.java

	Purpose:
		
	Description:
		
	History:
		Jun 22, 2011 9:54:23 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.util.Map;

import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.tracker.Tracker;
import org.zkoss.zk.ui.Component;

/**
 * The Binder that do the data binding things.
 * @author henrichen
 *
 */
public interface Binder {
	/**
	 * Returns the {@link BindEvaluatorX} used by this Binder. 
	 * @return the EvaluatorX.
	 */
	public BindEvaluatorX getEvaluatorX();

	/**
	 * Add a new command bindins.
	 * @param comp the associated component
	 * @param evtnm the associated component event name
	 * @param commandExpr the command expression
	 * @param commandArgs other key-value pairs pairs for command
	 */
	public void addCommandBinding(Component comp, String evtnm, String commandExpr, Map<String, Object> commandArgs);
	
	/**
	 * Add new form Bindings. 
	 * @param comp the associated component, must not null
	 * @param id the form id, must not null
	 * @param initExpr init expressions ; null to ignore it ; the return value of initExpr must be a {@link Form} instance
	 * @param loadExprs load expressions
	 * @param saveExprs save expressions
	 * @param validatorExpr the provided validator expression; null to ignore it 
	 * @param bindingArgs args key-value pairs for binding. 
	 * @param validatorArgs args key-value pairs for validator
	 */
	public void addFormBindings(Component comp, String id, String initExpr,
			String[] loadExprs, String[] saveExprs, String validatorExpr, Map<String, Object> bindingArgs, Map<String, Object> validatorArgs);

	/**
	 * Add new property Bindings.
	 * @param comp the associated component, must not null
	 * @param attr the associated attribute of the component; ex label, style
	 * @param initExpr init expressions ; null to ignore it
	 * @param loadExprs load expressions
	 * @param saveExprs save expressions
	 * @param converterExpr the provided converter expression; null to ignore it.
	 * @param validatorExpr the provided validator expression; null to ignore it.
	 * @param bindingArgs args key-value pairs for binding. 
	 * @param converterArgs args key-value pairs for converter
	 * @param validatorArgs args key-value pairs for validator
	 */
	public void addPropertyBinding(Component comp, String attr, String initExpr,
			String[] loadExprs, String[] saveExprs, String converterExpr, String validatorExpr, Map<String, Object> bindingArgs,Map<String, Object> converterArgs,Map<String, Object> validatorArgs);

	/**
	 * Remove all managed bindings that associated with the specified component.
	 * @param comp
	 */
	public void removeBindings(Component comp);
	
	/**
	 * Remove all managed Binding that associated with the specified 
	 * component and attribute name, event name, or form id. 
	 * @param comp the associated component
	 * @param key the associated attribute name, event name, or form id
	 */
	public void removeBindings(Component comp, String key);
	
	/**
	 * Returns the _converter of the given _converter name.
	 * @param name _converter name
	 * @return the _converter of the given _converter name.
	 */
	public Converter getConverter(String name);
	
	/**
	 * Returns the _validator of the given _validator name.
	 * @param name _validator name
	 * @return the _validator of the given _validator name.
	 */
	public Validator getValidator(String name);
	
	/**
	 * Notify change of the property.
	 * @param bean the backing bean object.
	 * @param property the property of the bean that change the value 
	 */
	public void notifyChange(Object bean, String property);
	
	
	/**
	 * send command fired to this binder and process the command immediately
	 * @param command command name
	 * @param args , arguments when notifing this command, it will be passed as a arguments of execution method of vm
	 */
	public void sendCommand(String command, Map<String, Object> args);
	
	/**
	 * post command this binder, binder will queue the command, and fired later.
	 * @param command command name
	 * @param args , arguments when notifing this command, it will be passed as a arguments of execution method of vm
	 */
	public void postCommand(String command, Map<String, Object> args);
	
	/**
	 * Returns associated ViewModel of this binder.
	 * @return associated ViewModel of this binder.
	 */
	public Object getViewModel();
	
	/**
	 * Sets associated ViewModel of this binder.
	 * @param viewModel the associated view model of this binder.
	 */
	public void setViewModel(Object viewModel);

	/**
	 * Sets the associated phase listener to intervene the binding life cycle.
	 * @param listener the associated phase listener.
	 */
	public void setPhaseListener(PhaseListener listener);
	
	/**
	 * Returns associated dependency tracker of this binder.
	 * @return associated dependency tracker of this binder.
	 */
	public Tracker getTracker();
}
