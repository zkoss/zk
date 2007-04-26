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
 * Instead, override {@link #get(String)}, {@link #set} and {@link #unset} instead.
 *
 * <p>If an interpreter doesn't support hierachical scopes,
 * it can simply implement a global scope, and then use
 * {@link #getFromNamespace} to
 * retrieve variables from ZK's hierachical namespaces.
 *
 * <p>If it supports hierachical scopes
 * (example: {@link org.zkoss.zk.scripting.bsh.BSHInterpreter}), it
 * can maintain a one-to-one relationship among interpreter's scopes
 * and ZK's {@link Namespace}. Thus, it can retrieve
 * the correct scope by giving ZK's {@link Namespace}, and vice versa.
 *
 * <p>Whether to support hierachical namespaces is optional.
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

	private static final Namespace EMPTY_NAMESPACE = new Namespace() {
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
	 */
	abstract protected void exec(String script);

	/** Gets the variable from the interpreter.
	 * Optional. Implement it if you want to expose variables defined
	 * in the interpreter to Java codes.
	 *
	 * <p>{@link #beforeExec} is called first, before this method is invoked.
	 *
	 * <p>An empty (and fake) namespace is pushed so {@link #getFromNamespace}
	 * always returns null.
	 */
	protected Object get(String name) {
		return null;
	}
	/** Gets the variable from the interpreter's scope of the giving
	 * namespace.
	 * Optional. Implement it if you want to expose variables defined
	 * in the interpreter to Java codes.
	 *
	 * <p>This method is implemented only if the interpreter that supports
	 * hierachical scopes ({@link org.zkoss.zk.scripting.HierachicalAware}).
	 *
	 * <p>Default: the same as {@link #get(String)}.
	 *
	 * <p>{@link #beforeExec} is called first, before this method is invoked.
	 *
	 * <p>An empty (and fake) namespace is pushed so {@link #getFromNamespace}
	 * always returns null.
	 */
	protected Object get(Namespace ns, String name) {
		return get(name);
	}
	/** Sets the variable from the interpreter.
	 * Optional. Implement it if you want to allow Java codes to define
	 * a variable in the interpreter.
	 *
	 * <p>{@link #beforeExec} is called first, before this method is invoked.
	 */
	protected void set(String name, Object value) {
	}
	/** Removes the variable from the interpreter.
	 * Optional. Implement it if you want to allow Java codes to undefine
	 * a variable from the interpreter.
	 *
	 * <p>{@link #beforeExec} is called first, before this method is invoked.
	 */
	protected void unset(String name) {
	}

	/** Called before {@link #exec}.
	 * <p>Default: call {@link #beforeExec} and push the namespace
	 * as the active one.
	 */
	protected void beforeInterpret(Namespace ns) {
		beforeExec();
		push(ns); //getFromNamespace will handle null
	}
	/** Called after {@link #exec}.
	 * <p>Default: call {@link #afterExec}.
	 */
	protected void afterInterpret(Namespace ns) {
		pop();
		afterExec();
	}
	/** Called before {@link #exec}, {@link #get} and many others.
	 * <p>Default: does nothing.
	 */
	protected void beforeExec() {
	}
	/** Called after {@link #exec}, {@link #get} and many others.
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
		return getCurrent().getVariable(name, false);
	}
	/** Locates and returns the variable through the specified namespaces and
	 * variable resolvers.
	 *
	 * <p>It is usually called to search namespaces and variable resolvers,
	 * when the real interpreter failed to find a variable in its own scope.
	 *
	 * <p>This method is used with the interpreter that supports
	 * hierachical scopes ({@link org.zkoss.zk.scripting.HierachicalAware}).
	 *
	 * @param ns the namespace to search from
	 */
	protected Object getFromNamespace(Namespace ns, String name) {
		return getCurrent() != EMPTY_NAMESPACE ? ns.getVariable(name, false): null;
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

		beforeInterpret(ns);
		try {
			exec(script);
		} finally {
			afterInterpret(ns);
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
	public Object getVariable(String name) {
		beforeExec();
		push(EMPTY_NAMESPACE);
			//don't use null since it means Namespaces#getCurrent, see below
		try {
			return get(name);
		} finally {
			pop();
			afterExec();
		}
	}
	/** Retrieve the variable by using the specified namespace
	 * as a reference.
	 *
	 * <p>Deriving class shall override {@link #get(Namespace ns, String)},
	 * instead of this method.
	 *
	 * <p>This method is part of {@link org.zkoss.zk.scripting.HierachicalAware}.
	 * It is defined here to simplify the implementation of the
	 * deriving classes, if they support the hierachical scopes.
	 */
	public Object getVariable(Namespace ns, String name) {
		beforeExec();
		push(EMPTY_NAMESPACE);
			//don't use null since it means Namespaces#getCurrent, see below
		try {
			return get(ns, name);
		} finally {
			pop();
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
