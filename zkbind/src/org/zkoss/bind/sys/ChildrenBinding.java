/* PropertyBinding.java

	Purpose:
		
	Description:
		
	History:
		2012/1/2 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys;


/**
 * A binding tells how to deal with Load children of a source object
 * (usually an UI component) and a property of a target object(usually a backing bean).
 * @author dennis
 * @since 6.0.0
 */
public interface ChildrenBinding extends Binding {
		
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
	 * Returns the condition type of this binding
	 */
	public ConditionType getConditionType();
}
