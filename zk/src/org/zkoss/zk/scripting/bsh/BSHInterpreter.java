/* BSHInterpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  1 14:28:43     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.scripting.bsh;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import bsh.BshClassManager;
import bsh.NameSpace;
import bsh.BshMethod;
import bsh.Variable;
import bsh.Primitive;
import bsh.EvalError;
import bsh.UtilEvalError;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Method;

/**
 * The interpreter that uses BeanShell to interpret zscript codes.
 *
 * @author tomyeh
 */
public class BSHInterpreter implements Interpreter { //not a good idea to serialize it
	private final Page _page;
	private final bsh.Interpreter _ip;
	private final NS _bshns;
	/** A list of {@link Namespace}.
	 * Top of it is the active one (may be null).
	 */
	private final List _nss = new LinkedList();

	public BSHInterpreter(Page owner) {
		_page = owner;
		_ip = new bsh.Interpreter();
		_ip.setClassLoader(Thread.currentThread().getContextClassLoader());

		_bshns = new NS(_ip.getClassManager(), "global");
		_ip.setNameSpace(_bshns);
	}

	/** Returns the native interpretor. */
	public bsh.Interpreter getNativeInterpreter() {
		return _ip;
	}

	//-- Interpreter --//
	public Class getClass(String clsnm) {
		try {
			return _bshns.getClass(clsnm);
		} catch (UtilEvalError ex) {
			throw new UiException("Failed to load class "+clsnm, ex);
		}
	}
	public Method getMethod(String name, Class[] argTypes) {
		if (argTypes == null)
			argTypes = new Class[0];

		try {
		 	final BshMethod m = _bshns.getMethod(name, argTypes, false);
		 	return m != null ? new BSHMethod(m): null;
		} catch (UtilEvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	}

	public Object getVariable(String name) {
		try {
			return Primitive.unwrap(_ip.get(name));
		} catch (EvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	/*public void setVariable(String name, Object val) {
		try {
			_ip.set(name, val); //unlike NameSpace.setVariable, set handles null
		} catch (EvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	public void unsetVariable(String name) {
		try {
			_ip.unset(name);
		} catch (EvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	}*/

	public void interpret(String script, Namespace ns) {
		try {
			push(ns);
			try {
				_ip.eval(script);
			} finally {
				pop();
			}
		} catch (EvalError ex) {
			throw UiException.Aide.wrap(ex);
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
	private Namespace getActive() {
		return _nss.isEmpty() ? null: (Namespace)_nss.get(0);
	}

	private class NS extends NameSpace {
		private boolean _inGet;

		/** Don't call this method. */
	    private NS(BshClassManager classManager, String name) {
	    	super(classManager, name);
	    }

		//super//
		protected Variable getVariableImpl(String name, boolean recurse)
		throws UtilEvalError {
			//Tom M Yeh: 20060606:
			//We cannot override getVariable because BeanShell use
			//getVariableImpl to resolve a variable recusrivly
			//
			//setVariable will callback this method,
			//so use _inGet to prevent dead loop
			Variable var = super.getVariableImpl(name, recurse);
			if (!_inGet && var == null) {
				final Namespace ns = getActive();
				Object v = ns != null ? ns.getVariable(name, false): null;
				if (v == null)
					v = ((PageCtrl)_page).resolveVariable(name);
				if (v != null) {
			//Variable has no public/protected contructor, so we have to
			//store the value back (with setVariable) and retrieve again
					_inGet = true;
					try {
						this.setVariable(name,
							v != null ? v: Primitive.NULL, false);
						var = super.getVariableImpl(name, false);
						this.unsetVariable(name); //restore
					} finally {
						_inGet = false;
					}
				}
			}
			return var;
		}
	}
}
