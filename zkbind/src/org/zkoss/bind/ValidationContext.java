/* ValidationContext.java

	Purpose:
		
	Description:
		
	History:
		2011/9/29 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind;

import java.util.Map;

/**
 * The context for validation 
 * @author dennis
 * @since 6.0.0
 */
public interface ValidationContext {

	/** 
	 * is valid 
	 * @return true of result is valid, Note, default is true if no {@link Validator} called {@link #setInvalid()}
	 */
	boolean isValid();

	/**
	 * set invalid
	 */
	void setInvalid();

	/**
	 * get the command that trigger the validation
	 * @return the command, null if a prompt-save-binding
	 */
	String getCommand();

	/**
	 * get collected properties that need to be validated.
	 * you usually use this method to get value of other properties to do complex validation or
	 * a form validation
	 * @return the properties map.
	 */
	Map<String,Property[]> getProperties();
	
	/**
	 * get collected properties that need to be validated by a property name.
	 * you usually use this method to get a value of other properties to do complex validation or
	 * a form validation
	 * @param name the property name
	 * @return the properties array
	 */
	Property[] getProperties(String name);

	/**
	 * get the main property that need to be validated. 
	 * @return the main property.
	 */
	Property getProperty();
	

	/**
	 * @return current bind context
	 */
	BindContext getBindContext();

	//TODO
//	/**
//	 * the error message to property
//	 * @param property the property that message will attached to
//	 * @param message the message
//	 */
//	void setMessage(Property property, String message);
}
