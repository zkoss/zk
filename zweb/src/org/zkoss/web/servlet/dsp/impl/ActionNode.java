/* ActionNode.java

	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 15:03:33     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.impl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Exceptions;
import org.zkoss.util.logging.Log;
import org.zkoss.xel.Expression;
import org.zkoss.xel.XelException;

import org.zkoss.web.servlet.dsp.DspException;
import org.zkoss.web.servlet.dsp.action.Action;

/**
 * Represents an action node.
 *
 * @author tomyeh
 */
class ActionNode extends Node {
	private static final Log log = Log.lookup(ActionNode.class);

	private final Class<?> _cls;
	private List<Attr> _attrs;
	private final int _nLines;

	/**
	 * @param nLines which line this action is located.
	 */
	ActionNode(Class<?> cls, int nLines) {
		_cls = cls;
		_nLines = nLines;
	}

	void interpret(InterpretContext ic)
	throws DspException, IOException {
		final Action parent = ic.action;
		try {
			ic.action = newAction();
			//1. apply attributes
			if (_attrs != null)
				for (Attr attr: _attrs)
					attr.apply(ic, ic.action);

			//2. render
			ic.action.render(
				new ActionContextImpl(ic, parent, this, _nLines),
				_children != null);
		} finally {
			ic.action = parent;
		}
	}
	/** Creates an instance of Action. */
	private Action newAction() throws DspException {
		try {
			return (Action)_cls.newInstance();
		} catch (Exception ex) {
			log.realCauseBriefly(ex); //Web server might 'eat'
			throw DspException.Aide.wrap(ex, "Failed to create "+_cls);
		}
	}

	/** Returns the line number */
	int getLineNumber() {
		return _nLines;
	}
	/** Renders the nested fragment. */
	void renderFragment(InterpretContext ic)
	throws DspException, IOException {
		if (_children == null)
			return;
		for (Iterator it = _children.iterator(); it.hasNext();) {
			((Node)it.next()).interpret(ic);
		}
	}

	/** Adds an attribute. */
	void addAttribute(String nm, String val, ParseContext ctx)
	throws NoSuchMethodException, ClassCastException, XelException {
		if (nm == null || val == null)
			throw new IllegalArgumentException("null");
		if (_attrs == null)
			_attrs = new LinkedList<Attr>();

		final Method mtd = (Method)Classes.getAccessibleObject(
			_cls, nm, new Class[] {null},
			Classes.B_SET|Classes.B_PUBLIC_ONLY|Classes.B_METHOD_ONLY);
		final Class type = mtd.getParameterTypes()[0];
		if (val.indexOf("${") >= 0) {
			_attrs.add(new Attr(mtd,
				ctx.getExpressionFactory().parseExpression(ctx, val, type)));
		} else {
			_attrs.add(new Attr(mtd, Classes.coerce(type, val)));
		}
	}
	private static class Attr {
		private final Method _method;
		private final Object _value;
		/**
		 * @param val the value. Either Expression or String.
		 */
		private Attr(Method mtd, Object val) {
			_method = mtd;
			_value = val;
		}
		/** Applies this attribute to the specified action. */
		private void apply(InterpretContext ic, Action action)
		throws DspException {
			final Object[] args = new Object[1];
			try {
				if (_value instanceof Expression) {
					args[0] = ((Expression)_value).evaluate(ic.xelc);
					//if (D.ON && log.finerable()) log.finer("attr "+_method.getName()+"="+_value+" to "+args[0]);
				} else {
					args[0] = _value;
				}
				_method.invoke(action, args);
			} catch (Exception ex) {
				log.realCauseBriefly(ex); //Web server might 'eat'
				throw DspException.Aide.wrap(ex,
	"Failed to invoke "+_method+" with "+args[0]
	+(args[0] != null ? " @"+args[0].getClass().getName(): ""));
			}
		}
	}

	public String toString() {
		return "Action["+_cls.getName()+']';
	}
}
