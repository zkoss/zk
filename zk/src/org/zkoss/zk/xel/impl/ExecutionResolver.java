/* ExecutionResolver.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 24 12:22:23     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.xel.impl;

import java.util.Collections;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.VariableResolverX;
import org.zkoss.xel.XelException;
import org.zkoss.xel.util.Evaluators;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.HtmlShadowElement;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;

/**
 * A variable resolver that is based on the specified execution.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ExecutionResolver implements VariableResolverX {
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
		return resolveVariable(null, null, name);
	}
	//-- VariableResolverX --//
	public Object resolveVariable(XelContext ctx, Object base, Object onm) {
		if (base != null) {
			Object o = ((ExecutionCtrl)_exec).getExtraXelVariable(ctx, base, onm);
			if (o != null)
				return o;
			final Page page = ((ExecutionCtrl)_exec).getCurrentPage();
			return page != null ? page.getXelVariable(ctx, base, onm, true): null;
		}

		if (onm == null)
			return null;

		final String name = onm.toString();
		if (name == null || name.length() == 0) //just in case
			return null;

		//Note: we have to access keyword first (rather than component's ns)
		//since 1) BeanShell interpreter will store back variables
		//and page.getZScriptVariable will return the old value
		//2) ZK 5, getAttributeOrFellow doesn't look for variable resolvers and implicit objects 
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
				return Components.getCurrentPage((Component)_self);
			if (_self instanceof Page)
				return _self;
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
				return _self;
			return null;
		}
		if ("spaceScope".equals(name)) {
			if (_self instanceof Component)
				return ((Component)_self).getAttributes(Component.SPACE_SCOPE);
			if (_self instanceof Page)
				return ((Page)_self).getAttributes();
			return Collections.EMPTY_MAP;
		}
		if ("param".equals(name) || "paramValues".equals(name))
			return Evaluators.resolveVariable(_parent, name);
			//Bug 3131983: cannot go through getZScriptVariable

		if (_self instanceof Component) {
			final Component comp = (Component)_self;

			//We have to look getZScriptVariable first and then namespace
			//so it is in the same order of interpreter
			final Page page = Components.getCurrentPage(comp);
			if (page != null) {
				final Object o = page.getZScriptVariable(comp, name);
				if (o != null)
					return o;
			}

			Object o = _exec.getAttribute(name);
			if (o != null/* || _exec.hasAttribute(name)*/) //ServletRequest not support hasAttribute
				return o;

			o = comp.getAttributeOrFellow(name, true);
			if (o != null)
				return o;

			o = ((ExecutionCtrl)_exec).getExtraXelVariable(name);
			if (o != null)
				return o;
			

			o = comp.getShadowVariable(name, true);
			if (o != null)
				return o;

			if (page != null) {
				o = page.getXelVariable(ctx, null, name, true);
				if (o != null)
					return o;
			}
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
				if (o != null/* || _exec.hasAttribute(name)*/) //ServletRequest not support hasAttribute
					return o;

				o = page.getAttributeOrFellow(name, true);
				if (o != null)
					return o;

				o = ((ExecutionCtrl)_exec).getExtraXelVariable(name);
				if (o != null)
					return o;

				o = page.getXelVariable(ctx, null, name, true);
				if (o != null)
					return o;
			} else {
				Object o = _exec.getAttribute(name, true);
				if (o != null/* || _exec.hasAttribute(name, true)*/) //ServletRequest not support hasAttribute
					return o;
			}
		}

		Object o = Evaluators.resolveVariable(_parent, name);
		if (o != null)
			return o;

		//lower priority (i.e., user could override it)
		//Reason: they were introduced later, and have to maintain backward comparibility
		if ("labels".equals(name))
			return Labels.getSegmentedLabels();
		return null;
	}

	//Object//
	public String toString() {
		return "[ExecutionResolver: " + _self + ']';
	}
}
