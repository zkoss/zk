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
package org.zkoss.zk.ui.util;

import java.util.Map;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

/**
 * {@link Namespace} relevant utilities.
 *
 * @author tomyeh
 */
public class Namespaces {
	/** Prepares builtin variable before calling {@link Page#interpret}.
	 *
	 * <p>Typical use:
	 * <pre><code>
	 * final Namespace ns = Namespaces.beforeInterpret(exec, comp);
	 * try {
	 *   page.interpret(zscript, ns);
	 * } finally {
	 *   Namespaces.afterInterpret(ns);
	 * }
	 * </code></pre>
	 *
	 * <p>Note: beforeInterpret() actually use {@link Namespace#backupVariable}
	 * to maintain what was modified, and then {@link #afterInterpret}
	 * restore them by use of {@link Namespace#restoreVariables}.
	 * Thus, you can back up more variables after calling this method
	 * with {@link Namespace#backupVariable} (by specifying newBlock as false).
	 *
	 * @param exec the current execution.
	 * If null, {@link Desktop#getExecution} is used.
	 * @param comp the component, never null.
	 * @return the namespace that owns the specified component
	 */
	public static final
	Namespace beforeInterpret(Execution exec, Component comp) {
		final Namespace ns = comp.getNamespace();
		ns.backupVariable("self", true);
		ns.backupVariable("componentScope", false);
		ns.setVariable("self", comp, true);
		ns.setVariable("componentScope",
			comp.getAttributes(Component.COMPONENT_SCOPE), true);

		final Object arg = getArg(exec, comp);
		if (arg != null) {
			ns.backupVariable("arg", false);
			ns.setVariable("arg", arg, true);
		}
		return ns;
	}
	/** Prepares builtin variable before calling
	 * {@link org.zkoss.zk.ui.Page#interpret}.
	 *
	 * @see #beforeInterpret(Execution,Component)
	 * @param exec the current execution.
	 * If null, {@link Desktop#getExecution} is used.
	 * @param page the page, never null.
	 * @return the namespace that owns the specified page
	 */
	public static final Namespace beforeInterpret(Execution exec, Page page) {
		final Namespace ns = page.getNamespace();
		ns.backupVariable(null, true);

		final Object arg = getArg(exec, page);
		if (arg != null) {
			ns.backupVariable("arg", false);
			ns.setVariable("arg", arg, true);
		}
		return ns;
	}
	/** Used with {@link #beforeInterpret(Execution,Component)} to clean up builtin
	 * variables.
	 */
	public static final void afterInterpret(Namespace ns) {
		ns.restoreVariables();
	}

	private static Map getArg(Execution exec, Component comp) {
		if (exec == null) {
			exec = Executions.getCurrent();
			if (exec == null && comp != null) {
				final Desktop dt = comp.getDesktop();
				if (dt != null) exec = dt.getExecution();
			}
			if (exec == null) return null;
		}
		return exec.getArg();
	}
	private static Map getArg(Execution exec, Page page) {
		if (exec == null) {
			exec = Executions.getCurrent();
			if (exec == null && page != null) {
				final Desktop dt = page.getDesktop();
				if (dt != null) exec = dt.getExecution();
			}
			if (exec == null) return null;
		}
		return exec.getArg();
	}
}
