/* GenericInterpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Feb  8 23:47:29     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.util;

import java.util.List;
import java.util.LinkedList;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.PageCtrl;

import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.Namespace;

/**
 * A skeletal class for implementing a interpreter ({@link Interpreter}) that
 * support {@link Namespace}.
 *
 * <p>Derive classes usually override {@link #exec} instead of {@link #interpret};
 * override {@link #getVariable(String)}) instead of {@link #getVariable(String, boolean)}.
 *
 * @author tomyeh
 */
abstract public class GenericInterpreter implements Interpreter {
	/** The owner. */
	private final Page _owner;
	/** A list of {@link Namespace}.
	 * Top of it is the active one (may be null).
	 */
	private final List _nss = new LinkedList();

	protected GenericInterpreter(Page owner) {
		_owner = owner;
	}

	//interface to override//
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

	/** Called before {@link #exec}.
	 * <p>Default: does nothing.
	 */
	protected void beforeExec() {
	}
	/** Called after {@link #exec}.
	 * <p>Default: does nothing.
	 */
	protected void afterExec() {
	}

	//utilities//
	/** Returns the owner.
	 */
	public Page getOwner() {
		return _owner;
	}
	/** Locates and returns the variable through namespaces and
	 * variable resolvers.
	 *
	 * <p>It is usually called to search namespaces and variable resolvers,
	 * when the real interpreter failed to find a variable in its own scope.
	 */
	protected Object locateVariable(String name) {
		final Namespace ns = getActive();
		final Object v = ns != null ? ns.getVariable(name, false): null;
		return v != null || ns.containsVariable(name, false) ? v:
			((PageCtrl)_owner).resolveVariable(name);
	}

	//Interpreter//
	public void interpret(String script, Namespace ns) {
		beforeExec();
		push(ns);
		try {
			exec(script);
		} finally {
			pop();
			afterExec();
		}
	}
	public Object getVariable(String name, boolean skipNamespace) {
		if (skipNamespace) push(null);
		try {
			return getVariable(name);
		} finally {
			if (skipNamespace) pop();
		}
	}
	/** Use the specified namespace as the active namespace.
	 */
	private void push(Namespace ns) {
		_nss.add(0, ns);
	}
	/** Remove the active namespace.
	 */
	private void pop() {
		_nss.remove(0);
	}
	/** Returns the active namespace.
	 */
	protected Namespace getActive() {
		return _nss.isEmpty() ? null: (Namespace)_nss.get(0);
	}
}
