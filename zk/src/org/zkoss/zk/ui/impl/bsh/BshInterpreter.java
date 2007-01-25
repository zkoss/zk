/* BshInterpreter.java

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
package org.zkoss.zk.ui.impl.bsh;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.io.Reader;
import java.io.StringReader;

import bsh.BshClassManager;
import bsh.NameSpace;
import bsh.Variable;
import bsh.Primitive;
import bsh.EvalError;
import bsh.UtilEvalError;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Interpreter;
import org.zkoss.zk.ui.util.Namespace;
import org.zkoss.zk.ui.util.VariableResolver;

/**
 * The interpreter that uses BeanShell to interpret zscript codes.
 *
 * @author tomyeh
 */
public class BshInterpreter implements Interpreter { //not a good idea to serialize it
	private final bsh.Interpreter _ip;
	private final Namespace _ns;
	/** A list of {@link VariableResolver}. */
	private List _resolvers;

	public BshInterpreter() {
		_ip = new bsh.Interpreter();
		_ip.setClassLoader(Thread.currentThread().getContextClassLoader());

		final BshClassManager classManager = _ip.getClassManager();
		//final NameSpace oldns = _ip.getNameSpace();
		_ip.setNameSpace(new MyNameSpace(classManager, "global"));
		//classManager.removeListener(oldns);
			//ClassManagerImpl doesn't implement removeListener

		_ns = new BshNamespace(_ip.getNameSpace());
	}

	/** Returns the native interpretor. */
	public bsh.Interpreter getNativeInterpreter() {
		return _ip;
	}

	//-- Interpreter --//
	public Namespace getNamespace() {
		return _ns;
	}
	public void setVariable(String name, Object val) {
		try {
			_ip.set(name, val); //unlike NameSpace.setVariable, set handles null
		} catch (EvalError ex) {
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
	public void unsetVariable(String name) {
		try {
			_ip.unset(name);
		} catch (EvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	}

	public boolean addVariableResolver(VariableResolver resolver) {
		if (resolver == null)
			throw new IllegalArgumentException("null");

		if (_resolvers == null)
			_resolvers = new LinkedList();
		else if (_resolvers.contains(resolver))
			return false;

		_resolvers.add(0, resolver); //FIFO order
		return true;
	}
	public boolean removeVariableResolver(VariableResolver resolver) {
		return _resolvers != null && _resolvers.remove(resolver);
	}

	public void interpret(String script, Namespace ns) {
		try {
			if (ns != null)
				_ip.eval(script, (NameSpace)ns.getNativeNamespace());
			else
				_ip.eval(script);
		} catch (EvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	}

	private class MyNameSpace extends NameSpace {
		private boolean _inGet;

		/** Don't call this method. */
	    private MyNameSpace(BshClassManager classManager, String name) {
	    	super(classManager, name);
	    }

		////
		protected Variable getVariableImpl(String name, boolean recurse)
		throws UtilEvalError {
			//Tom M Yeh: 20060606:
			//We cannot override getVariable because BeanShell use
			//getVariableImpl to resolve a variable recusrivly
			//
			//However, Variable has no public/protected contructor, so
			//we have to store the value back
			//
			//Limitation: here we assume the binding of the variable
			//is immutable. (due to Variable.getValue is not accessible)
			Variable var = super.getVariableImpl(name, recurse);
			if (!_inGet && var == null && _resolvers != null) {
				for (Iterator it = _resolvers.iterator(); it.hasNext();) {
					final Object v =
						((VariableResolver)it.next()).getVariable(name);
					if (v != null) {
						//setVariable will callback this method,
						//so use _inGet to prevent dead loop
						_inGet = true;
						try {
							this.setVariable(name, v, false);
						} finally {
							_inGet = false;
						}

						var = super.getVariableImpl(name, recurse);
						break;
					}
				}
			}
			return var;
		}
	}
}
