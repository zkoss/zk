/* Scopes.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 10 14:19:27     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.ext;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collection;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.impl.SimpleScope;

/**
 * Utilities to manage the current scope ({@link Scope}).
 *
 * @author tomyeh
 * @since 5.0.0
 */
public class Scopes {
	private static final Log log = Log.lookup(Scopes.class);

	/** A stack of implict objects ({@link Implicts}. */
	private static final ThreadLocal _implicits = new ThreadLocal();
	/** A stack of current scope. */
	private static final ThreadLocal _scopes = new ThreadLocal();

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
			impl.setImplicit("componentScope", new DeferredScope(scope));
				//Use DeferredScope so scope.getAttributes is called only if
				//required.
				//Reason: save footprint of AbstractComponent which stores
				//attrs in _auxinf (and created only if necessary)
		return scope;
	}
	private static Implicit beforeInterpret0(Scope scope) {
		List impls = (List)_implicits.get();
		if (impls == null)
			_implicits.set(impls = new LinkedList());
		final Implicit impl = new Implicit();
		impls.add(0, impl);

		final Execution exec = Executions.getCurrent();
		impl.setImplicit("arg", exec != null ? exec.getArg(): null);

		push(scope);

		return impl;
	}
	/** Used with {@link #beforeInterpret} to clean up implicit
	 * variables.
	 */
	public static final void afterInterpret() {
		((List)_implicits.get()).remove(0);
		pop();
	}

	/** Sets an implicit object.
	 * It can be called only between {@link #beforeInterpret} and
	 * {@link #afterInterpret}.
	 */
	public static void setImplicit(String name, Object value) {
		((Implicit)((List)_implicits.get()).get(0))
			.setImplicit(name, value);
	}
	/** Returns the implict object.
	 *
	 * <p>It searches the implicit object stored with {@link #setImplicit},
	 * and also searches the system implicity objects by use of
	 * {@link Components#getImplicit(Page, Component, String)}.
	 *
	 * @param name the variable to retrieve
	 * @param defValue the default vale that is used if the implicit
	 * object is not defined.
	 */
	public static Object getImplicit(String name, Object defValue) {
		final List implicits = (List)_implicits.get();
		if (implicits != null && !implicits.isEmpty()) //in case: beforeInterpret not called
			return ((Implicit)implicits.get(0)).getImplicit(name, defValue);

		Object val = getSysImplicit(name);
		return val != null ? val: defValue;
	}
	private static Object getSysImplicit(String name) {
		final Object val = getCurrent(null);
		return  val instanceof Component ? Components.getImplicit((Component)val, name):
			val instanceof Page ? Components.getImplicit((Page)val, name): null;
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
		final List nss = (List)_scopes.get();
		final Scope scope =
			nss != null && !nss.isEmpty() ? (Scope)nss.get(0): null;
		if (scope != null)
			return scope;

		if (page == null) {
			final Execution exec = Executions.getCurrent();
			if (exec != null)
				page = ((ExecutionCtrl)exec).getCurrentPage();
		}
		return page;
	}
	/** Pushes the specified scope as the current scope.
	 *
	 * @param scope the scope. If null, it means page's scope.
	 */
	private static final void push(Scope scope) {
		List nss = (List)_scopes.get();
		if (nss == null)
			_scopes.set(nss = new LinkedList());
		nss.add(0, scope);
	}
	/** Pops the current namespce (pushed by {@link #push}).
	 */
	private static final void pop() {
		((List)_scopes.get()).remove(0);
	}

	private static class Implicit {
		/** Implicit variables. */
		private final Map _vars = new HashMap();

		private Implicit() {
		}
		private void setImplicit(String name, Object value) {
			_vars.put(name, value);
		}
		private Object getImplicit(String name, Object defValue) {
			Object val = _vars.get(name);
			if (val != null || _vars.containsKey(name)) {
				if (val instanceof Deferred)
				 	_vars.put(name, val = ((Deferred)val).getValue());
				return val;
			}
			val = getSysImplicit(name);
			return val != null ? val: defValue;
		}
	}
	/** Store a deferred value. */
	private static interface Deferred {
		/** Returns the real value. */
		public Object getValue();
	}
	private static class DeferredScope {
		private final Scope _scope;
		private DeferredScope(Scope scope) {
			_scope = scope;
		}
		public Object getValue() {
			return _scope.getAttributes();
		}

		public String toString() {
			return _scope.toString();
		}
		public boolean equals(Object o) {
			return _scope.equals(
				o instanceof DeferredScope ? ((DeferredScope)o)._scope: o);
		}
		public int hashCode() {
			return _scope.hashCode();
		}
	}
}
