/* Namespaces.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 16 00:01:09     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.scripting;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.util.logging.Log;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.scripting.util.SimpleNamespace;

/**
 * {@link Namespace} relevant utilities.
 *
 * @author tomyeh
 */
public class Namespaces {
	private static final Log log = Log.lookup(Namespaces.class);

	/** A stack of implict objects ({@link Implicts}. */
	private static final ThreadLocal _implicits = new ThreadLocal();
	/** A stack of current namespace. */
	private static final ThreadLocal _curnss = new ThreadLocal();

	/** Prepares implicit variable before calling {@link Page#interpret}.
	 *
	 * <p>Typical use:
	 * <pre><code>
final Namespace ns = Namespaces.beforeInterpret(comp);
try {
  Namespaces.setImplicit("some", value);
  page.interpret(zslang, zscript, ns); //it will push ns as the current namespace
} finally {
  Namespaces.afterInterpret();
}
</code></pre>
	 *
	 * <p>Another example:
	 * <pre><code>
Namespaces.beforeInterpret(comp);
try {
  constr.validate(comp); //if constr might be an instance of a class implemented in zscript
} finally {
  Namespaces.afterInterpret();
}
</code></pre>
	 *
	 * <p>If you need to set some implicit variables, you can invoke
	 * {@link #setImplicit} between {@link #beforeInterpret}
	 * and {@link #afterInterpret}.
	 *
	 * @param comp the component, never null.
	 * @return the namespace that owns the specified component
	 * @since 3.6.1
	 */
	public static final
	Namespace beforeInterpret(Component comp) {
		Namespace ns = comp.getNamespace();
		if (ns == null) ns = new SimpleNamespace();

		final Implicit impl = beforeInterpret0(ns);
		impl.setImplicit("self", comp);
		impl.setImplicit("componentScope", comp.getAttributes(Component.COMPONENT_SCOPE));

		return ns;
	}
	/** Prepares builtin variable before calling
	 * {@link org.zkoss.zk.ui.Page#interpret} or a method that might be
	 * implemented with zscript.
	 *
	 * @see #beforeInterpret(Component)
	 * @param page the page, never null.
	 * @return the namespace that owns the specified page
	 * @since 3.6.1
	 */
	public static final Namespace beforeInterpret(Page page) {
		final Namespace ns = page.getNamespace();
		final Implicit impl = beforeInterpret0(ns);
		impl.setImplicit("self", page);
		return ns;
	}
	private static Implicit beforeInterpret0(Namespace ns) {
		List impls = (List)_implicits.get();
		if (impls == null)
			_implicits.set(impls = new LinkedList());
		final Implicit impl = new Implicit();
		impls.add(0, impl);

		final Execution exec = Executions.getCurrent();
		impl.setImplicit("arg", exec != null ? exec.getArg(): null);

		push(ns);

		return impl;
	}
	/** Used with {@link #beforeInterpret} to clean up implicit
	 * variables.
	 *
	 * @since 3.6.1
	 */
	public static final void afterInterpret() {
		((List)_implicits.get()).remove(0);
		pop();
	}

	/** Sets an implicit object.
	 * It can be called only between {@link #beforeInterpret} and
	 * {@link #afterInterpret}.
	 *
	 * @since 3.6.1
	 */
	public static void setImplicit(String name, Object value) {
		((Implicit)((List)_implicits.get()).get(0))
			.setImplicit(name, value);
	}
	/** Returns the implict object.
	 *
	 * @param name the variable to retrieve
	 * @param defValue the default vale that is used if the implicit
	 * object is not defined.
	 * @since 3.6.1
	 */
	public static Object getImplicit(String name, Object defValue) {
		final List implicits = (List)_implicits.get();
		if (implicits != null && !implicits.isEmpty()) //in case: beforeInterpret not called
			return ((Implicit)implicits.get(0)).getImplicit(name, defValue);
		return defValue;
	}

	/** Returns the current namespace.
	 * The current namespace is the event target's namespace if this thread
	 * is processing an event ({@link org.zkoss.zk.ui.event.Event#getTarget}.
	 * Otherwise, the namespace of the page specified is assumed.
	 *
	 * <p>This method is used only to implement {@link org.zkoss.zk.scripting.Interpreter}.
	 * You rarely need to access it other than implementing an interpreter.
	 */
	public static final Namespace getCurrent(Page page) {
		final List nss = (List)_curnss.get();
		final Namespace ns =
			nss != null && !nss.isEmpty() ? (Namespace)nss.get(0): null;
		return ns != null ? ns: page.getNamespace();
	}
	/** Pushes the specified namespace as the current namespace.
	 *
	 * @param ns the namespace. If null, it means page's namespace.
	 */
	private static final void push(Namespace ns) {
		List nss = (List)_curnss.get();
		if (nss == null)
			_curnss.set(nss = new LinkedList());
		nss.add(0, ns);
	}
	/** Pops the current namespce (pushed by {@link #push}).
	 */
	private static final void pop() {
		((List)_curnss.get()).remove(0);
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
			final Object o = _vars.get(name);
			return o != null || _vars.containsKey(name) ? o: defValue;
		}
	}
}
