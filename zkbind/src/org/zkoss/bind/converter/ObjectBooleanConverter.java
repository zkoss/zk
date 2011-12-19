/* BooleanConverter.java

	Purpose:
		
	Description:
		
	History:
		Jun 22, 2011 11:43:00 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter;

import java.io.Serializable;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.Binding;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;

/**
 * Converter to convert boolean to Object.
 * @author henrichen
 *
 */
public class ObjectBooleanConverter implements Converter,Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	/**
	 * Given an object value and return whether it is a "true" object.
	 * @param val the object to be checked if a true object
	 * @param comp associated Component
	 * @param ctx bind context for associate {@link Binding} and extra parameter (e.g. true and false)
	 * @return the converted Boolean object
	 */
	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
		final Object trueObj = ctx.getConverterArg("true");
		return Boolean.valueOf(Objects.equals(val, trueObj));
	}
	
	/**
	 * Given a Boolean value and return associated "true" object if true; or "false" object if false.
	 * @param val the boolean value to be checked.
	 * @param comp associate Component
	 * @param ctx bind context for associate {@link Binding} and extra parameter (e.g. true and false)
	 * @return the converted "true" object if true; or "false" object if false.
	 */
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		final Object trueObj = ctx.getConverterArg("true");
		final Object falseObj = ctx.getConverterArg("false");
		final Boolean b = (Boolean) val;
		return b.booleanValue() ? trueObj : falseObj;
	}
}
