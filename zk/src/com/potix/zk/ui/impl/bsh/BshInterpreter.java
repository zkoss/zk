/* BshInterpreter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  1 14:28:43     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl.bsh;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.io.Reader;
import java.io.StringReader;

import bsh.BshClassManager;
import bsh.NameSpace;
import bsh.Primitive;
import bsh.EvalError;

import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.Interpreter;
import com.potix.zk.ui.util.Namespace;
import com.potix.zk.ui.util.VariableResolver;

/**
 * The interpreter that uses BeanShell to interpret zscript codes.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class BshInterpreter implements Interpreter {
	private final bsh.Interpreter _ip;
	private final Namespace _ns;
	/** A list of {@link VariableResolver}. */
	private List _resolvers;

	public BshInterpreter() {
		_ip = new bsh.Interpreter();
		_ip.setClassLoader(Thread.currentThread().getContextClassLoader());

		final BshClassManager classManager = _ip.getClassManager();
		//final NameSpace oldns = _ip.getNameSpace();
		_ip.setNameSpace(new PageNameSpace(this, classManager, "global"));
		//classManager.removeListener(oldns);
			//ClassManagerImpl doesn't implement removeListener

		_ns = new BshNamespace(_ip.getNameSpace());
	}

	/** Returns the list of {@link VariableResolver} that was added
	 * by {@link #addVariableResolver}, or null if no resolver at all.
	 */
	/*package*/ List getVariableResolvers() {
		return _resolvers;
	}

	//-- Interpreter --//
	public Namespace getNamespace() {
		return _ns;
	}
	public void setVariable(String name, Object val) {
		try {
			_ip.set(name, val);
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

	public void eval(String script, Namespace ns) {
		try {
			if (ns != null)
				_ip.eval(script, (NameSpace)ns.getNativeNamespace());
			else
				_ip.eval(script);
		} catch (EvalError ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
}
