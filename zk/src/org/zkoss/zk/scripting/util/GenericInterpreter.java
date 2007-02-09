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
import org.zkoss.zk.scripting.Method;

/**
 * A skeletal class for implementing a interpreter ({@link Interpreter}) that
 * support {@link Namespace}.
 *
 * <p>Derive classes usually override {@link #exec} instead of {@link #interpret};
 * In addition, don't override {@link #getVariable},
 * {@link #setVariable} and {@link #unsetVariable}.
 * Instead, override {@link #get}, {@link #set} and {@link #unset} instead.
 *
 * @author tomyeh
 */
abstract public class GenericInterpreter implements Interpreter {
	/** A list of {@link Namespace}.
	 * Top of it is the active one (may be null).
	 */
	private final List _nss = new LinkedList();
	private Page _owner;
	private String _zslang;

	protected GenericInterpreter() {
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

	/** Gets the variable from the interpreter.
	 * <p>{@link #beforeExec} is called first, before this method is invoked.
	 */
	abstract protected Object get(String name);
	/** Sets the variable from the interpreter.
	 * <p>{@link #beforeExec} is called first, before this method is invoked.
	 */
	abstract protected void set(String name, Object value);
	/** Removes the variable from the interpreter.
	 * <p>{@link #beforeExec} is called first, before this method is invoked.
	 */
	abstract protected void unset(String name);

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
	/** Returns the scripting language this interpreter is associated with.
	 */
	public String getZScriptLanguage() {
		return _zslang;
	}
	/** Locates and returns the variable through namespaces and
	 * variable resolvers.
	 *
	 * <p>It is usually called to search namespaces and variable resolvers,
	 * when the real interpreter failed to find a variable in its own scope.
	 */
	protected Object getFromNamespace(String name) {
		final Namespace ns = getActive();
		final Object v = ns != null ? ns.getVariable(name, false): null;
		return v != null || (ns != null && ns.containsVariable(name, false)) ?
			v: ((PageCtrl)_owner).resolveVariable(name);
	}

	//Interpreter//
	public void init(Page owner, String zslang) {
		_owner = owner;
		_zslang = zslang;
	}

	/** Handles the namespace and then invoke {@link #exec}.
	 * <p>Don't override this method, rather, override {@link #exec}.
	 */
	public void interpret(String script, Namespace ns) {
		final String each =
			_owner.getLanguageDefinition().getEachTimeScript(_zslang);
		if (each != null)
			script = each + '\n' + script;

		beforeExec();
		push(ns);
		try {
			exec(script);
		} finally {
			pop();
			afterExec();
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
	/** Retrieve the variable.
	 *
	 * <p>Deriving class shall override {@link #get}, instead of this method.
	 */
	public Object getVariable(String name, boolean ignoreNamespace) {
		beforeExec();
		if (ignoreNamespace) push(null);
		try {
			return get(name);
		} finally {
			if (ignoreNamespace) pop();
			afterExec();
		}
	}
	/** Sets the variable to this interpreter.
	 *
	 * <p>Deriving class shall override {@link #set}, instead of this method.
	 */
	public final void setVariable(String name, Object value) {
		beforeExec();
		try {
			set(name, value);
		} finally {
			afterExec();
		}
	}
	/** Removes the variable from this interpreter.
	 *
	 * <p>Deriving class shall override {@link #unset}, instead of this method.
	 */
	public final void unsetVariable(String name) {
		beforeExec();
		try {
			unset(name);
		} finally {
			afterExec();
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
