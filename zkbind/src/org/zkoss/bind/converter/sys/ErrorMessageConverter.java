/* ErrorMessageConverter.java

	Purpose:
		
	Description:
		
	History:
		2012/2/21 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter.sys;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.impl.InputElement;

/**
 * A converter to send error message directly to client.
 * @author dennis
 * @since 6.0.1
 */
public class ErrorMessageConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Object coerceToUi(Object val, Component component, BindContext ctx) {
		final InputElement input = (InputElement)component;
		final String msg = val==null?null:val.toString();
		if(Strings.isEmpty(msg)){
			Clients.clearWrongValue(input);
		}else{
			Clients.wrongValue(input, msg);
		}
		return LoadPropertyBinding.LOAD_IGNORED;
	}

	@Override
	public Object coerceToBean(Object val, Component component, BindContext ctx) {
		return val;
	}

}
