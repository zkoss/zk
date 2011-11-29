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
	 * init a component property by a expression, it only execute once
	 * 
	 * @param comp the associated component, must not null
	 * @param attr the associated attribute of the component; ex label, style, must not null
	 * @param initExpr init expression, must not null
	 * @param initArgs args key-value pairs for initial, nullable
	 * @param converterExpr the converter expression, nullable
	 * @param converterArgs args key-value pairs for converter, nullable
	 */
	public void initProperty(Component comp,String attr, String initExpr, Map<String,Object> initArgs, 
			String converterExpr, Map<String, Object> converterArgs);
	
	
	/**
	 * Add new property-load-bindings.
	 * It creates a prompt|conditional property-load-binding depends on beforeCmds and afterCmds.
	 * If both beforeCmds and afterCmds are null or empty, it create a prompt binding.
	 * 
	 * @param comp the associated component, must not null
	 * @param attr the associated attribute of the component; ex label, style, must not null
	 * @param loadExpr load expression, must not null
	 * @param beforeCmds load before these commands, nullable
	 * @param afterCmds load after these commands, nullable
	 * @param bindingArgs args key-value pairs for this binding, nullable
	 * @param converterExpr the converter expression, nullable
	 * @param converterArgs args key-value pairs for converter, nullable
	 */
	public void addPropertyLoadBindings(Component comp,String attr,
			String loadExpr, String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs,
			String converterExpr, Map<String, Object> converterArgs);
	
	/**
	 * Add new property-save-bindings. 
	 * It creates a prompt|conditional property-save-binding depends on beforeCmds and afterCmds.
	 * If both beforeCmds and afterCmds are null or mepty, it create a prompt binding.
	 * 
	 * @param comp the associated component, must not null
	 * @param attr the associated attribute of the component; ex value, check, must not null
	 * @param saveExpr save expression, nullable
	 * @param beforeCmds save before these commands, nullable
	 * @param afterCmds save after these commands, nullable
	 * @param bindingArgs args key-value pairs for this binding, nullable
	 * @param converterExpr the converter expression, nullable
	 * @param converterArgs args key-value pairs for converter, nullable
	 * @param validatorExpr the converter expression, nullable
	 * @param validatorArgs args key-value pairs for validator, nullable
	 */
	public void addPropertySaveBindings(Component comp, String attr,
			String saveExpr,String[] beforeCmds, String[] afterCmds, Map<String, Object> bindingArgs,
			String converterExpr, Map<String, Object> converterArgs,
			String validatorExpr, Map<String, Object> validatorArgs);
	
	/**
	 * init a component form by expression, it only execute once
	 * 
	 * @param comp the associated component, must not null
	 * @param id the form id, must not null
	 * @param initExpr init expression, nullable
	 * @param initArgs args key-value pairs for this init, nullable
	 */
	public void initForm(Component comp,String id,String initExpr, Map<String, Object> initArgs);
	
	/**
	 * Add new form-load-bindings.
	 * It create a prompt|conditional form-load-binding depends on beforeCmds and afterCmds.
	 * If both beforeCmds and afterCmds are null or empty, it create a prompt binding.
	 * 
	 * @param comp the associated component, must not null
	 * @param id the form id, must not null
	 * @param initExpr init expression, nullable
	 * @param loadExpr load expression, nullable
	 * @param beforeCmds load before these commands, nullable
	 * @param afterCmds load after these commands, nullable
	 * @param bindingArgs args key-value pairs for this binding, nullable
	 */	
	public void addFormLoadBindings(Component comp,String id,
			String loadExpr,String[] beforeCmds,String[] afterCmds, Map<String, Object> bindingArgs);
	
	/**
	 * Add new form-save-bindings. 
	 * It create a conditional form-save-binding depends on beforeCmds and afterCmds.
	 * Since form-save-binding is always triggered by command, if both beforeCmds and afterCmds are null or empty, it throws an exception.
	 *  
	 * @param comp the associated component, must not null
	 * @param id the form id, must not null
	 * @param saveExpr save expression, nullable
	 * @param beforeCmds save before these commands, nullable
	 * @param afterCmds save after these commands, nullable
	 * @param bindingArgs args key-value pairs for this binding, nullable
	 * @param validatorExpr the converter expression, nullable
	 * @param validatorArgs args key-value pairs for validator, nullable
	 * @throws IllegalArgumentException if beforeCmds or afterCmds are both null or empty
	 */
	public void addFormSaveBindings(Component comp,String id,
			String saveExpr,String[] beforeCmds,String[] afterCmds, Map<String, Object> bindingArgs,
			String validatorExpr, Map<String, Object> validatorArgs);

	
	
//	public void addChildrenBinding(Component comp, String initExpr, String loadExpr, Map<String, Object> bindingArgs);
//	public void addReferenceBinding(Component comp, String id, String initExpr, String loadExpr, Map<String, Object> bindingArgs);
	
	
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
