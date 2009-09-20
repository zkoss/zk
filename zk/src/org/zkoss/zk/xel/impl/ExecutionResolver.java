/* ExecutionResolver.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 24 12:22:23     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.xel.impl;

import java.util.Collections;

import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ExecutionCtrl;

/**
 * A variable resolver that is based on the specified execution.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ExecutionResolver implements VariableResolver {
	/** The parent resolver. */
	private final VariableResolver _parent;
	private final Execution _exec;
	private Object _self;

	/** Constructs a resolver with a parent and a page.
	 * @param parent the parent resolver (null means ignored).
	 * @param exec the current execution
	 */
	public ExecutionResolver(Execution exec, VariableResolver parent) {
		if (exec == null) throw new NullPointerException();
		_exec = exec;
		_parent = parent;
	}

	/** Sets the self variable.
	 * The self variable also acts as the context to resolve other variables.
	 */
	public void setSelf(Object self) {
		_self = self;
	}
	/** Returns the self variable.
	 */
	public Object getSelf() {
		return _self;
	}

	//-- VariableResolver --//
	public Object resolveVariable(String name) throws XelException {
		if (name == null || name.length() == 0) //just in case
			return null;

		//Note: we have to access keyword frist (rather than component's ns)
		//since our BeanShell interpreter will store back variables
		//and page.getZScriptVariable will return the old value
		//we have 
		if ("arg".equals(name))
			return _exec.getArg();
		if ("componentScope".equals(name)) {
			if (_self instanceof Component)
				return ((Component)_self).getAttributes(Component.COMPONENT_SCOPE);
			return Collections.EMPTY_MAP;
		}
		if ("desktopScope".equals(name))
			return _exec.getDesktop().getAttributes();
		if ("desktop".equals(name))
			return _exec.getDesktop();
		if ("execution".equals(name))
			return _exec;
		if ("pageScope".equals(name)) {
			if (_self instanceof Component)
				return ((Component)_self).getAttributes(Component.PAGE_SCOPE);
			if (_self instanceof Page)
				return ((Page)_self).getAttributes();
			final Page page = ((ExecutionCtrl)_exec).getCurrentPage();
			return page != null ? page.getAttributes(): Collections.EMPTY_MAP;
		}
		if ("page".equals(name)) {
			if (_self instanceof Component)
				return ((Component)_self).getPage();
			if (_self instanceof Page)
				return (Page)_self;
			return ((ExecutionCtrl)_exec).getCurrentPage();
		}
		if ("requestScope".equals(name))
			return _exec.getAttributes();
		if ("self".equals(name))
			return _self;
		if ("sessionScope".equals(name))
			return _exec.getDesktop().getSession().getAttributes();
		if ("session".equals(name))
			return _exec.getDesktop().getSession();
		if ("spaceOwner".equals(name)) {
			if (_self instanceof Component)
				return ((Component)_self).getSpaceOwner();
			if (_self instanceof Page)
				return (Page)_self;
			return null;
		}
		if ("spaceScope".equals(name)) {
			if (_self instanceof Component)
				return ((Component)_self).getAttributes(Component.SPACE_SCOPE);
			if (_self instanceof Page)
				return ((Page)_self).getAttributes();
			return Collections.EMPTY_MAP;
		}

		if (_self instanceof Component) {
			final Component comp = (Component)_self;

			//We have to look getZScriptVariable first and then namespace
			//so it is in the same order of interpreter
			final Page page = comp.getPage();
			if (page != null) {
				final Object o =
					page.getZScriptVariable(comp, name);
				if (o != null)
					return o;
			}

			Object o = _exec.getAttribute(name);
			if (o != null || _exec.hasAttribute(name))
				return o;

			o = comp.getAttributeOrFellow(name, true);
			if (o != null)
				return o;
		} else {
			Page page;
			if (_self instanceof Page) {
				page = (Page)_self;
			} else {
				page = ((ExecutionCtrl)_exec).getCurrentPage();
			}

			if (page != null) {
				Object o = page.getZScriptVariable(name);
				if (o != null)
					return o;

				o = _exec.getAttribute(name);
				if (o != null || _exec.hasAttribute(name))
					return o;

				o = page.getAttributeOrFellow(name, true);
				if (o != null)
					return o;
			} else {
				Object o = _exec.getAttribute(name, true);
				if (o != null || _exec.hasAttribute(name, true))
					return o;
			}
		}

		return _parent != null ? _parent.resolveVariable(name): null;
	}

	//Object//
	public String toString() {
		return "[ExecutionResolver: " + _self + ']';
	}
}
