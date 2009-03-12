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

	/** Prepares builtin variable before calling {@link Page#interpret}.
	 *
	 * <p>Typical use:
	 * <pre><code>
final Map backup = new HashMap();
final Namespace ns = Namespaces.beforeInterpret(backup, comp, false);
try {
  Namespaces.setImplicit(backup, ns, "some", value);
  page.interpret(zslang, zscript, ns); //it will push ns as the current namespace
} finally {
  Namespaces.afterInterpret(backup, ns, false);
}
</code></pre>
	 *
	 * <p>Another example:
	 * <pre><code>
final Map backup = new HashMap();
final Namespace ns = Namespaces.beforeInterpret(backup, comp, true);
try {
  Namespaces.setImplicit(backup, ns, "some", value);
  constr.validate(comp); //if constr might be an instance of a class implemented in zscript
} finally {
  Namespaces.afterInterpret(backup, ns, true);
}
</code></pre>
	 *
	 * <p>If you need to backup some variables, you can invoke
	 * {@link #setImplicit} between {@link #beforeInterpret}
	 * and {@link #afterInterpret}.
	 *
	 * @param backup the map to hold the backup variables. Never null.
	 * @param comp the component, never null.
	 * @param pushNS whether to make the namespace being returned
	 * as the current namespace ({@link #getCurrent}).
	 * Note: its value must be the same as the popNS argument of
	 * {@link #afterInterpret}.
	 * @return the namespace that owns the specified component
	 */
	public static final Namespace beforeInterpret(Map backup, Component comp,
	boolean pushNS) {
		Namespace ns = comp.getNamespace();
		if (ns == null) ns = new SimpleNamespace();

		setImplicit(backup, ns, "self", comp);
		setImplicit(backup, ns, "componentScope", comp.getAttributes(Component.COMPONENT_SCOPE));

		final Execution exec = Executions.getCurrent();
		setImplicit(backup, ns, "arg", exec != null ? exec.getArg(): null);

		if (pushNS) push(ns);
		return ns;
	}
	/** Prepares builtin variable before calling
	 * {@link org.zkoss.zk.ui.Page#interpret} or a method that might be
	 * implemented with zscript.
	 *
	 * @see #beforeInterpret
	 * @param backup the map to hold the backup variables.
	 * If it is the first time to set the implicit, it cannot be null.
	 * If it is the second time, it must be null (so the previous backup won't
	 * be destroyed).
	 * @param page the page, never null.
	 * @param pushNS whether to make the namespace being returned
	 * as the current namespace ({@link #getCurrent}).
	 * Note: its value must be the same as the popNS argument of
	 * {@link #afterInterpret}.
	 * @return the namespace that owns the specified page
	 */
	public static final Namespace beforeInterpret(Map backup, Page page,
	boolean pushNS) {
		final Namespace ns = page.getNamespace();

		final Execution exec = Executions.getCurrent();
		setImplicit(backup, ns, "arg", exec != null ? exec.getArg(): null);

		if (pushNS) push(ns);
		return ns;
	}
	/** Used with {@link #beforeInterpret} to clean up builtin
	 * variables.
	 *
	 * @param backup the map to hold the backup variables. Never null.
	 * It must be the same as the backup argument of {@link #beforeInterpret}.
	 * @param ns the namespace returned by {@link #beforeInterpret}
	 * @param popNS whether to pop out the current namespace.
	 * Its value must be the same as the pushNS argument of {@link #beforeInterpret}.
	 */
	public static final void afterInterpret(Map backup, Namespace ns,
	boolean popNS) {
		if (backup != null && !backup.isEmpty()) {
			Namespace nsbk = ns;
			for (Namespace np; (np = nsbk.getParent()) != null; nsbk = np)
				;

			for (Iterator it = backup.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String name = (String)me.getKey();
				final Object val = me.getValue();
				//if (D.ON && log.finerable()) log.finer("Restore "+name+"="+val);
				if (val != VOID) nsbk.setVariable(name, val, true);
				else nsbk.unsetVariable(name, true);
			}
		}
		if (popNS) pop(ns);
	}

	/** Sets an implicit object that will be stored later when {@link #afterInterpret}
	 * is called.
	 * @since 3.6.1
	 */
	public static void setImplicit(Map backup, Namespace ns, String name, Object val) {
		//Bug 2684510: use top-level namespace to backup (since it is implict)
		for (Namespace np; (np = ns.getParent()) != null; ns = np)
			;

		if (backup != null) {
			final Object oldval = ns.getVariable(name, true);
			backup.put(name,
				oldval != null || ns.getVariableNames().contains(name) ? oldval: VOID);
		}
		ns.setVariable(name, val, true);
	}
	private static final Object VOID = new Object();

	/** @deprecated As of release 3.6.1, it is replaced with {@link #setImplicit}.
	 */
	public static final void backupVariable(Map backup, Namespace ns, String name) {
		//due to incompatible restore, we cannot store backup
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
	private static final void pop(Namespace ns) {
		final List nss = (List)_curnss.get();
		if (nss.remove(0) != ns)
			log.realCauseBriefly(new IllegalStateException("Unmatched pop the current namespace"));
	}

	/** A stack of current namespace. */
	private static final ThreadLocal _curnss = new ThreadLocal();
}
