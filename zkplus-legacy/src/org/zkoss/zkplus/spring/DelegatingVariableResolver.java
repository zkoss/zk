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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

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
 * <p>Developers can specify a list of class names separated with comma in
 * a library property called <code>org.zkoss.spring.VariableResolver.class</code>,
 * such they are used as the default variable resolvers.
 * <p>Applicable to Spring Framework version 2.x or later</p>
 * @author henrichen
 * @author ashish
 */
public class DelegatingVariableResolver implements VariableResolver, java.io.Serializable {
	private static final Logger log = LoggerFactory.getLogger(DelegatingVariableResolver.class);

	/**
	 * Holds list of variable resolvers for Spring core (3.0RC and later),
	 * Spring security(3.0RC and later) and Spring webflow(only for 1.x)
	 */
	protected transient List<VariableResolver> _variableResolvers = new ArrayList<VariableResolver>();

	public DelegatingVariableResolver() {
		final Execution exec = Executions.getCurrent();
		String classes = null;
		if (exec != null) {
			classes = exec.getDesktop().getWebApp().getConfiguration()
					.getPreference("org.zkoss.spring.VariableResolver", null);
		}

		if (classes == null)
			classes = Library.getProperty("org.zkoss.spring.VariableResolver.class");

		if (classes != null) {
			String[] vrClss = classes.split(",");
			for (int i = 0; i < vrClss.length; i++) {
				try {
					VariableResolver o = (VariableResolver) Classes.newInstanceByThread(vrClss[i]);
					if (!_variableResolvers.contains(o)) {
						_variableResolvers.add(o);
					}
				} catch (Throwable e) {
					log.warn("Ignored: failed to instantiate " + vrClss[i], e);
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
		for (VariableResolver resolver : _variableResolvers) {
			o = resolver.resolveVariable(name);
			if (o != null) {
				return o;
			}
		}
		return o;
	}

	public int hashCode() {
		return Objects.hashCode(_variableResolvers);
	}

	public boolean equals(Object obj) {
		return this == obj || (obj instanceof DelegatingVariableResolver
				&& Objects.equals(_variableResolvers, ((DelegatingVariableResolver) obj)._variableResolvers));
	}

	// -- Serializable --//
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		s.writeInt(_variableResolvers.size());
		for (Iterator it = _variableResolvers.iterator(); it.hasNext();) {
			Object o = it.next();
			if (o instanceof DefaultDelegatingVariableResolver) {
				s.writeObject("");
			} else
				s.writeObject(o);
		}
	}

	private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		_variableResolvers = new ArrayList<VariableResolver>();
		int size = s.readInt();
		for (int i = 0; i < size; i++) {
			Object o = s.readObject();
			if (o instanceof String) {
				_variableResolvers.add(new DefaultDelegatingVariableResolver());
			} else
				_variableResolvers.add((VariableResolver) o);
		}
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
			return Objects.hashCode(getClass());
		}

		public boolean equals(Object obj) {
			return this == obj || (obj instanceof DefaultDelegatingVariableResolver && getClass() == obj.getClass());
		}
	}
}
