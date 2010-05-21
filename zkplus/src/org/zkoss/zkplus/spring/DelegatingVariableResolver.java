/* DelegatingVariableResolver.java

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.zkoss.lang.Classes;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Configuration;

/**
 * <p>
 * DelegatingVariableResolver for resolving Spring beans,
 * Spring Security variables and Spring Webflow variables.
 * </p>
 * <p>
 * It delegates variable resolving to ZK Spring core, ZK Spring Security
 * and ZK Spring FlowResolver if they are on application classpath.
 * <p>
 * Usage:<br>
 * <code>&lt;?variable-resolver class="org.zkoss.zkplus.spring.DelegatingVariableResolver"?&gt;</code>
 * 
 * @author henrichen
 * @author ashish
 */
public class DelegatingVariableResolver implements VariableResolver {

	/**
	 * Holds list of variable resolvers for Spring core (3.0RC and later),
	 * Spring security(3.0RC and later) and Spring webflow(only for 1.x)
	 */
	protected List _variableResolvers = new ArrayList();

	public DelegatingVariableResolver() {
		final Configuration conf = Executions.getCurrent().getDesktop()
				.getWebApp().getConfiguration();
		final String value = conf.getPreference("org.zkoss.spring.VariableResolver",
				null);
		if (value != null) {
			String[] vrClss = value.split(",");
			for (int i = 0; i < vrClss.length; i++) {
				try {
					Object o = Classes.newInstanceByThread(vrClss[i]);
					if(!_variableResolvers.contains(o)) {
						_variableResolvers.add(o);
					}
				} catch (Exception e) {
					// do nothing
				}
			}
		} else {
			_variableResolvers.add(new DefaultDelegatingVariableResolver());
		}

	}

	/**
	 * Resolves variable name by name. It can resolve a spring bean, spring
	 * security authentication and spring web flow variables depending upon ZK
	 * Spring libraries in the classpath
	 */
	public Object resolveVariable(String name) {
		Object o = null;
		for (Iterator iterator = _variableResolvers.iterator(); iterator
				.hasNext();) {
			VariableResolver resolver = (VariableResolver) iterator.next();
			o = resolver.resolveVariable(name);
			if (o != null) {
				return o;
			}
		}
		return o;
	}

	public int hashCode() {
		return getClass().hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DelegatingVariableResolver))
			return false;
		DelegatingVariableResolver other = (DelegatingVariableResolver) obj; 
		return _variableResolvers.equals(other._variableResolvers);
	}
	
	/**
	 * Provides a default variable resolver implementation that resolves 
	 * spring beans by name. It also declares an implicit variable springContext
	 * that resolves to Spring webapp context
	 * @author ashish
	 *
	 */
	private static class DefaultDelegatingVariableResolver implements VariableResolver {

		private ApplicationContext _ctx;

		private ApplicationContext getApplicationContext() {
			if (_ctx != null)
				return _ctx;
				
			_ctx = SpringUtil.getApplicationContext();
			return _ctx;
		}

		public Object resolveVariable(String name) throws XelException {
			
			if ("springContext".equals(name)) {
				return getApplicationContext();
			}

			return SpringUtil.getBean(name);
		}
		public int hashCode() {
			return getClass().hashCode();
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			return getClass() == obj.getClass();
		}
	}
}
