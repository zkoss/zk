/* AbstractExecution.java

	Purpose:
		
	Description:
		
	History:
		Mon Jun  6 12:18:25     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;
import java.io.Reader;
import java.io.IOException;

import org.zkoss.idom.Document;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.VariableResolverX;
import org.zkoss.xel.XelContext;
import org.zkoss.web.servlet.Servlets;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.Visualizer;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.ExecutionInfo;
import org.zkoss.zk.au.AuResponse;

/**
 * A skeletal implementation of {@link Execution}.
 *
 * @author tomyeh
 */
abstract public class AbstractExecution implements Execution, ExecutionCtrl {
	private static final Log _zklog = Log.lookup("org.zkoss.zk.log");

	private Desktop _desktop;
	private Page _curpage;
	private PageDefinition _curpgdef;
	/** A list of EventInfo. */
	private final List<EventInfo> _evtInfos = new LinkedList<EventInfo>();
	/** A stack of args being pushed by {@link #pushArg}. */
	private List<Map<?, ?>> _args;
	//private Event _evtInProc;
	/** Which page is being created, or null if all in update mode. */
	private final Page _creating;
	/** The sequence ID of the current request. */
	private String _reqId;
	/** A collection of the AU responses that shall be generated to client */
	private Collection<AuResponse> _resps;
	/** The information of the event being served, or null if not under event processing. */
	private ExecutionInfo _execinf;
	private List<VariableResolver> _resolvers;

	/** Constructs an execution.
	 * @param creating which page is being creating for this execution, or
	 * null if none is being created.
	 * {@link #isAsyncUpdate} returns based on this.
	 */
	protected AbstractExecution(Desktop desktop, Page creating) {
		_desktop = desktop; //it is null if it is created by WebManager.newDesktop
		_curpage = _creating = creating;
		if (_curpage == null)
			_curpage = getPage(desktop);
	}
	private static Page getPage(Desktop desktop) {
		return desktop != null ? desktop.getFirstPage(): null;
	}

	//-- Execution --//
	public final boolean isAsyncUpdate(Page page) {
		if (page != null)
			return _creating != page;
		final Visualizer uv;
		return (uv = getVisualizer()) != null && uv.isEverAsyncUpdate();
	}
	public Desktop getDesktop() {
		return _desktop;
	}
	public Session getSession() {
		return _desktop != null ? _desktop.getSession(): Sessions.getCurrent();
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

		for (ListIterator<EventInfo> it = _evtInfos.listIterator(_evtInfos.size());;) {
			EventInfo ei = it.hasPrevious() ? it.previous(): null;
			if (ei == null || ei.priority >= priority) {
				if (ei != null) it.next();
				it.add(new EventInfo(priority, evt));
				break;
			}
		}	
	}
	public void postEvent(int priority, Component realTarget, Event evt) {
		postEvent(priority,
			realTarget != evt.getTarget() ? new ProxyEvent(realTarget, evt): evt);
	}

	//-- ExecutionCtrl --//
	public Object getAttribute(String name, boolean recurse) {
		Object val = getAttribute(name);
		Desktop desktop;
		return val != null || !recurse || (desktop=getDesktop()) == null ?
			val: desktop.getAttribute(name, true);
	}
	public boolean hasAttribute(String name, boolean recurse) {
		Desktop desktop;
		return hasAttribute(name)
		|| (recurse && (desktop=getDesktop()) != null && desktop.hasAttribute(name, true));
	}
	public Object setAttribute(String name, Object value, boolean recurse) {
		if (recurse && !hasAttribute(name)) {
			Desktop desktop = getDesktop();
			if (desktop != null) {
				if (desktop.hasAttribute(name, true))
					return desktop.setAttribute(name, value, true);
			}
		}
		return setAttribute(name, value);
	}
	public Object removeAttribute(String name, boolean recurse) {
		if (recurse && !hasAttribute(name)) {
			Desktop desktop = getDesktop();
			if (desktop != null) {
				if (desktop.hasAttribute(name, true))
					return desktop.removeAttribute(name, true);
			}
			return null;
		}
		return removeAttribute(name);
	}

	public final Page getCurrentPage() {
		if (_curpage == null)
			_curpage = getPage(_desktop);
		return _curpage;
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
			return _evtInfos.remove(0).event;

		// ZK-770: EventQueue has extra delay if scope is SESSION
		((DesktopCtrl)_desktop).onPiggyback();

		if (!_evtInfos.isEmpty())
			return _evtInfos.remove(0).event;
		return null;
	}

	public boolean isActivated() {
		return getVisualizer() != null;
	}
	public void onActivate() {
	}
	public void onDeactivate() {
	}

	public boolean isRecovering() {
		Visualizer uv = getVisualizer();
		return uv != null && uv.isRecovering();
	}

	public Visualizer getVisualizer() {
		return _desktop != null ? ((DesktopCtrl)_desktop).getVisualizer(): null;
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
	Map<?, ?> arg) {
		return createComponents0(uri, parent, null, null, arg);
	}
	public Component createComponents(String uri, Component parent,
	Component insertBefore, VariableResolver resolver) {
		return createComponents0(uri, parent, insertBefore, resolver, null);
	}
	private Component createComponents0(String uri, Component parent,
	Component insertBefore, VariableResolver resolver, Map<?, ?> arg) {
		final Component[] cs = getUiEngine().createComponents(
			this, getPageDefinition(uri), getCurrentPage(), parent, insertBefore, resolver, arg);
		return cs.length > 0 ? cs[0]: null;
	}

	public Component createComponents(PageDefinition pagedef,
	Component parent, Map<?, ?> arg) {
		return createComponents0(pagedef, parent, null, null, arg);
	}
	public Component createComponents(PageDefinition pagedef,
	Component parent, Component insertBefore, VariableResolver resolver) {
		return createComponents0(pagedef, parent, insertBefore, resolver, null);
	}
	private Component createComponents0(PageDefinition pagedef,
	Component parent, Component insertBefore, VariableResolver resolver, Map<?, ?> arg) {
		if (pagedef == null)
			throw new IllegalArgumentException("pagedef cannot be null");
		final Component[] cs = getUiEngine().createComponents(
			this, pagedef, getCurrentPage(), parent, insertBefore, resolver, arg);
		return cs.length > 0 ? cs[0]: null;
	}

	public Component createComponentsDirectly(String content, String ext,
	Component parent, Map<?, ?> arg) {
		return createComponentsDirectly0(content, ext, parent, null, null, arg);
	}
	public Component createComponentsDirectly(String content, String ext,
	Component parent, Component insertBefore, VariableResolver resolver) {
		return createComponentsDirectly0(content, ext, parent, insertBefore, resolver, null);
	}
	private Component createComponentsDirectly0(String content, String ext,
	Component parent, Component insertBefore, VariableResolver resolver, Map<?, ?> arg) {
		final Component[] cs = getUiEngine().createComponents(
			this, getPageDefinitionDirectly(content, ext),
			getCurrentPage(), parent, insertBefore, resolver, arg);
		return cs.length > 0 ? cs[0]: null;
	}

	public Component createComponentsDirectly(Document content, String ext,
	Component parent, Map<?, ?> arg) {
		return createComponentsDirectly0(content, ext, parent, null, null, arg);
	}
	public Component createComponentsDirectly(Document content, String ext,
	Component parent, Component insertBefore, VariableResolver resolver) {
		return createComponentsDirectly0(content, ext, parent, insertBefore, resolver, null);
	}
	private Component createComponentsDirectly0(Document content, String ext,
	Component parent, Component insertBefore, VariableResolver resolver, Map<?, ?> arg) {
		final Component[] cs = getUiEngine().createComponents(
			this, getPageDefinitionDirectly(content, ext),
			getCurrentPage(), parent, insertBefore, resolver, arg);
		return cs.length > 0 ? cs[0]: null;
	}

	public Component createComponentsDirectly(Reader reader, String ext,
	Component parent, Map<?, ?> arg) throws IOException {
		return createComponentsDirectly0(reader, ext, parent, null, null, arg);
	}
	public Component createComponentsDirectly(Reader reader, String ext,
	Component parent, Component insertBefore, VariableResolver resolver)
	throws IOException {
		return createComponentsDirectly0(reader, ext, parent, insertBefore, resolver, null);
	}
	private Component createComponentsDirectly0(Reader reader, String ext,
	Component parent, Component insertBefore, VariableResolver resolver, Map<?, ?> arg)
	throws IOException {
		final Component[] cs = getUiEngine().createComponents(
			this, getPageDefinitionDirectly(reader, ext),
			getCurrentPage(), parent, insertBefore, resolver, arg);
		return cs.length > 0 ? cs[0]: null;
	}

	public Component[] createComponents(String uri, Map<?, ?> arg) {
		return getUiEngine().createComponents(
			this, getPageDefinition(uri), null, null, null, null, arg);
	}
	public Component[] createComponents(PageDefinition pagedef, Map<?, ?> arg) {
		if (pagedef == null)
			throw new IllegalArgumentException("pagedef cannot be null");
		return getUiEngine().createComponents(this, pagedef, null, null, null, null, arg);
	}
	public Component[] createComponentsDirectly(String content, String ext,
	Map<?, ?> arg) {
		return getUiEngine().createComponents(
			this, getPageDefinitionDirectly(content, ext),
			null, null, null, null, arg);
	}
	public Component[] createComponentsDirectly(Document content, String ext,
	Map<?, ?> arg) {
		return getUiEngine().createComponents(
			this, getPageDefinitionDirectly(content, ext),
			null, null, null, null, arg);
	}
	public Component[] createComponentsDirectly(Reader reader, String ext,
	Map<?, ?> arg) throws IOException {
		return getUiEngine().createComponents(
			this, getPageDefinitionDirectly(reader, ext),
			null, null, null, null, arg);
	}

	public void sendRedirect(String uri) {
		getUiEngine().sendRedirect(uri, null);
	}
	public void sendRedirect(String uri, String target) {
		getUiEngine().sendRedirect(uri, target);
	}

	public Map<?, ?> getArg() {
		if (_args != null)
			return _args.get(0);
		return Collections.emptyMap();
	}
	public void pushArg(Map<?, ?> arg) {
		if (_args == null)
			_args = new LinkedList<Map<?, ?>>();
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
	public void addAuResponse(AuResponse response) {
		getUiEngine().addResponse(response);
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

	public Collection<AuResponse> getResponses() {
		return _resps;
	}
	public void setResponses(Collection<AuResponse> responses) {
		_resps = responses;
	}

	public ExecutionInfo getExecutionInfo() {
		return _execinf;
	}
	public void setExecutionInfo(ExecutionInfo execinf) {
		_execinf = execinf;
	}

	public boolean addVariableResolver(VariableResolver resolver) {
		if (resolver == null)
			throw new IllegalArgumentException("null");

		if (_resolvers == null)
			_resolvers = new LinkedList<VariableResolver>();
		else if (_resolvers.contains(resolver))
			return false;

		_resolvers.add(0, resolver); //FILO order
		return true;
	}
	public boolean removeVariableResolver(VariableResolver resolver) {
		return _resolvers != null && _resolvers.remove(resolver);
	}
	public boolean hasVariableResolver(VariableResolver resolver) {
		return _resolvers != null && _resolvers.contains(resolver);
	}
	//@Override ExecutionCtrl
	public Object getExtraXelVariable(String name) {
		return getExtraXelVariable(null, null, name);
	}
	//@Override ExecutionCtrl
	public Object getExtraXelVariable(XelContext ctx, Object base, Object name) {
		//Note this method searches only _resolvers
		if (_resolvers != null) {
			for (Iterator it = CollectionsX.comodifiableIterator(_resolvers);
			it.hasNext();) {
				final VariableResolver vr = (VariableResolver)it.next();
				final Object o =
					vr instanceof VariableResolverX ?
						((VariableResolverX)vr).resolveVariable(ctx, base, name):
					base == null && name != null ?
						vr.resolveVariable(name.toString()): null;
				if (o != null)
					return o;
			}
		}
		return null;
	}

	@Override
	public void log(String msg) {
		if (_desktop != null)
			_desktop.getWebApp().log(msg);
		else
			_zklog.info(msg);
	}
	@Override
	public void log(String msg, Throwable ex) {
		if (_desktop != null)
			_desktop.getWebApp().log(msg, ex);
		else
			_zklog.error(msg, ex);
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
