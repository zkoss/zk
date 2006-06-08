/* AbstractExecution.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun  6 12:18:25     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.io.Reader;
import java.io.IOException;

import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.el.ELException;

import com.potix.idom.Document;
import com.potix.web.servlet.Servlets;
import com.potix.web.el.ELContexts;
import com.potix.web.el.ELContext;

import com.potix.zk.ui.Execution;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.event.Event;
import com.potix.zk.ui.metainfo.PageDefinition;
import com.potix.zk.ui.metainfo.ComponentDefinition;
import com.potix.zk.ui.metainfo.InstanceDefinition;
import com.potix.zk.ui.sys.WebAppCtrl;
import com.potix.zk.ui.sys.ExecutionCtrl;
import com.potix.zk.ui.sys.Visualizer;
import com.potix.zk.ui.sys.UiEngine;
import com.potix.zk.au.AuResponse;

/**
 * A skeletal implementation of {@link Execution}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
abstract public class AbstractExecution implements Execution, ExecutionCtrl {
	private final Desktop _desktop;
	private Page _curpage;
	private PageDefinition _curpgdef;
	private Visualizer _ei;
	private List _events;
	/** A stack of args being pushed by {@link #pushArg}. */
	private List _args;
	//private Event _evtInProc;
	/** Which page is being created, or null if all in update mode. */
	private final Page _creating;
	private final ExecutionResolver _exresolv;

	/** Constructs an execution.
	 * @param creating which page is being creating for this execution, or
	 * null if none is being created.
	 * {@link #isAsyncUpdate} returns based on this.
	 */
	protected AbstractExecution(Desktop desktop, Page creating, VariableResolver resolv) {
		_desktop = desktop;
		_creating = creating;
		_exresolv = new ExecutionResolver(this, resolv);
	}
	/** Returns the JSP context for this execution.
	 * A derive must implement this method.
	 */
	abstract protected ELContext getELContext();

	//-- Execution --//
	public boolean isAsyncUpdate(Page page) {
		return page != null && (_creating == null || _creating != page);
	}
	public Desktop getDesktop() {
		return _desktop;
	}
	public Object evaluate(Component comp, String expr,
	Class expectedType) {
		PageDefinition pagedef = null;
		final ComponentDefinition compdef = comp.getDefinition();
		if (compdef instanceof InstanceDefinition)
			pagedef = ((InstanceDefinition)compdef).getPageDefinition();
		return evaluate0(pagedef, comp, expr, expectedType);
	}
	public Object evaluate(PageDefinition pagedef, Page page, String expr,
	Class expectedType) {
		return evaluate0(
			pagedef != null ? pagedef: page != null ? page.getDefinition(): null,
			page, expr, expectedType);
	}
	private Object evaluate0(PageDefinition pagedef, Object self, String expr,
	Class expectedType) {
		if (expr == null || expr.length() == 0 || expr.indexOf("${") < 0)
			return expr;

		if (pagedef == null && _curpage != null)
			pagedef = _curpage.getDefinition();
		try {
			_exresolv.setSelf(self);
			return getELContext().getExpressionEvaluator().evaluate(
				expr, expectedType, _exresolv,
				pagedef != null ? pagedef.getFunctionMapper(): null);
		} catch (ELException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}

	public void postEvent(Event evt) {
		if (evt == null)
			throw new NullPointerException();
		if (_events == null)
			_events = new LinkedList();
		_events.add(evt);
	}

	public VariableResolver getVariableResolver() {
		return _exresolv;
	}

	//-- ExecutionCtrl --//
	public final Page getCurrentPage() {
		return _curpage;
	}
	public final void setCurrentPage(Page curpage) {
		if (_curpage != null && curpage != null && _curpage != curpage
		&& _curpage.getDesktop() != curpage.getDesktop())
			throw new IllegalStateException("Change current page to another desktop? "+curpage);
		_curpage = curpage;
	}
	public PageDefinition getCurrentPageDefinition(boolean fallback) {
		return !fallback || _curpgdef != null ? _curpgdef:
			_curpage != null ? _curpage.getDefinition(): null;
	}
	public void setCurrentPageDefinition(PageDefinition pgdef) {
		_curpgdef = pgdef;
	}

	public Event getNextEvent() {
		return _events != null && !_events.isEmpty() ?
			(Event)_events.remove(0): null;
	}

	public boolean isActivated() {
		return _ei != null;
	}
	public void onActivate() {
		ELContexts.push(getELContext());
	}
	public void onDeactivate() {
		ELContexts.pop();
	}

	public void setVisualizer(Visualizer ei) {
		_ei = ei;
	}
	public Visualizer getVisualizer() {
		return _ei;
	}

	public String toAbsoluteURI(String uri) {
		if (uri == null) return null;

		if (Servlets.isUniversalURL(uri)) return uri;
		final char cc = uri.length() > 0 ? uri.charAt(0): (char)0;
		return cc == '/' || cc == '~' || isIncluded() ? uri:
				getDesktop().getCurrentDirectory() + uri;
		//we ignore _creating, because Servlet's include cannot handle
		//related URI correctly (even though it is by the layout servlet)
	}

	private final UiEngine getUiEngine() {
		return ((WebAppCtrl)_desktop.getWebApp()).getUiEngine();
	}

	public Component createComponents(String uri, Component parent,
	Map params) {
		//if (log.debugable()) log.debug("Creating from "+uri);
		return getUiEngine().createComponents(
			this, getPageDefinition(uri), _curpage, parent, params);
	}
	public Component createComponents(PageDefinition pagedef,
	Component parent, Map params) {
		if (pagedef == null)
			throw new IllegalArgumentException("pagedef cannot be null");
		return getUiEngine().createComponents(
			this, pagedef, _curpage, parent, params);
	}
	public Component createComponentsDirectly(String content, String ext,
	Component parent, Map params) {
		return getUiEngine().createComponents(
			this, getPageDefinitionDirectly(content, ext), _curpage,
			parent, params);
	}
	public Component createComponentsDirectly(Document content, String ext,
	Component parent, Map params) {
		return getUiEngine().createComponents(
			this, getPageDefinitionDirectly(content, ext), _curpage,
			parent, params);
	}
	public Component createComponentsDirectly(Reader reader, String ext,
	Component parent, Map params) throws IOException {
		return getUiEngine().createComponents(
			this, getPageDefinitionDirectly(reader, ext), _curpage,
			parent, params);
	}

	public void sendRedirect(String uri) {
		getUiEngine().sendRedirect(uri, null);
	}
	public void sendRedirect(String uri, String target) {
		getUiEngine().sendRedirect(uri, target);
	}

	public Map getArg() {
		return _args != null ? (Map)_args.get(0): Collections.EMPTY_MAP;
	}
	public void pushArg(Map arg) {
		if (_args == null)
			_args = new LinkedList();
		_args.add(0, arg);
	}
	public void popArg() {
		if (_args != null) {
			if (_args.size() == 1)
				_args = null;
			else
				_args.remove(0);
		}
	}
	public void addAuResponse(String key, AuResponse response) {
		getUiEngine().addResponse(key, response);
	}

	//Object//
	public String toString() {
		return "[Exec"+System.identityHashCode(this)+": "+_desktop+']';
	}
}
