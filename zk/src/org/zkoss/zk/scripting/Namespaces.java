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
	 * final Namespace ns = Namespaces.beforeInterpret(exec, comp);
	 * try {
	 	Namespaces.backupVariables(ns, "some");
	 *   page.interpret(zscript, ns);
	 * } finally {
	 *   Namespaces.afterInterpret(ns);
	 * }
	 * </code></pre>
	 *
	 * <p>If you need to backup some variables, you can invoke
	 * {@link #backupVariable} between {@link #beforeInterpret}
	 * and {@link #afterInterpret}.
	 *
	 * @param exec the current execution.
	 * If null, {@link Desktop#getExecution} is used.
	 * @param comp the component, never null.
	 * @return the namespace that owns the specified component
	 */
	public static final
	Namespace beforeInterpret(Execution exec, Component comp) {
		final Namespace ns = comp.getNamespace();
		backupVariable0(ns, "self", true);
		backupVariable0(ns, "componentScope", false);
		ns.setVariable("self", comp, true);
		ns.setVariable("componentScope",
			comp.getAttributes(Component.COMPONENT_SCOPE), true);

		final Object arg = getArg(exec, comp);
		if (arg != null) {
			backupVariable0(ns, "arg", false);
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
		backupVariable0(ns, null, true);

		final Object arg = getArg(exec, page);
		if (arg != null) {
			backupVariable0(ns, "arg", false);
			ns.setVariable("arg", arg, true);
		}
		return ns;
	}
	/** Used with {@link #beforeInterpret(Execution,Component)} to clean up builtin
	 * variables.
	 */
	public static final void afterInterpret(Namespace ns) {
		//Restores the variables that are backup-ed by {@link #backupVariable0}
	 	//in the same block.
		List backups = (List)ns.getVariable(VAR_BACKUPS, true);
		if (backups == null || backups.isEmpty()) {
			log.warning("restore without any block of backup-ed variables, "+ns);
		} else {
			final Map map = (Map)backups.remove(0);
			for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String name = (String)me.getKey();
				final Object val = me.getValue();
				//if (D.ON && log.finerable()) log.finer("Restore "+name+"="+val);
				if (val != null) ns.setVariable(name, val, true);
				else ns.unsetVariable(name);
			}
		}
	}

	private static final String VAR_BACKUPS = "__zk__backups__";
	/** Backup the specfied variable, such that it can be restored with
	 * {@link #afterInterpret}.
	 *
	 * <p>Note: you have to invoke {@link #beforeInterpret} before calling
	 * this method. Then, backup-ed variables will be restored together
	 * when {@link #afterInterpret} is called.
	 *
	 * @param name the variable to backup.
	 */
	public static final void backupVariable(Namespace ns, String name) {
		backupVariable0(ns, name, false);
	}
	/** Backup the specified variables
	 *
	 * @param name the variable to backup. If null and newBlock is true,
	 * it simply creates a new block. If null and newBlock is false, nothing
	 * happens.
	 * @param newBlock whether to create a new block. If true, a new block
	 * is created and the specified variable and following {@link #backupVariable0}
	 * are gathered in this new block.
	 * If false, the variable's value is added to the same block of the previous
	 * invocation of {@link #backupVariable}.
	 */
	private static final
	void backupVariable0(Namespace ns, String name, boolean newBlock) {
		List backups = (List)ns.getVariable(VAR_BACKUPS, true);
		if (newBlock || backups == null || backups.isEmpty()) {
			if (backups == null) 
				ns.setVariable(VAR_BACKUPS, backups = new LinkedList(), true);
			backups.add(0, new HashMap());
		}
		if (name != null) {
			final Map map = (Map)backups.get(0);
			map.put(name, ns.getVariable(name, true));
		}
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
