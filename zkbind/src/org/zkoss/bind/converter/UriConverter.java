/* UriConverter.java

	Purpose:
		
	Description:
		
	History:
		Jun 23, 2011 12:18:59 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

/**
 * Convert String into Uri
 * @author henrichen
 *
 */
public class UriConverter implements Converter {

	public Object coerceToUi(Object val, Component component, BindContext ctx) {
		final String uri = (String) val;
		final String prefix = (String) ctx.getConverterArg("prefix");
		final String postfix = (String) ctx.getConverterArg("postfix");
		final StringBuilder sb = new StringBuilder();
//		return prefix + uri + postfix;
		if(prefix!=null) 
			sb.append(prefix);
		sb.append(uri);
		if(postfix!=null) 
			sb.append(postfix);
		return sb.toString();
	}

	public Object coerceToBean(Object val, Component component, BindContext ctx) {
		// Never called
		return null;
	}

}
