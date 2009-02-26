/* GenericInterpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Feb  8 23:47:29     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.util;

import java.util.Set;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;

import org.zkoss.lang.Objects;
import org.zkoss.xel.Function;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Namespaces;
import org.zkoss.zk.scripting.NamespaceChangeListener;

/**
 * A skeletal class for implementing a interpreter ({@link Interpreter}) that
 * support {@link Namespace}.
 *
 * <p>Derive classes usually override {@link #exec} instead of {@link #interpret};
 * In addition, don't override {@link #getVariable},
 * {@link #setVariable} and {@link #unsetVariable}.
 * Instead, override {@link #get(String)}, {@link #contains(String)},
 * {@link #set(String,Object)} and {@link #unset(String)} instead.
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
 * It also has to implement {@link #get(Namespace,String)}, {@link #contains(Namespace,String)}
 * {@link #set(Namespace,String,Object)} and {@link #unset(Namespace,String)}.
 *
 * <p>Whether to support hierachical namespaces is optional.
 *
 * @author tomyeh
 */
abstract public class GenericInterpreter implements Interpreter {
	/** Used by {@link #getFromNamespace} to denote a variable is not defined.
	 * @since 2.4.0
	 */
	public static final Object UNDEFINED = new Object() {
		public String toString() {return "undefined";}
	};

	/** A list of {@link Namespace}.
	 * Top of it is the current one (if null, it means Namespaces.getCurrent)
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
	 */
	abstract protected void exec(String script);

	/** Tests whether a variable is defined in this interpreter.
	 * Optional. Implement it if the interpreter can tell the difference
	 * between null and undefined.
	 *
	 * <p>By default, it tests whether {@link #get(String)} returns non-null.
	 * @since 2.4.0
	 */
	protected boolean contains(String name) {
		return get(name) != null;
	}
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
	/** Sets the variable to the interpreter.
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

	/** Tests whether a variable is defined in the interpreter's scope
	 * associated with the specified namespace.
	 * Optional. Implement it if the interpreter can tell the difference
	 * between null and undefined.
	 *
	 * <p>By default, it tests whether {@link #get(Namespace, String)}
	 * returns non-null.
	 * @since 2.4.0
	 */
	protected boolean contains(Namespace ns, String name) {
		return get(ns, name) != null;
	}
	/** Gets the variable from the interpreter's scope associated with
	 * the giving namespace.
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
	/** Sets the variable to the interpreter's scope associated with the
	 * giving namespace.
	 * Optional. Implement it if you want to allow Java codes to define
	 * a variable in the interpreter.
	 *
	 * <p>This method is implemented only if the interpreter that supports
	 * hierachical scopes ({@link org.zkoss.zk.scripting.HierachicalAware}).
	 *
	 * <p>Default: the same as {@link #set(String, Object)}.
	 *
	 * <p>{@link #beforeExec} is called first, before this method is invoked.
	 * @since 2.4.0
	 */
	protected void set(Namespace ns, String name, Object value) {
		set(name, value);
	}
	/** Removes the variable from the interpreter.
	 * Optional. Implement it if you want to allow Java codes to undefine
	 * a variable from the interpreter.
	 *
	 * <p>This method is implemented only if the interpreter that supports
	 * hierachical scopes ({@link org.zkoss.zk.scripting.HierachicalAware}).
	 *
	 * <p>Default: the same as {@link #unset(String)}.
	 *
	 * <p>{@link #beforeExec} is called first, before this method is invoked.
	 * @since 2.4.0
	 */
	protected void unset(Namespace ns, String name) {
		unset(name);
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
	/** Returns the variable through namespaces and variable resolvers,
	 * or {@link #UNDEFINED} if the variable not defined.
	 *
	 * <p>It is usually called to search namespaces and variable resolvers,
	 * when the real interpreter failed to find a variable in its own scope.
	 *
	 * <p>Note: We use {@link #UNDEFINED} to denote undefined since 2.4.0,
	 * while null is a valid value.
	 */
	protected Object getFromNamespace(String name) {
		final Namespace ns = getCurrent();
		if (ns != null) {
			Object val = ns.getVariable(name, false);
			if (val != null || ns.containsVariable(name, false))
				return val;
		}
		return getImplicit(name);
	}
	/** Returns the value of the implict variables.
	 * It is called by {@link #getFromNamespace}, so you don't need to
	 * invoke this method if you invoke {@link #getFromNameSpace}.
	 * However, you have to invoke this method as the last step, if you
	 * implement your own getFromNamespace from scratch.
	 * @since 3.6.0
	 */
	protected static Object getImplicit(String name) {
		if ("execution".equals(name)) {
			final Execution exec = Executions.getCurrent();
			if (exec != null) return exec;
		}
		return UNDEFINED;
	}
	/** Returns the variable through the specified namespaces and
	 * variable resolvers, or {@link #UNDEFINED} if the variable is not
	 * defined.
	 *
	 * <p>It is usually called to search namespaces and variable resolvers,
	 * when the real interpreter failed to find a variable in its own scope.
	 *
	 * <p>This method is used with the interpreter that supports
	 * hierachical scopes ({@link org.zkoss.zk.scripting.HierachicalAware}).
	 *
	 * @param ns the namespace to search from (never null).
	 * Note: if {@link #getCurrent} returns null, this method simply returns
	 * null (i.e., ignoring ns).
	 * @param localOnly whether to look for the current namespace only.
	 * If false, it looks up the parent namespace, if any.
	 * @since 3.6.0
	 */
	protected Object getFromNamespace(Namespace ns, String name, boolean localOnly) {
		if (getCurrent() != null) {
			Object val = ns.getVariable(name, localOnly);
			if (val != null || ns.containsVariable(name, localOnly))
				return val;
		}
		return getImplicit(name);
	}
	/** It is a shortcut of getFromNamespace(ns, name, false).
	 * @see #getFromNamespace(Namespace, String, boolean)
	 */
	protected Object getFromNamespace(Namespace ns, String name) {
		return getFromNamespace(ns, name, false);
	}

	//Interpreter//
	public void init(Page owner, String zslang) {
		_owner = owner;
		_zslang = zslang;
	}
	/** Reset the owner ({@link #getOwner}) to null.
	 */
	public void destroy() {
		_owner = null;
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
	 * @since 3.0.0
	 */
	public Function getFunction(String name, Class[] argTypes) {
		return null;
	}
	/** Returns null since retrieving methods is not supported.
	 * @since 3.0.0
	 */
	public Function getFunction(Namespace ns, String name, Class[] argTypes) {
		return null;
	}

	/** Tests whether the variable exists.
	 *
	 * <p>Deriving class shall override {@link #contains(String)}, instead of this method.
	 * @since 2.4.0
	 */
	public boolean containsVariable(String name) {
		beforeExec();
		push(Objects.UNKNOWN);
			//don't use null since it means Namespaces#getCurrent, see below
		try {
			return contains(name);
		} finally {
			pop();
			afterExec();
		}
	}
	/** Retrieve the variable.
	 *
	 * <p>Deriving class shall override {@link #get(String)}, instead of this method.
	 */
	public Object getVariable(String name) {
		beforeExec();
		push(Objects.UNKNOWN);
			//don't use null since it means Namespaces#getCurrent, see below
		try {
			return get(name);
		} finally {
			pop();
			afterExec();
		}
	}
	/** Sets the variable to this interpreter.
	 *
	 * <p>Deriving class shall override {@link #set(String,Object)}, instead of this method.
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
	 * <p>Deriving class shall override {@link #unset(String)}, instead of this method.
	 */
	public final void unsetVariable(String name) {
		beforeExec();
		try {
			unset(name);
		} finally {
			afterExec();
		}
	}

	/** Tests whether the variable exists by using the specified name
	 * as a reference to identify the interpreter's scope.
	 *
	 * <p>Deriving class shall override {@link #contains(Namespace,String)}, instead of this method.
	 * @since 2.4.0
	 */
	public boolean containsVariable(Namespace ns, String name) {
		beforeExec();
		push(Objects.UNKNOWN);
			//don't use null since it means Namespaces#getCurrent, see below
		try {
			return contains(ns, name);
		} finally {
			pop();
			afterExec();
		}
	}
	/** Returns the value of a variable defined in this interpreter's
	 * scope identified by the specified namespace.
	 * Note: it doesn't search the specified namespace ({@link Namespace}).
	 *
	 * <p>Deriving class shall override {@link #get(Namespace,String)},
	 * instead of this method.
	 *
	 * <p>This method is part of {@link org.zkoss.zk.scripting.HierachicalAware}.
	 * It is defined here to simplify the implementation of the
	 * deriving classes, if they support the hierachical scopes.
	 */
	public Object getVariable(Namespace ns, String name) {
		beforeExec();
		push(Objects.UNKNOWN);
			//don't use null since it means Namespaces#getCurrent, see below
		try {
			return get(ns, name);
		} finally {
			pop();
			afterExec();
		}
	}
	/** Sets the value of a variable to this interpreter's scope
	 * identified by the specified namespace.
	 *
	 * <p>Deriving class shall override {@link #set(Namespace,String,Object)},
	 * instead of this method.
	 * @since 2.4.0
	 */
	public final void setVariable(Namespace ns, String name, Object value) {
		beforeExec();
		try {
			set(ns, name, value);
		} finally {
			afterExec();
		}
	}
	/** Removes the value of a variable defined in the interpreter's
	 * scope identified by the specified namespace.
	 *
	 * <p>Deriving class shall override {@link #unset(Namespace,String)}, instead of this method.
	 * @since 2.4.0
	 */
	public final void unsetVariable(Namespace ns, String name) {
		beforeExec();
		try {
			unset(ns, name);
		} finally {
			afterExec();
		}
	}

	/** Use the specified namespace as the active namespace.
	 */
	private void push(Object ns) {
		_nss.add(0, ns);
	}
	/** Remove the active namespace.
	 */
	private void pop() {
		_nss.remove(0);
	}
	/** Returns the current namespace, or null if no namespace is allowed.
	 * Note: if this method returns null, it means the interpreter shall
	 * not search variables defined in ZK namespace.
	 *
	 * <p>This method will handle {@link Namespaces#getCurrent} automatically.
	 */
	protected Namespace getCurrent() {
		if (!_nss.isEmpty()) {
			Object o = _nss.get(0);
			if (o == Objects.UNKNOWN)
				return null; //no namespace allowed
			if (o != null)
				return (Namespace)o;
			//we assume owner's namespace if null, because zscript might
			//define a class that will be invoke thru, say, event listener
			//In other words, interpret is not called, so ns is not specified
		}
		return Namespaces.getCurrent(_owner); //never null
	}
}
