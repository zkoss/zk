/* DelegatingVariableResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  1 13:53:53     2006, Created by andrewho@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zkplus.spring;

import java.util.Map;
import java.util.HashMap;
import javax.servlet.ServletContext;

import com.potix.zk.ui.Executions;
import com.potix.zk.ui.util.VariableResolver;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * DelegatingVariableResolver, a spring bean variable resolver
 * @author <a href="mailto:andrewho@potix.com">andrewho@potix.com</a>
 */
public class DelegatingVariableResolver implements VariableResolver {
	protected ApplicationContext _ctx;
	protected final Map _vars = new HashMap();
	
	/**
	 * Get the spring application context.
	 */
	protected ApplicationContext getApplicationContext() {
		if (_ctx != null)
			return _ctx;
			
		_ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(
				(ServletContext)Executions.getCurrent().getDesktop().getWebApp().getNativeContext());
		_vars.put("springContext", _ctx);
		return _ctx;
	}
	
	/**
	 * Get the spring bean by id name.
	 */		
	public Object getVariable(String name) {
		Object o = _vars.get(name);
		if (o == null) {
			o = getApplicationContext().getBean(name);
			if (o != null)
				_vars.put(name, o);
		}
		return o;
	}
}
