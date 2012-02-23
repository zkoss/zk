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
	 * get dependent properties that need to be validated.
	 * you usually use this method to get value of other properties to do complex validation or
	 * a form validation
	 * @return the properties map.
	 */
	Map<String,Property[]> getProperties();
	
	/**
	 * get dependent properties by the property name.
	 * you usually use this method to get a value of other properties to do complex validation or
	 * a form validation
	 * @param name the property name
	 * @return the properties array
	 */
	Property[] getProperties(String name);
	
	
	/**
	 * get dependent properties by a base object.
	 * this method returns a Map (key is the property name) of properties that have same base object. 
	 * It is useful in the form validation case to get all dependent property that related to a form.
	 * For example in a validator of a form, <pre><code>
	 * Map<String,Property> beanProps = ctx.getProperties(ctx.getProperty().getBase());
	 * Map<String,Property> formProps = ctx.getProperties(ctx.getProperty().getValue());
	 * </code></pre>
	 * @param base the base object of properties
	 * @return a Map of properties that has same base object.
	 * @since 6.0.1
	 */
	Map<String,Property> getProperties(Object base);
	
	
	/**
	 * Returns validator arg value of the given key
	 * This is a shortcut of <code>getBindContext().getValidatorArg()</code> 
	 * @param key the key to the value.
	 * @return value of validator arg of the given key
	 * @since 6.0.1
	 */
	public Object getValidatorArg(String key);

	/**
	 * get the main property that need to be validated. 
	 * @return the main property.
	 */
	Property getProperty();
	

	/**
	 * @return current bind context
	 */
	BindContext getBindContext();

}
