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
import bsh.UtilEvalError;

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
	private final MyInterpreter _ip;
	private final Namespace _ns;
	/** A list of {@link VariableResolver}. */
	private List _resolvers;

	public BshInterpreter() {
		_ip = new MyInterpreter();
		_ip.setNameSpace(new MyNameSpace(_ip.getClassManager(), "global"));
		_ns = new BshNamespace(_ip.getNameSpace());
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

	//help classes//
	private static class MyInterpreter extends bsh.Interpreter {
		private MyInterpreter() {
			//We don't use Interpreter() since we will assign another
			//NameSpace later to support addVariableResolver.
			//LiteNameSpace is used because its performance is better
			//-- no loading packages
			super(_in,  System.out, System.err, false,
				new LiteNameSpace(null, "global"));
			this.evalOnly = true;
			try {
				set( "bsh.evalOnly", new Primitive(true) );
			} catch (EvalError ex) {
				throw UiException.Aide.wrap(ex);
			}

			setClassLoader(Thread.currentThread().getContextClassLoader());
		}
	}
	private static final Reader _in = new StringReader("");

	/** Extends NameSpace to support _resolvers. */
	private class MyNameSpace extends NameSpace {
	    private MyNameSpace(BshClassManager classManager, String name) {
	    	super(classManager, name);
	    }

		public Object getVariable( String name, boolean recurse ) 
		throws UtilEvalError {
			final Object o = super.getVariable(name, recurse);

			if ((o == null || o == Primitive.VOID)
			&& _resolvers != null) {
				for (Iterator it = _resolvers.iterator(); it.hasNext();) {
					final Object v =
						((VariableResolver)it.next()).getVariable(name);
					if (v != null) return v;
				}
			}

			return o;
		}
	}
}
