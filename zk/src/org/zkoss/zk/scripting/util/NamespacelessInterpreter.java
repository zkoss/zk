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
import org.zkoss.zk.scripting.NamespaceChangeListener;

/**
 * A skeletal class for implementing a interpreter ({@link Interpreter}) that
 * doesn't support {@link Namespace}.
 *
 * <p>Deriving classes usually overrides {@link #exec} instead of {@link #interpret}.
 * In addition, don't override {@link #getVariable},
 * {@link #setVariable} and {@link #unsetVariable}.
 * Instead, override {@link #get}, {@link #set} and {@link #unset} instead.
 *
 * @author tomyeh
 */
abstract public class NamespacelessInterpreter implements Interpreter {
	private static final Log log = Log.lookup(NamespacelessInterpreter.class);

	/** A list of {@link ExecInfo}. */
	private final List _execInfos = new LinkedList();

	protected NamespacelessInterpreter() {
	}

	//interface to override//
	/** Executes the specified script.
	 * Deriving class shall provide an implementation of this method, rather
	 * than overriding {@link #interpret}.
	 *
	 * <p>It is invoked by {@link #interpret} after 'copying' variables from
	 * the namespace to the interpreter.
	 * <p>{@link #beforeExec} is called first, before this method is invoked.
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

	/** Called before {@link #exec} or setting a variable.
	 * <p>Default: does nothing.
	 */
	protected void beforeExec() {
	}
	/** Called after {@link #exec} or setting a variable.
	 * <p>Default: does nothing.
	 */
	protected void afterExec() {
	}

	/** Makes ns as the active namespace and copies all its variable
	 * to this interpreter.
	 */
	private void push(Namespace ns) {
		//Note: if execinfo.count < 0, it means that all execinfo shall
		//be popped, but we keep one for improving performance
		final ExecInfo oei = getActive();
		if (oei != null) {
			if (oei.ns == ns) {
				oei.count++; //reuse it
				return; //done
			}

			oei.restore();

			if (oei.count < 0) //special case
				_execInfos.remove(0);
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

				((ExecInfo)_execInfos.get(0)).backup();
					//make it effective again
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
	 * <p>Deriving class shall override {@link #exec} instead of
	 * this method.
	 */
	public final void interpret(String script, Namespace ns) {
		beforeExec();
		push(ns);
		try {
			exec(script);
		} finally {
			pop();
			afterExec();
		}
	}
	/** Retrieve the variable.
	 *
	 * <p>Deriving class shall override {@link #get}, instead of this method.
	 */
	public final Object getVariable(String name, boolean ignoreNamespace) {
		beforeExec();
		try {
			ExecInfo ei = null;
			if (ignoreNamespace) {
				ei = getActive();
				if (ei != null) {
					ei.restore();
					if (ei.count < 0) {
						ei = null; //no need to backup again
						_execInfos.remove(0);
					}
				}
			}

			try {
				return get(name);
			} finally {
				if (ei != null)
					ei.backup();
			}
		} finally {
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
		private Map _backup;
		/** A list of listener. */
		private List _listeners;

		private ExecInfo(Namespace ns) {
			this.ns = ns;
		}
		private void backup() {
			if (this.ns != null) {
				try {
					backup0();
				} catch (Throwable ex) {
					log.error("Failed to backup "+this, ex);
				}
			}
		}
		private void backup0() {
			_backup = new HashMap();
			_listeners = new LinkedList();

			final List nss = new LinkedList();
			for (Namespace p = this.ns; p != null; p = p.getParent()) {
				ChangeListener l = new ChangeListener(this, p);
				_listeners.add(l);

				nss.add(0, p); //reverse order
			}
			for (Iterator it = nss.iterator(); it.hasNext();) {
				final Namespace p = (Namespace)it.next();
				for (Iterator e = p.getVariableNames().iterator();
				e.hasNext();) {
					final String nm = (String)e.next();
					backup(nm, p.getVariable(nm, true));
				}
			}
		}
		/** Backup a variable.
		 */
		private void backup(String name, Object newvalue) {
			try {
				if (!_backup.containsKey(name))
					_backup.put(name, get(name));
				set(name, newvalue);
			} catch (Throwable ex) {
				log.error("Failed to backup "+name, ex);
			}
		}
		private void restore() {
			if (_backup != null) {
				try {
					restore0();
				} catch (Throwable ex) {
					log.error("Failed to restore "+this, ex);
				}
			}
		}
		private void restore0() {
			for (Iterator it = _listeners.iterator(); it.hasNext();) {
				final ChangeListener l = (ChangeListener)it.next();
				try {
					l.unlisten();
				} catch (Throwable ex) {
					log.error("Failed to unlisten "+l, ex);
				}
			}
			_listeners = null;

			for (Iterator it = _backup.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				try {
					set((String)me.getKey(), me.getValue());
				} catch (Throwable ex) {
					log.error("Failed to restore "+me.getKey(), ex);
				}
			}
			_backup = null;
		}

		public String toString() {
			return "[count="+this.count+", "+this.ns+']';
		}
	}

	private class ChangeListener implements NamespaceChangeListener {
		private final ExecInfo _ei;
		private final Namespace _ns;
		private ChangeListener(ExecInfo ei, Namespace ns) {
			_ei = ei;
			_ns = ns;
			_ns.addChangeListener(this);
		}
		private void unlisten() {
			_ns.removeChangeListener(this);
		}

		/** Whether the variable is not obscure by child namespace.
		 */
		private boolean isEffective(String name) {
			for (Namespace p = _ei.ns; p != _ns; p = p.getParent())
				if (p.containsVariable(name, true))
					return false;
			return true;
		}
		public void onAdd(String name, Object value) {
			if (isEffective(name)) {
				beforeExec();
				try {
					_ei.backup(name, value);
				} finally {
					afterExec();
				}
			}
		}
		public void onRemove(String name) {
			if (isEffective(name)) {
				beforeExec();
				try {
					_ei.backup(name, null);
				} finally {
					afterExec();
				}
			}
		}
		public void onParentChanged(Namespace newparent) {
			beforeExec();
			try {
				_ei.restore();
				_ei.backup();
			} finally {
				afterExec();
			}
		}
		public String toString() {
			return "["+_ei+" for "+_ns+']';
		}
	}
}
