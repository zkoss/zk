/* ValidationContextImpl.java

	Purpose:
		
	Description:
		
	History:
		2011/9/29 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.impl;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
/**
 * the default implementation of validation context
 * @author dennis
 * @since 6.0.0
 */
public class ValidationContextImpl implements ValidationContext{

	private boolean _valid = true;//default validation result is true
	private String _command;
	private Property _property; //main property
	private Map<String,Property[]> _properties; //related properties
	private BindContext _ctx;
	
	private Map<Property,String> messages;
	
	public ValidationContextImpl(String command, Property property,Map<String,Property[]> properties, BindContext ctx,boolean valid){
		this._command = command;
		this._property = property;
		this._properties = properties;
		this._ctx = ctx;
		this._valid = valid;
	}

	public BindContext getBindContext() {
		return _ctx;
	}

	public String getCommand() {
		return _command;
	}

	public Map<String,Property[]> getProperties() {
		return _properties;
	}
	
	public Property[] getProperties(String name) {
		return _properties.get(name);
	}

	public Property getProperty() {
		return _property;
	}


	public boolean isValid() {
		return _valid;
	}

//	public void setValid(boolean valid) {
//		this.valid = valid;
//	}
	
	public void setInvalid(){
		this._valid = false;
	}

	//TODO
	public void setMessage(Property property, String message) {
		if(messages==null){
			messages = new HashMap<Property, String>();
		}
		messages.put(property, message);
	}
	
	//TODO
	public String getMessage(Property property){
		return messages==null?null:messages.get(property);
	}


}
