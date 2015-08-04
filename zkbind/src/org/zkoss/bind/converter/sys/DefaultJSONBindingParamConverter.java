/** DefaultJSONBindingParamConverter.java.

	Purpose:
		
	Description:
		
	History:
		9:52:47 AM Mar 12, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter.sys;

import java.io.Serializable;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.impl.ParamCall;
import org.zkoss.json.JSONValue;
import org.zkoss.lang.Classes;
import org.zkoss.zk.ui.Component;

/**
 * A default JSON binding parameter converter 
 * @author jumperchen
 * @since 8.0.0
 */
public class DefaultJSONBindingParamConverter implements Converter,
		Serializable {

	public Object coerceToUi(Object val, Component component, BindContext ctx) {
		return JSONValue.toJSONString(val);
	}

	public Object coerceToBean(Object val, Component component, BindContext ctx) {
		return Classes.coerce((Class)ctx.getAttribute(ParamCall.BINDING_PARAM_CALL_TYPE), val);	
	}

}
