/* BranchOutput.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 9, 2007 12:42:46 PM     2007, Created by Dennis.Chen
 }}IS_NOTE

 Some code of this file is refer to Common Development and Distribution License
 the license file is https://javaserverfaces.dev.java.net/CDDL.html  

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.jsf.zul.impl;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;

import org.zkoss.lang.reflect.Fields;
import org.zkoss.zk.ui.Component;

/**
 *  The skeletal class used to implement the ZULJSF components
 *  which needs to support {@link javax.faces.component.ValueHolder}.<br/>
 *  This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * @author Dennis.Chen
 */
abstract public class BranchOutput extends BranchComponent implements ValueHolder,ValueHolderSupport {

	
	//------------code refer to UIOuput in JSF RI-----------------
	protected Converter _converter;
	protected Object _value;

	/**
	 * The "localValueSet" state for this component.
	 */
	private boolean _localValueSet;

	/**
	 * Return the "local value set" state for this component. Calls to
	 * setValue() automatically reset this property to
	 * true.
	 */
	public boolean isLocalValueSet() {
		return _localValueSet;
	}

	/**
	 * Sets the "local value set" state for this component.
	 */
	public void setLocalValueSet(boolean localValueSet) {
		this._localValueSet = localValueSet;
	}

	/**
	 * get Convert of this component.
	 */
	public Converter getConverter() {

		if (this._converter != null) {
			return (this._converter);
		}
		ValueBinding vb = getValueBinding("converter");
		if (vb != null) {
			return ((Converter) vb.getValue(getFacesContext()));
		} else {
			return (null);
		}

	}

	/**
	 * set Converter of this Component.
	 * 
	 */
	public void setConverter(Converter converter) {

		this._converter = converter;

	}

	/**
	 * get local value directly , ignore the ValueBinding; 
	 */
	public Object getLocalValue() {
		return (this._value);
	}

	
	public Object getValue() {

		//modify dennis, maybe use set null to value,
		if (_localValueSet/*this._value != null*/) {
			return (this._value);
		}
		ValueBinding vb = getValueBinding("value");
		if (vb != null) {
			return (vb.getValue(getFacesContext()));
		} else {
			return (null);
		}

	}

	public void setValue(Object value) {

		this._value = value;
		_localValueSet = true;

	}

	// ----------------------------------------------------- StateHolder Methods
	/**
	 * Override Method, save the state of this component.
	 */
	public Object saveState(FacesContext context) {

		Object values[] = new Object[4];
		values[0] = super.saveState(context);
		values[1] = saveAttachedState(context, _converter);
		values[2] = _value;
		values[3] = _localValueSet?Boolean.TRUE:Boolean.FALSE;
		return (values);

	}

	/**
	 * Override Method, restore the state of this component.
	 */
	public void restoreState(FacesContext context, Object state) {

		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		_converter = (Converter) restoreAttachedState(context, values[1]);
		_value = values[2];
		_localValueSet = ((Boolean)values[3]).booleanValue();

	}
	
	//	------------end of code refer--------------------
	
	/**
	 * Override Method,
	 * if instance implements {@link ValueHolderSupport} (always for now), 
	 * then i will try to set the value from ValueHolder to ZUL Component.
	 * 
	 */
	protected void afterZULComponentComposed(Component zulcomp){
		super.afterZULComponentComposed(zulcomp);
		
		//check if need to put value attribute of ValueHolder to ZUL
		if(this instanceof ValueHolderSupport && (isLocalValueSet() || getValueBinding("value")!=null)){
			Object value = null;
			String att = ((ValueHolderSupport)this).getMappedAttributeName();
			if(att!=null){
				if(this instanceof EditableValueHolder && !((EditableValueHolder)this).isValid()){
					value = ((EditableValueHolder)this).getSubmittedValue();
					try {
						if(value!=null && value instanceof String){
							value = transferValueForAttribute((String)value);
						}
						Fields.setField(zulcomp,att,value,true);
						return;
					} catch (Exception x){
						//when invalid, I ingore the exception and set to orginal value by getValue.
					}
				}
				
				value = getValue();
				Converter converter = getConverter();
				if(converter!=null){
					value = converter.getAsString(getFacesContext(),this, value);
				}
				
				try {
					if(value!=null && value instanceof String){
						value = transferValueForAttribute((String)value);
					}
					Fields.setField(zulcomp,att,value,true);
				} catch (Exception x){
					throw new RuntimeException("set Field fail at attribute :"+att,x);
				}
			}
		}
	}
	
	/**
	 * Return ZUL Component attribute to map to value attribute of ValueHolder,
	 * delivering class might override this method to return corresponding name.
	 *  
	 * Note : Default is "value"
	 * 
	 * @see ValueHolderSupport
	 */
	public String getMappedAttributeName(){
		return "value";
	}
	
	/**
	 * return orginal String
	 * @see ValueHolderSupport
	 */
	public Object transferValueForAttribute(String value){
		return value;
	}

}
