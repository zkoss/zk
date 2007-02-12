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

import java.util.Set;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.PageCtrl;

import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Namespaces;
import org.zkoss.zk.scripting.Method;
import org.zkoss.zk.scripting.NamespaceChangeListener;

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
	 * Top of it is the current one (if null, it means Namespaces.getCurrent)
	 */
	private final List _nss = new LinkedList();
	private Page _owner;
	private String _zslang;

	private static final Namespace _emptyns = new Namespace() {
		public Set getVariableNames() {
			return Collections.EMPTY_SET;
		}
		public boolean containsVariable(String name, boolean local) {
			return false;
		}
		public Object getVariable(String name, boolean local) {
			return null;
		}
		public void setVariable(String name, Object value, boolean local) {
		}
		public void unsetVariable(String name, boolean local) {
		}
		public Namespace getParent() {
			return null;
		}
		public void setParent(Namespace parent) {
		}
		public boolean addChangeListener(NamespaceChangeListener listener) {
			return false;
		}
		public boolean removeChangeListener(NamespaceChangeListener listener) {
			return false;
		}
	};

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
	/** Locates and returns the variable through namespaces and
	 * variable resolvers.
	 *
	 * <p>It is usually called to search namespaces and variable resolvers,
	 * when the real interpreter failed to find a variable in its own scope.
	 */
	protected Object getFromNamespace(String name) {
		final Namespace ns = getCurrent();
		final Object v = ns.getVariable(name, false);
		return v != null || ns.containsVariable(name, false) ?
			v: ((PageCtrl)_owner).resolveVariable(name);
	}

	//Interpreter//
	public void init(Page owner, String zslang) {
		_owner = owner;
		_zslang = zslang;
	}
	/** Does nothing. */
	public void destroy() {
	}
	public Page getOwner() {
		return _owner;
	}
	public String getLanguage() {
		return _zslang;
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
		push(ns); //getFromNamespace will handle null
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
		if (ignoreNamespace) push(_emptyns);
			//don't use null since it means Namespaces#getCurrent, see below
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
	/** Returns the current namespace, never null.
	 *
	 * <p>This method will handle {Namespace#getCurrent} automatically.
	 */
	protected Namespace getCurrent() {
		Namespace ns = _nss.isEmpty() ? null: (Namespace)_nss.get(0);
		return ns != null ? ns: Namespaces.getCurrent(_owner);
			//we assume owner's namespace if null, because zscript might
			//define a class that will be invoke thru, say, event listener
			//In other words, interpret is not called, so ns is not specified
	}
}
