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
import java.util.Map.Entry;

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
	
	private static final String BASED_VALIDATION_PROPERTIES = "$BASED_VALIDATION_PROPS$";
	
	public ValidationContextImpl(String command, Property property,Map<String,Property[]> properties, BindContext ctx,boolean valid){
		this._command = command;
		this._property = property;
		this._properties = properties;
		this._ctx = ctx;
		this._valid = valid;
	}

	@Override
	public BindContext getBindContext() {
		return _ctx;
	}

	@Override
	public String getCommand() {
		return _command;
	}

	@Override
	public Map<String,Property[]> getProperties() {
		return _properties;
	}
	
	@Override
	public Property[] getProperties(String name) {
		return _properties.get(name);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String,Property> getProperties(Object base){
		Map<Object,Map<String,Property>> m =  (Map<Object,Map<String,Property>>)_ctx.getAttribute(BASED_VALIDATION_PROPERTIES);
		if(m==null){
			_ctx.setAttribute(BASED_VALIDATION_PROPERTIES,m = new HashMap<Object,Map<String,Property>>());
		}
			
		Map<String,Property> mp = m.get(base);
		if(mp!=null) return mp;
		mp = new HashMap<String,Property>();
		m.put(base, mp);
		
		for(Entry<String, Property[]> e:_properties.entrySet()){
			for(Property p:e.getValue()){
				if(base.equals(p.getBase())){
					mp.put(e.getKey(), p);
				}
			}
		}
		return mp;
	}
	
	@Override
	public Object getValidatorArg(String key) {
		return _ctx.getValidatorArg(key);
	}
	
	@Override
	public Property getProperty() {
		return _property;
	}

	@Override
	public boolean isValid() {
		return _valid;
	}

	@Override
	public void setInvalid(){
		this._valid = false;
	}

}
