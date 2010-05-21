/* SpringUtil.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  1 13:53:53     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.spring;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;

/**
 * SpringUtil, a Spring utility.
 *
 * @author henrichen
 */
public class SpringUtil {
	/**
	 * Get the spring application context.
	 */
	public static ApplicationContext getApplicationContext() {
		Execution exec = Executions.getCurrent();
		if (exec == null) {
			throw new UiException("SpringUtil can be called only under ZK environment!");
		}
		
		return WebApplicationContextUtils.getRequiredWebApplicationContext(
				(ServletContext)exec.getDesktop().getWebApp().getNativeContext());
	}
	
	/**
	 * Get the spring bean by the specified name.
	 */		
	public static Object getBean(String name) {
		Object o = null;
		try {
			if(getApplicationContext().containsBean(name)) {
				o = getApplicationContext().getBean(name);
			}
		} catch (NoSuchBeanDefinitionException ex) {
			// ignore
		}
		return o;
	}

	/**
	 * Get the spring bean by the specified name and class.
	 */		
	public static Object getBean(String name, Class cls) {
		Object o = null;
		try {
			if(getApplicationContext().containsBean(name)) {
				o = getApplicationContext().getBean(name, cls);
			}
		} catch (NoSuchBeanDefinitionException ex) {
			// ignore
		} catch (BeanNotOfRequiredTypeException e) {
			// ignore
		}
		return o;
	}
}
