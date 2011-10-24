/* FormBinding.java

	Purpose:
		
	Description:
		
	History:
		Jul 26, 2011 4:00:09 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys;

import org.zkoss.bind.Form;

/**
 * A binding tells how to deal with Load and Save between a form and a bean.
 * @author henrichen
 *
 */
public interface FormBinding extends Binding {
	/**
	 * Returns the implicit form associated with this form binding.
	 * @return
	 */
	public Form getFormBean();
	
//	/**
//	 * Returns the associated form id of this Binding.
//	 * @return the associated attribute name of this component.
//	 */
//	public String getFormId();
	
	/**
	 * Returns the associated command name of this binding; null if not speicified.
	 * @return the associated command name of this binding; null if not speicified.
	 */
	public String getCommandName();
	
	/**
	 * Returns the property expression script of this binding.
	 * @return the property expression script of this binding. 
	 */
	public String getPropertyString();
	
	/**
	 * Returns whether bind this binding after execute associated command(true); 
	 * 	otherwise it shall bind before execute associated command(false).
	 * @return whether bind this binding after execute associated command(true); 
	 * 	otherwise it shall bind before execute associated command(false).
	 */
	public boolean isAfter();

}
