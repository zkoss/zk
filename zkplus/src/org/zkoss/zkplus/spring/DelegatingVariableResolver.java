/* DelegatingVariableResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  1 13:53:53     2006, Created by andrewho
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.spring;

import java.util.Map;
import java.util.HashMap;
import javax.servlet.ServletContext;

import org.zkoss.zk.ui.Executions;
import org.zkoss.xel.VariableResolver;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>DelegatingVariableResolver, a spring bean variable resolver.</p>
 *
 * <p>It defines a variable called <code>springContext</code> to represent
 * the instance of <code>org.springframework.context.ApplicationContext</code>.
 * It also looks variables for beans defined in <code>springContext</code>.
 *
 * <p>Usage:<br>
 * <code>&lt;?variable-resolver class="org.zkoss.zkplus.spring.DelegatingVariableResolver"?&gt;</code>
 *
 * @author andrewho
 */
public class DelegatingVariableResolver implements VariableResolver {
	protected ApplicationContext _ctx;
//	protected final Map _vars = new HashMap();
	
	/**
	 * Get the spring application context.
	 */
	protected ApplicationContext getApplicationContext() {
		if (_ctx != null)
			return _ctx;
			
		_ctx = SpringUtil.getApplicationContext();
//		_vars.put("springContext", _ctx);
		return _ctx;
	}
	
	/**
	 * Get the spring bean by the specified name.
	 */		
	public Object resolveVariable(String name) {
		if ("springContext".equals(name)) {
			return getApplicationContext();
		}
//		Object o = _vars.get(name);
//		if (o == null) {
			try {
				return getApplicationContext().getBean(name);
			} catch (NoSuchBeanDefinitionException ex) {
				return null;
			}
//			if (o != null)
//				_vars.put(name, o);
//		}
//		return o;
	}
}
