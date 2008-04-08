/* AbstractExecution.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun  6 12:18:25     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;
import java.io.Reader;
import java.io.IOException;

import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;

import org.zkoss.lang.Classes;
import org.zkoss.idom.Document;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.el.ELContext;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.Visualizer;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.au.AuResponse;

/**
 * A skeletal implementation of {@link Execution}.
 *
 * @author tomyeh
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
	/** The sequence ID of the current request. */
	private String _reqId;
	/** Whether onPiggyback is checked for this execution. */
	private boolean _piggybacked;

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
	public final boolean isAsyncUpdate(Page page) {
		return _creating == null || (page != null && _creating != page);
	}
	public Desktop getDesktop() {
		return _desktop;
	}
	public Object evaluate(Component comp, String expr, Class expectedType) {
		return evaluate0(comp, expr, expectedType,
			comp != null ? comp.getPage(): null);
	}
	public Object evaluate(Page page, String expr, Class expectedType) {
		return evaluate0(page, expr, expectedType, page);
	}
	private Object evaluate0(Object self, String expr,
	Class expectedType, Page page) {
		if (expr == null || expr.length() == 0 || expr.indexOf("${") < 0) {
			if (expectedType == Object.class || expectedType == String.class)
				return expr;
			return Classes.coerce(expectedType, expr);
		}

		if (page == null) page = _curpage;
		_exresolv.setSelf(self);
		return getELContext().getExpressionEvaluator().evaluate(
			expr, expectedType, _exresolv,
			page != null ? page.getFunctionMapper(): null);
	}

	public void postEvent(Event evt) {
		if (evt == null)
			throw new IllegalArgumentException("null");
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
	public PageDefinition getCurrentPageDefinition() {
		return _curpgdef;
	}
	public void setCurrentPageDefinition(PageDefinition pgdef) {
		_curpgdef = pgdef;
	}

	public Event getNextEvent() {
		if (_events != null && !_events.isEmpty())
			return (Event)_events.remove(0);

		if (!_piggybacked) { //handle piggyback only once
			for (Iterator it = _desktop.getPages().iterator(); it.hasNext();) {
				final Page p = (Page)it.next();
				if (isAsyncUpdate(p)) { //ignore new created pages
					for (Iterator e = p.getRoots().iterator(); e.hasNext();) {
						final Component c = (Component)e.next();
						if (Events.isListened(c, Events.ON_PIGGYBACK, false)) //asap+deferrable
							postEvent(new Event(Events.ON_PIGGYBACK, c));
					}
				}
			}
			_piggybacked = true;
		}
		return _events != null && !_events.isEmpty() ? 
			(Event)_events.remove(0): null;
	}

	public boolean isActivated() {
		return _ei != null;
	}
	public void onActivate() {
	}
	public void onDeactivate() {
	}

	public boolean isRecovering() {
		return _ei != null && _ei.isRecovering();
	}

	public void setVisualizer(Visualizer ei) {
		_ei = ei;
	}
	public Visualizer getVisualizer() {
		return _ei;
	}

	public String toAbsoluteURI(String uri, boolean skipInclude) {
		if (uri != null && uri.length() > 0) {
			final char cc = uri.charAt(0);
			if (cc != '/' && cc != '~' && !(skipInclude && isIncluded())
			&& !Servlets.isUniversalURL(uri)) {
				final String dir = getDesktop().getCurrentDirectory();
				if (dir != null)
					return dir + uri;
			}
		}
		return uri;
		//we ignore _creating, because Servlet's include cannot handle
		//related URI correctly (even though it is by the layout servlet)
	}

	private final UiEngine getUiEngine() {
		return ((WebAppCtrl)_desktop.getWebApp()).getUiEngine();
	}

	public Component createComponents(String uri, Component parent,
	Map arg) {
		final Component[] cs = getUiEngine().createComponents(
			this, getPageDefinition(uri), _curpage, parent, arg);
		return cs.length > 0 ? cs[0]: null;
	}
	public Component createComponents(PageDefinition pagedef,
	Component parent, Map arg) {
		if (pagedef == null)
			throw new IllegalArgumentException("pagedef cannot be null");
		final Component[] cs = getUiEngine().createComponents(
			this, pagedef, _curpage, parent, arg);
		return cs.length > 0 ? cs[0]: null;
	}
	public Component createComponentsDirectly(String content, String ext,
	Component parent, Map arg) {
		final Component[] cs = getUiEngine().createComponents(
			this, getPageDefinitionDirectly(content, ext),
			_curpage, parent, arg);
		return cs.length > 0 ? cs[0]: null;
	}
	public Component createComponentsDirectly(Document content, String ext,
	Component parent, Map arg) {
		final Component[] cs = getUiEngine().createComponents(
			this, getPageDefinitionDirectly(content, ext),
			_curpage, parent, arg);
		return cs.length > 0 ? cs[0]: null;
	}
	public Component createComponentsDirectly(Reader reader, String ext,
	Component parent, Map arg) throws IOException {
		final Component[] cs = getUiEngine().createComponents(
			this, getPageDefinitionDirectly(reader, ext),
			_curpage, parent, arg);
		return cs.length > 0 ? cs[0]: null;
	}

	public Component[] createComponents(String uri, Map arg) {
		return getUiEngine().createComponents(
			this, getPageDefinition(uri), null, null, arg);
	}
	public Component[] createComponents(PageDefinition pagedef, Map arg) {
		if (pagedef == null)
			throw new IllegalArgumentException("pagedef cannot be null");
		return getUiEngine().createComponents(this, pagedef, null, null, arg);
	}
	public Component[] createComponentsDirectly(String content, String ext,
	Map arg) {
		return getUiEngine().createComponents(
			this, getPageDefinitionDirectly(content, ext),
			null, null, arg);
	}
	public Component[] createComponentsDirectly(Document content, String ext,
	Map arg) {
		return getUiEngine().createComponents(
			this, getPageDefinitionDirectly(content, ext),
			null, null, arg);
	}
	public Component[] createComponentsDirectly(Reader reader, String ext,
	Map arg) throws IOException {
		return getUiEngine().createComponents(
			this, getPageDefinitionDirectly(reader, ext),
			null, null, arg);
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

	public void setRequestId(String reqId) {
		_reqId = reqId;
	}
	public String getRequestId() {
		return _reqId;
	}

	//Object//
	public String toString() {
		return "[Exec"+System.identityHashCode(this)+": "+_desktop+']';
	}
}
