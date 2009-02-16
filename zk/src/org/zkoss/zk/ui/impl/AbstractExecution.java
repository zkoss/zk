/* AbstractExecution.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun  6 12:18:25     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.Collections;
import java.util.ListIterator;
import java.io.Reader;
import java.io.IOException;

import org.zkoss.idom.Document;
import org.zkoss.web.servlet.Servlets;

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
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.Visualizer;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.au.AuResponse;

/**
 * A skeletal implementation of {@link Execution}.
 *
 * @author tomyeh
 */
abstract public class AbstractExecution implements Execution, ExecutionCtrl {
	private Desktop _desktop;
	private Page _curpage;
	private PageDefinition _curpgdef;
	private Visualizer _ei;
	/** A list of EventInfo. */
	private final List _evtInfos = new LinkedList();
	/** A stack of args being pushed by {@link #pushArg}. */
	private List _args;
	//private Event _evtInProc;
	/** Which page is being created, or null if all in update mode. */
	private final Page _creating;
	/** The sequence ID of the current request. */
	private String _reqId;
	/** Whether onPiggyback is checked for this execution. */
	private boolean _piggybacked;

	/** Constructs an execution.
	 * @param creating which page is being creating for this execution, or
	 * null if none is being created.
	 * {@link #isAsyncUpdate} returns based on this.
	 */
	protected AbstractExecution(Desktop desktop, Page creating) {
		_desktop = desktop; //it is null if it is created by WebManager.newDesktop
		_creating = creating;

		if (desktop != null) {
			final Iterator it = desktop.getPages().iterator();
			if (it.hasNext()) _curpage = (Page)it.next();
		}
	}

	//-- Execution --//
	public final boolean isAsyncUpdate(Page page) {
		return _creating == null || (page != null && _creating != page);
	}
	public Desktop getDesktop() {
		return _desktop;
	}

	public void postEvent(Event evt) {
		postEvent(0, evt);
	}
	public void postEvent(int priority, Event evt) {
		if (evt == null)
			throw new IllegalArgumentException("null");

		evt = ((DesktopCtrl)_desktop).beforePostEvent(evt);
		if (evt == null)
			return; //done (ignored)

		for (ListIterator it = _evtInfos.listIterator(_evtInfos.size());;) {
			EventInfo ei = it.hasPrevious() ? (EventInfo)it.previous(): null;
			if (ei == null || ei.priority >= priority) {
				if (ei != null) it.next();
				it.add(new EventInfo(priority, evt));
				break;
			}
		}	
	}

	//-- ExecutionCtrl --//
	public final Page getCurrentPage() {
		if (_curpage != null)
			return _curpage;
		final Iterator it = _desktop.getPages().iterator();
		return it.hasNext() ? (Page)it.next(): null;
	}
	public final void setCurrentPage(Page curpage) {
		if (_curpage != null && curpage != null && _curpage != curpage) {
			Desktop _curdt = _curpage.getDesktop(),
				curdt = curpage.getDesktop();
			if (_curdt != null && curdt != null && _curdt != curdt)
				throw new IllegalStateException("Change current page to another desktop? "+curpage);
		}
		_curpage = curpage;
	}
	public PageDefinition getCurrentPageDefinition() {
		return _curpgdef;
	}
	public void setCurrentPageDefinition(PageDefinition pgdef) {
		_curpgdef = pgdef;
	}

	public Event getNextEvent() {
		if (!_evtInfos.isEmpty())
			return ((EventInfo)_evtInfos.remove(0)).event;

		if (!_piggybacked) { //handle piggyback only once
			((DesktopCtrl)_desktop).onPiggyback();
			_piggybacked = true;

			if (!_evtInfos.isEmpty())
				return ((EventInfo)_evtInfos.remove(0)).event;
		}
		return null;
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
			this, getPageDefinition(uri), getCurrentPage(), parent, arg);
		return cs.length > 0 ? cs[0]: null;
	}
	public Component createComponents(PageDefinition pagedef,
	Component parent, Map arg) {
		if (pagedef == null)
			throw new IllegalArgumentException("pagedef cannot be null");
		final Component[] cs = getUiEngine().createComponents(
			this, pagedef, getCurrentPage(), parent, arg);
		return cs.length > 0 ? cs[0]: null;
	}
	public Component createComponentsDirectly(String content, String ext,
	Component parent, Map arg) {
		final Component[] cs = getUiEngine().createComponents(
			this, getPageDefinitionDirectly(content, ext),
			getCurrentPage(), parent, arg);
		return cs.length > 0 ? cs[0]: null;
	}
	public Component createComponentsDirectly(Document content, String ext,
	Component parent, Map arg) {
		final Component[] cs = getUiEngine().createComponents(
			this, getPageDefinitionDirectly(content, ext),
			getCurrentPage(), parent, arg);
		return cs.length > 0 ? cs[0]: null;
	}
	public Component createComponentsDirectly(Reader reader, String ext,
	Component parent, Map arg) throws IOException {
		final Component[] cs = getUiEngine().createComponents(
			this, getPageDefinitionDirectly(reader, ext),
			getCurrentPage(), parent, arg);
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
	public void setDesktop(Desktop desktop) {
		if (desktop == null) throw new IllegalArgumentException("null");
		if (_desktop != null && _desktop != desktop)
			throw new IllegalStateException("assign diff desktop");

		_desktop = desktop;
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

	private static class EventInfo {
		private final int priority;
		private final Event event;
		private EventInfo(int priority, Event event) {
			this.priority = priority;
			this.event = event;
		}
		public String toString() {
			return "[" + this.priority + ": " + this.event.toString() + "]";
		}
	}
}
