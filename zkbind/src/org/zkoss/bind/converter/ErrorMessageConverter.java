/* ErrorMessageConverter.java

	Purpose:
		
	Description:
		
	History:
		2012/2/21 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

/**
 * A special converter that help you show a error message on any component.<br/>
 * For example <br/>
 * <pre>
 * {@code
 * <div msg="@bind(vmsgs[tb]) @converter('org.zkoss.bind.converter.ErrorMessageConverter')">
 *     <textbox id="tb" value="@bind(vm.value1) @validator(vm.validator1)"  />
 * </div>
 * }
 * </pre>
 * At here, <i>msg</i> is a non-existed attribute of the component, you can use another non-existed name
 * @author dennis
 * @since 6.0.1
 */
public class ErrorMessageConverter implements Converter, java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Object coerceToUi(Object val, Component component, BindContext ctx) {
		final String msg = val==null?null:val.toString();
		if(Strings.isEmpty(msg)){
			Clients.clearWrongValue(component);
		}else{
			Clients.wrongValue(component, msg);
		}
		return IGNORED_VALUE;
	}

	@Override
	public Object coerceToBean(Object val, Component component, BindContext ctx) {
		return val;
	}

}
