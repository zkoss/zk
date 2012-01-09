/* PropertyImpl.java

	Purpose:
		
	Description:
		
	History:
		Aug 12, 2011 12:45:49 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.io.Serializable;

import org.zkoss.bind.Property;

/**
 * Implementation of a property.
 * @author henrichen
 * @since 6.0.0
 */
public class PropertyImpl implements Property,Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	private final Object _base;
	private final String _property;
	private final Object _value;
	public PropertyImpl(Object base, String property, Object value) {
		_base = base;
		_property = property;
		_value = value;
	}

	public Object getBase() {
		return _base;
	}
	
	public Object getValue() {
		return _value;
	}

	public String getProperty() {
		return _property;
	}
	
	public String toString(){
		return new StringBuilder().append(getClass().getSimpleName()).append("@").append(Integer.toHexString(hashCode()))
		.append(",base:").append(getBase())
		.append(",property:").append(getProperty())
		.append(",value:").append(getValue()).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_base == null) ? 0 : _base.hashCode());
		result = prime * result
				+ ((_property == null) ? 0 : _property.hashCode());
		result = prime * result + ((_value == null) ? 0 : _value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyImpl other = (PropertyImpl) obj;
		if (_base == null) {
			if (other._base != null)
				return false;
		} else if (!_base.equals(other._base))
			return false;
		if (_property == null) {
			if (other._property != null)
				return false;
		} else if (!_property.equals(other._property))
			return false;
		if (_value == null) {
			if (other._value != null)
				return false;
		} else if (!_value.equals(other._value))
			return false;
		return true;
	}
	
	
}
