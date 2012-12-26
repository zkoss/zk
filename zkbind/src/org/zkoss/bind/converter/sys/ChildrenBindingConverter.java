/* ChildrenBindingConverter.java

	Purpose:
		
	Description:
		
	History:
		2012/2/29 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter.sys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

/**
 * To converter items to List
 * @author dennis
 * @since 6.5.2
 */
public class ChildrenBindingConverter implements Converter, Serializable{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public Object coerceToUi(Object val, Component component, BindContext ctx) {
		if (val == null || val instanceof List) {
			return val;
		}
		Collection<Object> data;
		if (val instanceof Collection) {
			data = new ArrayList<Object>((Collection)val);
		} else if (val instanceof Object[]) {
			data = Arrays.asList((Object[])val);
		} else if ((val instanceof Class) && Enum.class.isAssignableFrom((Class)val)) {
			data = Arrays.asList(((Class)val).getEnumConstants());
		} else {
			data = new ArrayList<Object>();
			data.add(val);
		}
		return data;
	}

	@Override
	public Object coerceToBean(Object val, Component component, BindContext ctx) {
		//no save binding in children binding
		return val;
	}

}
