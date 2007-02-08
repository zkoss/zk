/* NamespacelessInterpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Feb  7 13:50:54     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.util;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.zkoss.lang.D;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.Method;

/**
 * A skeletal class for implementing a interpreter ({@link Interpreter}) that
 * doesn't support {@link Namespace}.
 *
 * <p>Deriving class usually overrides {@link #exec} instead of {@link #interpret}.
 *
 * @author tomyeh
 */
abstract public class NamespacelessInterpreter implements Interpreter {
	private static final Log log = Log.lookup(NamespacelessInterpreter.class);

	/** A list of {@link ExecInfo}. */
	private final List _execInfos = new LinkedList();

	/** Executes the specified script.
	 * Deriving class shall provide an implementation of this method, rather
	 * than overriding {@link #interpret}.
	 *
	 * <p>It is invoked by {@link #interpret} after 'copying' variables from
	 * the namespace to the interpreter.
	 */
	abstract protected void exec(String script);

	/** Returns a variable from this interpreter directly.
	 */
	abstract protected Object getVariable(String name);
	/** Sets a variable to this interpreter directly.
	 */
	abstract protected void setVariable(String name, Object value);
	/** Unsets a variable from this interpreter directly.
	 */
//	abstract protected void unsetVariable(String name);

	/** Makes ns as the active namespace and copies all its variable
	 * to this interpreter.
	 */
	private void push(Namespace ns) {
		//check whether it is the special case that all execinfo are popped
		//but we keep one for improving performance
		final ExecInfo oei = getActive();
		if (oei != null && oei.count < 0) {
			assert D.OFF || _execInfos.size() == 1: "Wrong: "+_execInfos;
			if (oei.ns == ns) { //yes, the same case
				oei.count = 0;
				return; //done; reuse oei
			} else {
				oei.restore();
				_execInfos.remove(0);
			}
		} else if (oei != null) {
			if (oei.ns == ns) {
				oei.count++; //reuse it
				return; //done
			}
			oei.restore();
		}

		final ExecInfo ei = new ExecInfo(ns);
		_execInfos.add(0, ei);
		ei.backup();
	}
	/** Undo {@link #push}.
	 */
	private void pop() {
		final ExecInfo ei = (ExecInfo)_execInfos.get(0);
		if (ei.count-- == 0) {
			//Note: to speed up performance, we don't restore if only one
			//ns is left (so we can reuse it if next push is with the same ns
			if (_execInfos.size() > 1) {
				_execInfos.remove(0);
				ei.restore();
			}
		}
	}
	/** Returns the active namespace.
	 */
	private ExecInfo getActive() {
		return _execInfos.isEmpty() ? null: (ExecInfo)_execInfos.get(0);
	}

	//Interpreter//
	/** Interprets the script against the specified namespace.
	 * It maintains a stack to copy variables from the name space to
	 * this interpreter -- it assumes this interpreter doesn't support
	 * the concept of namespace.
	 *
	 * <p>Deriving class usually override {@link #exec} instead of
	 * this method.
	 */
	public void interpret(String script, Namespace ns) {
		push(ns);
		try {
			exec(script);
		} finally {
			pop();
		}
	}
	public Object getVariable(String name, boolean skipNamespace) {
		ExecInfo ei = null;
		if (skipNamespace) {
			ei = getActive();
			if (ei != null) {
				ei.restore();
				if (ei.count < 0) {
					ei = null; //no need to backup
					_execInfos.remove(0);
				}
			}
		}
		try {
			return getVariable(name);
		} finally {
			if (ei != null)
				ei.backup();
		}
	}
	/** Returns null since retrieving class is not supported.
	 */
	public Class getClass(String clsnm) {
		return null;
	}
	/** Returns null since retrieving methods is not supported.
	 */
	public Method getMethod(String name, Class[] argTypes) {
		return null;
	}

	/** Info stored in {@link NamespacelessInterpreter#_execInfos}.
	 */
	private class ExecInfo {
		private final Namespace ns;
		private int count;

		/** A map of (name, value) of variables that are backup. */
		private Map backup;

		private ExecInfo(Namespace ns) {
			this.ns = ns;
		}
		private void backup() {
			if (ns != null) {
//TODO: addChangeListener to monitor what has been changed for each in nss.
				backup = new HashMap();
				final List nss = new LinkedList();
				for (Namespace p = ns; p != null; p = p.getParent())
					nss.add(0, p); //reverse order
				for (Iterator it = nss.iterator(); it.hasNext();) {
					final Namespace ns = (Namespace)it.next();
					for (Iterator e = ns.getVariableNames().iterator();
					e.hasNext();) {
						final String nm = (String)e.next();
						try {
							backup.put(nm, getVariable(nm));
							setVariable(nm, ns.getVariable(nm, true));
						} catch (Throwable ex) {
							log.error("Failed to backup "+nm, ex);
						}
					}
				}
			}
		}
		private void restore() {
			if (backup != null) {
//TODO: removeChangeListener
				for (Iterator it = backup.entrySet().iterator(); it.hasNext();) {
					final Map.Entry me = (Map.Entry)it.next();
					try {
						setVariable((String)me.getKey(), me.getValue());
					} catch (Throwable ex) {
						log.error("Failed to restore "+me.getKey(), ex);
					}
				}
				backup = null;
			}
		}

		public String toString() {
			return "[count="+count+", "+ns+']';
		}
	}
}
