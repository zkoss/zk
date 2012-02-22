/* WrongValuePropertyImpl.java

	Purpose:
		
	Description:
		
	History:
		2012/2/22 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.io.Serializable;

import org.zkoss.bind.Property;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;

/**
 * // ZK-878 Exception if binding a form with errorMessage
 * To handle wrong value exception when getting a component value.
 * @author dennis
 * @since 6.0.1
 */
public class WrongValuePropertyImpl implements Property,Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	private final Object _base;
	private final String _property;
	private final Object _wrongValueException;
	public WrongValuePropertyImpl(Object base, String property, Object wrongValueException) {
		_base = base;
		_property = property;
		if (!(wrongValueException instanceof WrongValueException 
				|| wrongValueException instanceof WrongValuesException)) {
			throw new IllegalArgumentException("not a wrong value exception, is "+wrongValueException);
		}
		_wrongValueException = wrongValueException;
	}

	public Object getBase() {
		return _base;
	}
	
	public Object getValue() {
		return null;
	}

	public String getProperty() {
		return _property;
	}
	
	public WrongValueException[] getWrongValueExceptions(){
		if(_wrongValueException instanceof WrongValueException){
			return new WrongValueException[]{(WrongValueException)_wrongValueException};
		}else if(_wrongValueException instanceof WrongValuesException){
			return ((WrongValuesException)_wrongValueException).getWrongValueExceptions();
		}
		return null;
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
		result = prime * result + ((_wrongValueException == null) ? 0 : _wrongValueException.hashCode());
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
		WrongValuePropertyImpl other = (WrongValuePropertyImpl) obj;
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
		if (_wrongValueException == null) {
			if (other._wrongValueException != null)
				return false;
		} else if (!_wrongValueException.equals(other._wrongValueException))
			return false;
		return true;
	}

}
