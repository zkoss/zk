/* ActionNode.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 15:03:33     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.dsp.impl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.Writer;
import java.io.IOException;

import javax.servlet.jsp.el.ELException;

import com.potix.lang.D;
import com.potix.lang.Classes;
import com.potix.lang.Exceptions;
import com.potix.util.logging.Log;
import com.potix.web.servlet.ServletException;

import com.potix.web.servlet.dsp.action.Action;

/**
 * Represents an action node.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
class ActionNode extends Node {
	private static final Log log = Log.lookup(ActionNode.class);

	private final Class _cls;
	private List _attrs;
	private final int _nLines;

	/**
	 * @param nLines which line this action is located.
	 */
	ActionNode(Class cls, int nLines) {
		_cls = cls;
		_nLines = nLines;
	}

	void interpret(InterpretContext ic)
	throws javax.servlet.ServletException, IOException {
		final Action parent = ic.action;
		try {
			ic.action = newAction();
			//1. apply attributes
			if (_attrs != null) {
				for (Iterator it = _attrs.iterator(); it.hasNext();)
					((Attr)it.next()).apply(ic, ic.action);
			}

			//2. render
			ic.action.render(
				new ActionContextImpl(ic, parent, this, _nLines),
				_children != null);
		} finally {
			ic.action = parent;
		}
	}
	/** Creates an instance of Action. */
	private Action newAction() throws javax.servlet.ServletException {
		try {
			return (Action)_cls.newInstance();
		} catch (Exception ex) {
			throw new ServletException("Failed to create "+_cls+". Cause: "+Exceptions.getMessage(ex));
		}
	}

	/** Returns the line number */
	int getLineNumber() {
		return _nLines;
	}
	/** Renders the nested fragment. */
	void renderFragment(InterpretContext ic)
	throws javax.servlet.ServletException, IOException {
		if (_children == null)
			return;
		for (Iterator it = _children.iterator(); it.hasNext();) {
			((Node)it.next()).interpret(ic);
		}
	}

	/** Adds an attribute. */
	void addAttribute(String nm, String val)
	throws NoSuchMethodException, ClassCastException {
		if (nm == null || val == null)
			throw new IllegalArgumentException("null");
		if (_attrs == null)
			_attrs = new LinkedList();

		final Method mtd = (Method)Classes.getAccessibleObject(
			_cls, nm, new Class[] {null},
			Classes.B_SET|Classes.B_PUBLIC_ONLY|Classes.B_METHOD_ONLY);
		if (val.indexOf("${") >= 0) { //
			_attrs.add(new Attr(mtd, val, true));
		} else {
			_attrs.add(new Attr(mtd,
				Classes.coerce(mtd.getParameterTypes()[0], val), false));
		}
	}
	private static class Attr {
		private final Method _method;
		private final Object _value;
		private final boolean _bExpr;
		private Attr(Method mtd, Object val, boolean expr) {
			_method = mtd;
			_value = val;
			_bExpr = expr;
		}
		/** Applies this attribute to the specified action. */
		private void apply(InterpretContext ic, Action action)
		throws javax.servlet.ServletException {
			final Object[] args = new Object[1];
			try {
				if (_bExpr) {
					args[0] = ic.dc.getExpressionEvaluator().evaluate(
						(String)_value, Object.class, ic.resolver, ic.mapper);
					if (D.ON && log.finerable()) log.finer("attr "+_method.getName()+"="+_value+" to "+args[0]);
				} else {
					args[0] = _value;
				}
				_method.invoke(action, args);
			} catch (Exception ex) {
				if (log.debugable()) log.debug(ex);
				throw new ServletException("Failed to invoke "+_method+" with "+(_bExpr ? _value: args[0])
					+". Cause: "+Exceptions.getMessage(ex)+"\n"+Exceptions.getFirstStackTrace(ex));
			}
		}
	}

	public String toString() {
		return "Action["+_cls.getName()+']';
	}
}
