/* Scopes.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 10 14:19:27     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.ext;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Library;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.impl.SimpleScope;
import org.zkoss.zk.ui.sys.ExecutionCtrl;

/**
 * Utilities to manage the current scope ({@link Scope}).
 *
 * <p> Since 10.0.0, there two library properties to control the scope lock cache:
 * <ol>
 *     <li>org.zkoss.zk.ui.ext.Scopes.maxLockSize: the maximum number of locks to be cached. (Default: 10,000)</li>
 *     <li>org.zkoss.zk.ui.ext.Scopes.maxTimeout: the maximum time a lock can be cached in minutes after accessing it. (Default: 60 minutes)</li>
 * </ol>
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class Scopes {
	private static final Logger log = LoggerFactory.getLogger(Scopes.class);

	/** A stack of implicit objects ({@link Implicit}). */
	private static final ThreadLocal<List<Implicit>> _implicits = new ThreadLocal<>();
	/** A stack of current scope. */
	private static final ThreadLocal<List<Scope>> _scopes = new ThreadLocal<>();

	private static final Cache<Scope, Object> SCOPE_LOCKS = CacheBuilder.newBuilder()
			.maximumSize(Library.getIntProperty(
					"org.zkoss.zk.ui.ext.Scopes.maxLockSize", 10_000))
			.expireAfterAccess(Library.getIntProperty(
					"org.zkoss.zk.ui.ext.Scopes.maxTimeout", 60), TimeUnit.MINUTES)
			.build();

	private static final Object FALLBACK_LOCK = new Object();

	private Scopes() {
		// no instance
	}

	/**
	 * Returns a consistent lock for a given scope, if available.
	 *
	 * <p> The method returns a lock object specific to the provided scope,
	 * allowing for fine-grained synchronization control. If no lock is associated
	 * with the scope, the method returns a fallback lock instead.
	 * <p> Usage of this method is preferred over manual synchronization or lock management,
	 * as it provides a standardized approach to handling concurrency in a scope-based context.
	 *
	 * @param scope The scope for which the lock is to be acquired.
	 * @return The lock object associated with the given scope, if any; otherwise,
	 * a static fallback lock.
	 * @since 10.0.0
	 */
	public static Object getLockForScopeIfAny(Scope scope) {
		try {
			// Attempt to get the lock from the cache
			return SCOPE_LOCKS.get(scope, Object::new);
		} catch (ExecutionException e) {
			// Handle the exception, e.g., log it
			log.warn("Error while retrieving lock for scope. Using fallback lock.", e);

			// Fallback: return a predefined static lock object or create a new one
			// Using a static fallback lock can have implications on concurrency,
			// as different scopes might end up using the same lock.
			// Alternatively, you could create a new Object here as a fallback lock
			// but that won't guarantee the same lock for the same scope across different calls.
			return FALLBACK_LOCK;
		}
	}

	/** Prepares implicit variable before calling {@link Page#interpret}.
	 *
	 * <p>Typical use:
	 * <pre><code>
	final Scope scope = Scopes.beforeInterpret(comp);
	try {
	Scopes.setImplicit("some", value);
	page.interpret(zslang, zscript, scope); //it will push scope as the current scope
	} finally {
	Scopes.afterInterpret();
	}
	</code></pre>
	 *
	 * <p>Another example:
	 * <pre><code>
	Scopes.beforeInterpret(comp);
	try {
	constr.validate(comp); //if constr might be an instance of a class implemented in zscript
	} finally {
	Scopess.afterInterpret();
	}
	</code></pre>
	 *
	 * <p>If you need to set some implicit variables, you can invoke
	 * {@link #setImplicit} between {@link #beforeInterpret}
	 * and {@link #afterInterpret}.
	 *
	 * @param scope the scope, never null.
	 * @return the scope used for interpretation. It is the same as the scope
	 * parameter if it is not null. Otherwise, a temporary scope is created.
	 */
	public static final Scope beforeInterpret(Scope scope) {
		if (scope == null)
			scope = new SimpleScope(null);

		final Implicit impl = beforeInterpret0(scope);
		impl.setImplicit("self", scope);

		if (scope instanceof Component)
			impl.setImplicit("componentScope", new DeferredAttributes(scope));
		//Use DeferredAttributes so scope.getAttributes is called only if
		//required.
		//Reason: save footprint of AbstractComponent which stores
		//attrs in _auxinf (and created only if necessary)
		return scope;
	}

	private static Implicit beforeInterpret0(Scope scope) {
		List<Implicit> impls = _implicits.get();
		if (impls == null)
			_implicits.set(impls = new LinkedList<Implicit>());
		final Implicit impl = new Implicit();
		impls.add(0, impl);

		final Execution exec = Executions.getCurrent();
		impl.setImplicit("arg", exec != null ? exec.getArg() : null);

		push(scope);

		return impl;
	}

	/** Used with {@link #beforeInterpret} to clean up implicit
	 * variables.
	 */
	public static final void afterInterpret() {
		_implicits.get().remove(0);
		pop();
	}

	/** Sets an implicit object.
	 * It can be called only between {@link #beforeInterpret} and
	 * {@link #afterInterpret}.
	 */
	public static void setImplicit(String name, Object value) {
		_implicits.get().get(0).setImplicit(name, value);
	}

	/** Returns the implicit object.
	 *
	 * <p>It searches the implicit object stored with {@link #setImplicit},
	 * and also searches the system implicit objects by use of
	 * {@link Components#getImplicit(Page, Component, String)}.
	 *
	 * @param name the variable to retrieve
	 * @param defValue the default vale that is used if the implicit
	 * object is not defined.
	 */
	public static Object getImplicit(String name, Object defValue) {
		final List<Implicit> implicits = _implicits.get();
		if (implicits != null && !implicits.isEmpty()) //in case: beforeInterpret not called
			return implicits.get(0).getImplicit(name, defValue);

		Object val = getSysImplicit(name);
		return val != null ? val : defValue;
	}

	private static Object getSysImplicit(String name) {
		final Object val = getCurrent(null);
		return val instanceof Component ? Components.getImplicit((Component) val, name)
				: val instanceof Page ? Components.getImplicit((Page) val, name) : null;
	}

	/** Returns the current scope.
	 * The current scope is the event target's scope if this thread
	 * is processing an event ({@link org.zkoss.zk.ui.event.Event#getTarget}.
	 * Otherwise, the scope of the page specified is assumed.
	 *
	 * <p>This method is used only to implement {@link org.zkoss.zk.scripting.Interpreter}.
	 * You rarely need to access it other than implementing an interpreter.
	 *
	 * @param page the page. It is used if {@link #beforeInterpret}
	 * is not called before. If null, the current page ({@link ExecutionCtrl#getCurrentPage}
	 * is assumed.
	 */
	public static final Scope getCurrent(Page page) {
		final List<Scope> nss = _scopes.get();
		final Scope scope = nss != null && !nss.isEmpty() ? nss.get(0) : null;
		if (scope != null)
			return scope;

		if (page == null) {
			final Execution exec = Executions.getCurrent();
			if (exec != null)
				page = ((ExecutionCtrl) exec).getCurrentPage();
		}
		return page;
	}

	/** Pushes the specified scope as the current scope.
	 *
	 * @param scope the scope. If null, it means page's scope.
	 */
	private static final void push(Scope scope) {
		List<Scope> nss = _scopes.get();
		if (nss == null)
			_scopes.set(nss = new LinkedList<Scope>());
		nss.add(0, scope);
	}

	/** Pops the current namespace (pushed by {@link #push}).
	 */
	private static final void pop() {
		_scopes.get().remove(0);
	}

	private static class Implicit {
		/** Implicit variables. */
		private final Map<String, Object> _vars = new HashMap<String, Object>();

		private Implicit() {
		}

		private void setImplicit(String name, Object value) {
			_vars.put(name, value);
		}

		private Object getImplicit(String name, Object defValue) {
			Object val = _vars.get(name);
			if (val != null || _vars.containsKey(name)) {
				if (val instanceof Deferred)
					_vars.put(name, val = ((Deferred) val).getValue());
				return val;
			}
			val = getSysImplicit(name);
			return val != null ? val : defValue;
		}
	}

	/** Store a deferred value. */
	private static interface Deferred {
		/** Returns the real value. */
		public Object getValue();
	}

	private static class DeferredAttributes implements Map<String, Object> {
		private final Scope _scope;

		private DeferredAttributes(Scope scope) {
			_scope = scope;
		}

		public void clear() {
			_scope.getAttributes().clear();
		}

		public boolean containsKey(Object arg0) {
			return _scope.getAttributes().containsKey(arg0);
		}

		public boolean containsValue(Object arg0) {
			return _scope.getAttributes().containsValue(arg0);
		}

		public Set<Map.Entry<String, Object>> entrySet() {
			return _scope.getAttributes().entrySet();
		}

		public Object get(Object arg0) {
			return _scope.getAttributes().get(arg0);
		}

		public boolean isEmpty() {
			return _scope.getAttributes().isEmpty();
		}

		public Set<String> keySet() {
			return _scope.getAttributes().keySet();
		}

		public Object put(String arg0, Object arg1) {
			return _scope.getAttributes().put(arg0, arg1);
		}

		public void putAll(Map<? extends String, ? extends Object> arg0) {
			_scope.getAttributes().putAll(arg0);
		}

		public Object remove(Object arg0) {
			return _scope.getAttributes().remove(arg0);
		}

		public int size() {
			return _scope.getAttributes().size();
		}

		public Collection<Object> values() {
			return _scope.getAttributes().values();
		}

		public int hashCode() {
			return _scope.getAttributes().hashCode();
		}

		public boolean equals(Object o) {
			if (this == o)
				return true;
			return _scope.getAttributes()
					.equals(o instanceof DeferredAttributes ? ((DeferredAttributes) o)._scope.getAttributes() : o);
		}

		public String toString() {
			return _scope.getAttributes().toString();
		}
	}
}
