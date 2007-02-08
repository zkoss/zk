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
	abstract protected void unsetVariable(String name);

	private void push(Namespace ns) {
		if (!_execInfos.isEmpty()) {
			final ExecInfo oei = (ExecInfo)_execInfos.get(0);
			if (oei.ns == ns) {
				oei.count++;
				return; //done
			}
			oei.restore();
		}

		final ExecInfo ei = new ExecInfo(ns);
		ei.backup();
		_execInfos.add(0, ei);
	}
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
	public Object getVariable(String name, boolean skipNamespace) {
		//TODO: if (skipNamespace) restore variables
		return getVariable(name);
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
		}
		private void restore() {
		}
	}
	private static final Object VOID = new Object();
}
