/* GenericInterpreter.java

	Purpose:
		
	Description:
		
	History:
		Thu Feb  8 23:47:29     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.ext.Scopes;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.scripting.Interpreter;

/**
 * A skeletal class for implementing a interpreter ({@link Interpreter}).
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
 * retrieve variables from ZK's hierachical scopes.
 *
 * <p>If it supports hierachical scopes
 * (example: {@link org.zkoss.zk.scripting.bsh.BSHInterpreter}), it
 * can maintain a one-to-one relationship among interpreter's scopes
 * and ZK's {@link IdSpace}. Thus, it can retrieve
 * the correct scope by giving ZK's {@link IdSpace}, and vice versa.
 * It also has to implement {@link #get(Scope,String)}, {@link #contains(Scope,String)}
 * {@link #set(Scope,String,Object)} and {@link #unset(Scope,String)}.
 *
 * <p>Whether to support hierachical scopes is optional.
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

	/** A list of {@link Scope}.
	 * Top of it is the current one (if null, it means Scopes.getCurrent)
	 */
	private final List _scopes = new LinkedList();
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
	 * <p>An empty (and fake) scope is pushed so {@link #getFromNamespace}
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
	 * associated with the specified scope.
	 * Optional. Implement it if the interpreter can tell the difference
	 * between null and undefined.
	 *
	 * <p>By default, it tests whether {@link #get(Scope, String)}
	 * returns non-null.
	 * @since 5.0.0
	 */
	protected boolean contains(Scope scope, String name) {
		return get(scope, name) != null;
	}
	/** Gets the variable from the interpreter's scope associated with
	 * the giving scope.
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
	 * <p>An empty (and fake) scope is pushed so {@link #getFromNamespace}
	 * always returns null.
	 * @since 5.0.0
	 */
	protected Object get(Scope scope, String name) {
		return get(name);
	}
	/** Sets the variable to the interpreter's scope associated with the
	 * giving scope.
	 * Optional. Implement it if you want to allow Java codes to define
	 * a variable in the interpreter.
	 *
	 * <p>This method is implemented only if the interpreter that supports
	 * hierachical scopes ({@link org.zkoss.zk.scripting.HierachicalAware}).
	 *
	 * <p>Default: the same as {@link #set(String, Object)}.
	 *
	 * <p>{@link #beforeExec} is called first, before this method is invoked.
	 * @since 5.0.0
	 */
	protected void set(Scope scope, String name, Object value) {
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
	 * @since 5.0.0
	 */
	protected void unset(Scope scope, String name) {
		unset(name);
	}

	/** Called before {@link #exec}.
	 * <p>Default: call {@link #beforeExec} and push the scope
	 * as the active one.
	 * @since 5.0.0
	 */
	protected void beforeInterpret(Scope scope) {
		beforeExec();
		push(scope); //getFromNamespace will handle null
	}
	/** Called after {@link #exec}.
	 * <p>Default: call {@link #afterExec}.
	 * @since 5.0.0
	 */
	protected void afterInterpret(Scope scope) {
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
	/** Returns the variable through scopes and variable resolvers,
	 * or {@link #UNDEFINED} if the variable not defined.
	 *
	 * <p>It is usually called to search scopes and variable resolvers,
	 * when the real interpreter failed to find a variable in its own scope.
	 *
	 * <p>Note: We use {@link #UNDEFINED} to denote undefined since 2.4.0,
	 * while null is a valid value.
	 */
	protected Object getFromNamespace(String name) {
		final Scope scope = getCurrent();
		if (scope != null) { //null means no scope allowed!
			final Execution exec = Executions.getCurrent();
			if (exec != null && exec != scope) {
				Object val = exec.getAttribute(name);
				if (val != null /*|| exec.hasAttribute(name)*/) //request's hasAttribute same as getAttribute
					return val;
			}

			if (scope instanceof Component) {
				Component comp = (Component)scope;
				Object val = comp.getAttributeOrFellow(name, true);
				if (val != null || comp.hasAttributeOrFellow(name, true))
					return val;

				Page page = comp.getPage();
				if (page != null) {
					val = page.getXelVariable(null, null, name, true);
					if (val != null)
						return val;
				}
			} else if (scope instanceof Page) {
				Page page = (Page)scope;
				Object val = page.getAttributeOrFellow(name, true);
				if (val != null || page.hasAttributeOrFellow(name, true))
					return val;

				val = page.getXelVariable(null, null, name, true);
				if (val != null)
					return val;
			} else {
				Object val = scope.getAttribute(name, true);
				if (val != null || scope.hasAttribute(name, true))
					return val;
			}
		}
		return getImplicit(name);
	}
	/** Returns the value of the implict variables.
	 * It is called by {@link #getFromNamespace}, so you don't need to
	 * invoke this method if you invoke {@link #getFromNamespace}.
	 * However, you have to invoke this method as the last step, if you
	 * implement your own getFromNamespace from scratch.
	 * @since 3.6.0
	 */
	protected static Object getImplicit(String name) {
		return Scopes.getImplicit(name, UNDEFINED);
	}
	/** Returns the variable through the specified scopes and
	 * variable resolvers, or {@link #UNDEFINED} if the variable is not
	 * defined.
	 *
	 * <p>It is usually called to search scopes and variable resolvers,
	 * when the real interpreter failed to find a variable in its own scope.
	 *
	 * <p>This method is used with the interpreter that supports
	 * hierachical scopes ({@link org.zkoss.zk.scripting.HierachicalAware}).
	 *
	 * @param scope the scope to search from (never null).
	 * Note: if {@link #getCurrent} returns null, this method simply returns
	 * null (i.e., ignoring scope).
	 * @param recurse whether to look for the parent scope, if any.
	 * @since 5.0.0
	 */
	protected Object getFromNamespace(Scope scope, String name, boolean recurse) {
		if (getCurrent() != null) { //null means no scope allowed!
			Object val = scope.getAttribute(name, recurse);
			if (val != null || scope.hasAttribute(name, recurse))
				return val;
		}
		return getImplicit(name);
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

	/** Handles the scope and then invoke {@link #exec}.
	 * <p>Don't override this method, rather, override {@link #exec}.
	 * @since 5.0.0
	 */
	public void interpret(String script, Scope scope) {
		final String each =
			_owner.getLanguageDefinition().getEachTimeScript(_zslang);
		if (each != null)
			script = each + '\n' + script;

		beforeInterpret(scope);
		try {
			exec(script);
		} finally {
			afterInterpret(scope);
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
	 * @since 5.0.0
	 */
	public Function getFunction(Scope scope, String name, Class[] argTypes) {
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
			//don't use null since it means Scopes#getCurrent, see below
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
			//don't use null since it means Scopes#getCurrent, see below
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
	 * <p>Deriving class shall override {@link #contains(Scope,String)}, instead of this method.
	 * @since 5.0.0
	 */
	public boolean containsVariable(Scope scope, String name) {
		beforeExec();
		push(Objects.UNKNOWN);
			//don't use null since it means Scopes#getCurrent, see below
		try {
			return contains(scope, name);
		} finally {
			pop();
			afterExec();
		}
	}
	/** Returns the value of a variable defined in this interpreter's
	 * scope identified by the specified scope.
	 * Note: it doesn't search the specified scope ({@link Scope}).
	 *
	 * <p>Deriving class shall override {@link #get(Scope,String)},
	 * instead of this method.
	 *
	 * <p>This method is part of {@link org.zkoss.zk.scripting.HierachicalAware}.
	 * It is defined here to simplify the implementation of the
	 * deriving classes, if they support the hierachical scopes.
	 * @since 5.0.0
	 */
	public Object getVariable(Scope scope, String name) {
		beforeExec();
		push(Objects.UNKNOWN);
			//don't use null since it means Scopes#getCurrent, see below
		try {
			return get(scope, name);
		} finally {
			pop();
			afterExec();
		}
	}
	/** Sets the value of a variable to this interpreter's scope
	 * identified by the specified scope.
	 *
	 * <p>Deriving class shall override {@link #set(Scope,String,Object)},
	 * instead of this method.
	 * @since 5.0.0
	 */
	public final void setVariable(Scope scope, String name, Object value) {
		beforeExec();
		try {
			set(scope, name, value);
		} finally {
			afterExec();
		}
	}
	/** Removes the value of a variable defined in the interpreter's
	 * scope identified by the specified scope.
	 *
	 * <p>Deriving class shall override {@link #unset(Scope,String)}, instead of this method.
	 * @since 5.0.0
	 */
	public final void unsetVariable(Scope scope, String name) {
		beforeExec();
		try {
			unset(scope, name);
		} finally {
			afterExec();
		}
	}

	/** Use the specified scope as the active scope.
	 */
	private void push(Object scope) {
		_scopes.add(0, scope);
	}
	/** Remove the active scope.
	 */
	private void pop() {
		_scopes.remove(0);
	}
	/** Returns the current scope, or null if no scope is allowed.
	 * Note: if this method returns null, it means the interpreter shall
	 * not search variables defined in ZK scope.
	 *
	 * <p>This method will handle {@link Scopes#getCurrent} automatically.
	 * @since 5.0.0
	 */
	protected Scope getCurrent() {
		if (!_scopes.isEmpty()) {
			Object o = _scopes.get(0);
			if (o == Objects.UNKNOWN)
				return null; //no scope allowed
			if (o != null)
				return (Scope)o;
			//we assume owner's scope if null, because zscript might
			//define a class that will be invoke thru, say, event listener
			//In other words, interpret is not called, so scope is not specified
		}
		return Scopes.getCurrent(_owner); //never null
	}
}
