/* PageImpl.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 18:17:32     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.AbstractMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.lang.Library;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Expectable;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.io.Serializables;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.util.DualFunctionMapper;
import org.zkoss.xel.util.Evaluators;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.AbstractPage;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinitionMap;
import org.zkoss.zk.ui.metainfo.DefinitionNotFoundException;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.PageSerializationListener;
import org.zkoss.zk.ui.util.PageActivationListener;
import org.zkoss.zk.ui.ext.Includer;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.ext.Scopes;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.sys.Attributes;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.PageConfig;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.PageRenderer;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.au.out.AuSetTitle;
import org.zkoss.zk.scripting.*;

/**
 * An implmentation of {@link Page} and {@link PageCtrl}.
 * Refer to them for more details.
 *
 * <p>Note: though {@link PageImpl} is serializable, it is designed
 * to work with Web container to enable the serialization of sessions.
 * It is not suggested to serialize and desrialize it directly since
 * many fields might be lost.
 *
 * <p>On the other hand, it is OK to serialize and deserialize
 * {@link Component}.
 *
 * <p>Implementation Notes:<br>
 * It is not thread-safe because it is protected by the spec:
 * at most one thread can access a page and all its components at the same time.
 *
 * @author tomyeh
 */
public class PageImpl extends AbstractPage implements java.io.Serializable {
	private static final Log log = Log.lookup(PageImpl.class);
    private static final long serialVersionUID = 20091008L;

	/** The component that includes this page, or null if not included. */
	private transient Component _owner;
	/** Used to retore _owner. */
	private transient String _ownerUuid;
	private transient Desktop _desktop;
	private String _id = "", _uuid;
	private String _title = "", _style = "";
	private final String _path;
	private String _zslang;
	/** A list of deferred zscript [Component parent, {@link ZScript}]. */
	private List _zsDeferred;
	/** A map of attributes. */
	private transient SimpleScope _attrs;
	/** A map of event listener: Map(evtnm, List(EventListener)). */
	private transient Map _listeners;
	/** The reason to store it is PageDefinition is not serializable. */
	private FunctionMapper _mapper;
	/** The reason to store it is PageDefinition is not serializable. */
	private ComponentDefinitionMap _compdefs;
	/** The reason to store it is PageDefinition is not serializable. */
	private transient LanguageDefinition _langdef;
	/** The header tags. */
	private String _hdbfr = "", _hdaft = "";
	/** The response headers. */
	private Collection _hdres;
	/** The root attributes. */
	private String _rootAttrs = "";
	private String _contentType, _docType, _firstLine;
	private Boolean _cacheable;
	private Boolean _autoTimeout;
	/** The expression factory (ExpressionFactory).*/
	private Class _expfcls;
	/** A map of interpreters Map(String zslang, Interpreter ip). */
	private transient Map _ips;
	private transient NS _ns;
	/** A list of {@link VariableResolver}. */
	private transient List _resolvers;
	private boolean _complete;

	/** Constructs a page by giving the page definition.
	 *
	 * <p>Note: when a page is constructed, it doesn't belong to a desktop
	 * yet. Caller has to invoke {@link #init} to complete
	 * the creation of a page.
	 * Why two phase? Contructor could be called before execution
	 * is activated, but {@link #init} must be called in an execution.
	 *
	 * <p>Also note that {@link #getId} and {@link #getTitle}
	 * are not ready until {@link #init} is called.
	 *
	 * @param pgdef the page definition (never null).
	 */
	public PageImpl(PageDefinition pgdef) {
		this(pgdef.getLanguageDefinition(), pgdef.getComponentDefinitionMap(),
			pgdef.getRequestPath(), pgdef.getZScriptLanguage());
	}
	/** Constructs a page without page definition and richlet.
	 *
	 * @param langdef the language definition (never null)
	 * @param compdefs the component definition map.
	 * If null, an empty map is assumed.
	 * @param path the request path. If null, empty is assumed.
	 * @param zslang the zscript language. If null, "Java" is assumed.
	 */
	public PageImpl(LanguageDefinition langdef,
	ComponentDefinitionMap compdefs, String path, String zslang) {
		init();

		_langdef = langdef;
		_compdefs = compdefs != null ? compdefs:
			new ComponentDefinitionMap(
				_langdef.getComponentDefinitionMap().isCaseInsensitive());
		_path = path != null ? path: "";
		_zslang = zslang != null ? zslang: "Java";
	}

	/** Constructs a page by specifying a richlet.
	 *
	 * <p>Note: when a page is constructed, it doesn't belong to a desktop
	 * yet. Caller has to invoke {@link #init} to complete
	 * the creation of a page.
	 *
	 * <p>Also note that {@link #getId} and {@link #getTitle}
	 * are not ready until {@link #init} is called.
	 *
	 * @param richlet the richlet to serve this page.
	 * @param path the request path, or null if not available
	 */
	public PageImpl(Richlet richlet, String path) {
		init();

		_langdef = richlet.getLanguageDefinition();
		_compdefs = new ComponentDefinitionMap(
			_langdef.getComponentDefinitionMap().isCaseInsensitive());
		_path = path != null ? path: "";
		_zslang = "Java";
	}
	/** Initialized the page when contructed or deserialized.
	 */
	protected void init() {
		_ips = new LinkedHashMap(4);
		_ns = new NS(); //backwad compatible
		_attrs = new SimpleScope(this);
	}

	/** Returns the UI engine.
	 */
	private final UiEngine getUiEngine() {
		return ((WebAppCtrl)_desktop.getWebApp()).getUiEngine();
	}

	//-- Page --//
	private final Execution getExecution() {
		return _desktop != null ? _desktop.getExecution():
			Executions.getCurrent();
	}
	public final FunctionMapper getFunctionMapper() {
		return _mapper;
	}
	public void addFunctionMapper(FunctionMapper mapper) {
		_mapper = DualFunctionMapper.combine(mapper, _mapper);
	}

	public String getRequestPath() {
		return _path;
	}
	public final String getId() {
		return _id;
	}
	public final String getUuid() {
		return _uuid;
	}
	public void setId(String id) {
		if (_desktop != null && _desktop.getPages().contains(this))
			throw new UiException("ID cannot be changed after initialized");
		_id = id != null ? id: "";
		//No need to update client since it is allowed only before init(...)
	}
	public String getTitle() {
		return _title;
	}
	public void setTitle(String title) {
		if (title == null) title = "";
		if (!_title.equals(title)) {
			_title = title;
			if (_desktop != null) {
				final Execution exec = getExecution();
				if (_title.length() > 0) {
					_title = (String)exec.evaluate(this, _title, String.class);
					if (_title == null) _title = "";
				}

				if (exec.isAsyncUpdate(this))
					getUiEngine().addResponse(new AuSetTitle(_title));
			}
		}
	}
	public String getStyle() {
		return _style;
	}
	public void setStyle(String style) {
		if (style == null) style = "";
		if (!_style.equals(style)) {
			_style = style;
			if (_desktop != null) {
				final Execution exec = getExecution();
				if (_style.length() > 0) {
					_style = (String)exec.evaluate(this, _style, String.class);
					if (_style == null) _style = "";
				}
				//FUTURE: might support the change of style dynamically
			}
		}
	}

	public Map getAttributes(int scope) {
		switch (scope) {
		case DESKTOP_SCOPE:
			return _desktop != null ?
				_desktop.getAttributes(): Collections.EMPTY_MAP;
		case SESSION_SCOPE:
			return _desktop != null ?
				_desktop.getSession().getAttributes(): Collections.EMPTY_MAP;
		case APPLICATION_SCOPE:
			return _desktop != null ?
				_desktop.getWebApp().getAttributes(): Collections.EMPTY_MAP;
		case PAGE_SCOPE:
			return _attrs.getAttributes();
		case REQUEST_SCOPE:
			final Execution exec = getExecution();
			if (exec != null) return exec.getAttributes();
			//fall thru
		default:
			return Collections.EMPTY_MAP;
		}
	}
	public Object getAttribute(String name, int scope) {
		return getAttributes(scope).get(name);
	}
	public boolean hasAttribute(String name, int scope) {
		return getAttributes(scope).containsKey(name);
	}
	public Object setAttribute(String name, Object value, int scope) {
		final Map attrs = getAttributes(scope);
		if (attrs == Collections.EMPTY_MAP)
			throw new IllegalStateException("This component doesn't belong to any ID space: "+this);
		return attrs.put(name, value);
	}
	public Object removeAttribute(String name, int scope) {
			final Map attrs = getAttributes(scope);
			if (attrs == Collections.EMPTY_MAP)
				throw new IllegalStateException("This component doesn't belong to any ID space: "+this);
		return attrs.remove(name);
	}

	public Map getAttributes() {
		return _attrs.getAttributes();
	}
	public Object getAttribute(String name) {
		return _attrs.getAttribute(name);
	}
	public boolean hasAttribute(String name) {
		return _attrs.hasAttribute(name);
	}
	public Object setAttribute(String name, Object value) {
		return _attrs.setAttribute(name, value);
	}
	public Object removeAttribute(String name) {
		return _attrs.removeAttribute(name);
	}
	public Object getAttribute(String name, boolean recurse) {
		Object val = getAttribute(name);
		return val != null || !recurse || hasAttribute(name) ? val:
			_desktop != null ? _desktop.getAttribute(name, true): null;
	}
	public boolean hasAttribute(String name, boolean recurse) {
		return hasAttribute(name)
			|| (recurse && _desktop != null && _desktop.hasAttribute(name, true));
	}
	public Object setAttribute(String name, Object value, boolean recurse) {
		if (recurse && !hasAttribute(name)) {
			if (_desktop != null) {
				if (_desktop.hasAttribute(name, true))
					return _desktop.setAttribute(name, value, true);
			}
		}
		return setAttribute(name, value);
	}
	public Object removeAttribute(String name, boolean recurse) {
		if (recurse && !hasAttribute(name)) {
			if (_desktop != null) {
				if (_desktop.hasAttribute(name, true))
					return _desktop.removeAttribute(name, true);
			}
			return null;
		}
		return removeAttribute(name);
	}

	public Object getAttributeOrFellow(String name, boolean recurse) {
		Object val = getAttribute(name);
		if (val != null || hasAttribute(name))
			return val;

		val = getFellowIfAny(name);
		if (val != null)
			return val;

		return recurse && _desktop != null ?
			_desktop.getAttribute(name, true): null;
	}
	public boolean hasAttributeOrFellow(String name, boolean recurse) {
		return hasAttribute(name) || hasFellow(name)
			|| (recurse && _desktop != null && _desktop.hasAttribute(name, true));
	}

	public boolean addScopeListener(ScopeListener listener) {
		return _attrs.addScopeListener(listener);
	}
	public boolean removeScopeListener(ScopeListener listener) {
		return _attrs.removeScopeListener(listener);
	}

	public void invalidate() {
		getUiEngine().addInvalidate(this);
	}

	public Class resolveClass(String clsnm) throws ClassNotFoundException {
		try {
			return Classes.forNameByThread(clsnm);
		} catch (ClassNotFoundException ex) {
			for (Iterator it = getLoadedInterpreters().iterator();
			it.hasNext();) {
				final Class c = ((Interpreter)it.next()).getClass(clsnm);
				if (c != null)
					return c;
			}
			throw ex;
		}
	}
	/** @deprecated As of release 5.0.0, replaced with {@link #setAttribute}. */
	public void setVariable(String name, Object val) {
		_ns.setVariable(name, val, true);
	}
	/** @deprecated As of release 5.0.0, replaced with {@link #hasAttribute}. */
	public boolean containsVariable(String name) {
		return _ns.containsVariable(name, true);
	}
	/** @deprecated As of release 5.0.0, replaced with {@link #getAttribute}. */
	public Object getVariable(String name) {
		return _ns.getVariable(name, true);
	}
	/** @deprecated As of release 5.0.0, replaced with {@link #removeAttribute}. */
	public void unsetVariable(String name) {
		_ns.unsetVariable(name, true);
	}
	public Class getZScriptClass(String clsnm) {
		for (Iterator it = getLoadedInterpreters().iterator();
		it.hasNext();) {
			Class cls = ((Interpreter)it.next()).getClass(clsnm);
			if (cls != null)
				return cls;
		}

		try {
			return Classes.forNameByThread(clsnm);
		} catch (ClassNotFoundException ex) {
			return null;
		}
	}
	public Function getZScriptFunction(String name, Class[] argTypes) {
		for (Iterator it = getLoadedInterpreters().iterator();
		it.hasNext();) {
			Function mtd =
				((Interpreter)it.next()).getFunction(name, argTypes);
			if (mtd != null)
				return mtd;
		}
		return null;
	}
	public Function getZScriptFunction(
	Component comp, String name, Class[] argTypes) {
		for (Iterator it = getLoadedInterpreters().iterator();
		it.hasNext();) {
			final Object ip = it.next();
			Function mtd =
				ip instanceof HierachicalAware ?
				((HierachicalAware)ip).getFunction(comp, name, argTypes):
				((Interpreter)ip).getFunction(name, argTypes);
			if (mtd != null)
				return mtd;
		}
		return null;
	}
	/** @deprecated As of release 5.0.0, replaced with
	 * {@link #getZScriptFunction(Component,String,Class[])}.
	 */
	public Function getZScriptFunction(Namespace ns, String name, Class[] argTypes) {
		return getZScriptFunction(ns != null ? ns.getOwner(): null, name, argTypes);
	}

	public Object getZScriptVariable(String name) {
		for (Iterator it = getLoadedInterpreters().iterator();
		it.hasNext();) {
			final Object val = ((Interpreter)it.next()).getVariable(name);
			if (val != null)
				return val;
		}
		return null;
	}
	public Object getZScriptVariable(Component comp, String name) {
		for (Iterator it = getLoadedInterpreters().iterator();
		it.hasNext();) {
			final Object ip = it.next();
			final Object val = ip instanceof HierachicalAware ?
				((HierachicalAware)ip).getVariable(comp, name):
				((Interpreter)ip).getVariable(name);
			if (val != null)
				return val;
		}
		return null;
	}
	/** @deprecated As of release 5.0.0, replaced with
	 * {@link #getZScriptVariable(Component,String)}.
	 */
	public Object getZScriptVariable(Namespace ns, String name) {
		return getZScriptVariable(ns != null ? ns.getOwner(): null, name);
	}

	public Object getXelVariable(String name) {
		return getXelVariable(null, null, name, false);
	}
	public Object getXelVariable(
	XelContext ctx, Object base, Object name, boolean ignoreExec) {
		if (!ignoreExec) {
			final Execution exec = getExecution();
			if (exec != null)
				return Evaluators.resolveVariable(
					ctx, exec.getVariableResolver(), base, name);
					//note: ExecutionResolver will call back this method
		}
		
		if (_resolvers != null) {
			for (Iterator it = _resolvers.iterator(); it.hasNext();) {
				Object o = Evaluators.resolveVariable(
					ctx, (VariableResolver)it.next(), base, name);
				if (o != null)
					return o;
			}
		}
		return null;
	}

	public boolean addVariableResolver(VariableResolver resolver) {
		if (resolver == null)
			throw new IllegalArgumentException("null");

		if (_resolvers == null)
			_resolvers = new LinkedList();
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

	public boolean addEventListener(String evtnm, EventListener listener) {
		if (evtnm == null || listener == null)
			throw new IllegalArgumentException("null");
		if (!Events.isValid(evtnm))
			throw new IllegalArgumentException("Invalid event name: "+evtnm);

		if (_listeners == null)
			_listeners = new HashMap(3);

		List l = (List)_listeners.get(evtnm);
		if (l != null) {
			for (Iterator it = l.iterator(); it.hasNext();) {
				final EventListener li = (EventListener)it.next();
				if (listener.equals(li))
					return false;
			}
		} else {
			_listeners.put(evtnm, l = new LinkedList());
		}
		l.add(listener);
		return true;
	}
	public boolean removeEventListener(String evtnm, EventListener listener) {
		if (evtnm == null || listener == null)
			throw new NullPointerException();

		if (_listeners != null) {
			final List l = (List)_listeners.get(evtnm);
			if (l != null) {
				for (Iterator it = l.iterator(); it.hasNext();) {
					final EventListener li = (EventListener)it.next();
					if (listener.equals(li)) {
						if (l.size() == 1)
							_listeners.remove(evtnm);
						else
							it.remove();
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isComplete() {
		return _complete;
	}
	public void setComplete(boolean complete) {
		_complete = complete;
	}

	//-- PageCtrl --//
	public void preInit() {
		if (_desktop != null)
			throw new IllegalStateException("init twice");

		final Execution exec = Executions.getCurrent();
		_desktop = exec.getDesktop();
		if (_desktop == null)
			throw new IllegalArgumentException("null desktop");
	}
	public void init(PageConfig config) {
		final Execution exec = Executions.getCurrent();

		if (((ExecutionCtrl)exec).isRecovering()) {
			final String uuid = config.getUuid(), id = config.getId();
			if (uuid == null || id == null)
				throw new IllegalArgumentException("both id and uuid are required in recovering");
			_uuid = uuid;
			_id = id;
		} else {
			_uuid = ((DesktopCtrl)_desktop).getNextUuid(this);

			if (_id == null || _id.length() == 0)
				_id = config.getId();
			if (_id != null)
				_id = (String)exec.evaluate(this, _id, String.class);
			if (_id == null) {
				_id = "";
			} else if (_id.length() != 0) {
				final String INVALID = ".&\\%";
				if (Strings.anyOf(_id, INVALID, 0) < _id.length())
					throw new IllegalArgumentException("Invalid page ID: "+_id+". Invalid characters: "+INVALID);
			}
		}

		//Note: the evaluation order was changed since 5.0.2
		((DesktopCtrl)_desktop).addPage(this);
			//add page before evaluate title and others

		String s;
		if (_title.length() == 0) {
			s = config.getTitle();
			if (s != null) setTitle(s);
		}
		if (_style.length() == 0) {
			s = config.getStyle();
			if (s != null) setStyle(s);
		}

		s = config.getHeaders(true);
		if (s != null) _hdbfr = s;
		s = config.getHeaders(false);
		if (s != null) _hdaft = s;
		_hdres = config.getResponseHeaders();
		if (_hdres.isEmpty()) _hdres = null;
	}
	public void destroy() {
		super.destroy();

		for (Iterator it = getLoadedInterpreters().iterator(); it.hasNext();) {
			final Interpreter ip = (Interpreter)it.next();
			try {
				ip.destroy();
			} catch (Throwable ex) {
				log.error("Failed to destroy "+ip, ex);
			}
		}
		_ips.clear();
		_ips = null; //not just clear since it is better to NPE than memory leak

		//theorectically, the following is not necessary, but, to be safe...
		_attrs.getAttributes().clear();
		_desktop = null;
		_owner = null;
		_listeners = null;
		_ns = null;
		_resolvers = null;
	}
	public boolean isAlive() {
		return _ips != null;
	}

	private static final Map REQUEST_ATTRS = new AbstractMap() {
		public Set entrySet() {
			final Execution exec = Executions.getCurrent();
			if (exec == null) return Collections.EMPTY_SET;
			return exec.getAttributes().entrySet();
		}
		public Object put(Object name, Object value) {
			final Execution exec = Executions.getCurrent();
			if (exec == null) throw new IllegalStateException("No execution at all");
			return exec.getAttributes().put(name, value);
		}
		public boolean containsKey(Object name) {
			final Execution exec = Executions.getCurrent();
			return exec != null && exec.getAttributes().containsKey(name);
		}
		public Object get(Object name) {
			final Execution exec = Executions.getCurrent();
			if (exec == null) return null;
			return exec.getAttributes().get(name);
		}
		public Object remove(Object name) {
			final Execution exec = Executions.getCurrent();
			if (exec == null) return null;
			return exec.getAttributes().remove(name);
		}
	};

	public String getHeaders(boolean before) {
		return before ? _hdbfr: _hdaft;
	}
	public String getHeaders() {
		return _hdbfr + _hdaft;
	}
	public Collection getResponseHeaders() {
		return _hdres != null ? _hdres: Collections.EMPTY_LIST;
	}
	public String getRootAttributes() {
		return _rootAttrs;
	}
	public void setRootAttributes(String rootAttrs) {
		_rootAttrs = rootAttrs != null ? rootAttrs: "";
	}
	public String getContentType() {
		return _contentType;
	}
	public void setContentType(String contentType) {
		_contentType = contentType;
	}
	public String getDocType() {
		return _docType;
	}
	public void setDocType(String docType) {
		_docType = docType;
	}
	public String getFirstLine() {
		return _firstLine;
	}
	public void setFirstLine(String firstLine) {
		_firstLine = firstLine;
	}
	public Boolean getCacheable() {
		return _cacheable;
	}
	public void setCacheable(Boolean cacheable) {
		_cacheable = cacheable;
	}
	public Boolean getAutomaticTimeout() {
		return _autoTimeout;
	}
	public void setAutomaticTimeout(Boolean autoTimeout) {
		_autoTimeout = autoTimeout;
	}

	public final Desktop getDesktop() {
		return _desktop;
	}

	public void redraw(Writer out)
	throws IOException {
		String ctl;
		final Execution exec = getExecution();
		final boolean au = exec.isAsyncUpdate(null);
		if (!au && !exec.isIncluded()
		&& ((ctl=ExecutionsCtrl.getPageRedrawControl(exec)) == null
			|| "desktop".equals(ctl))) {
			if (!au && shallIE7Compatible())
				try {
					if (exec.isBrowser("ie8")
					&& !exec.containsResponseHeader("X-UA-Compatible"))
						exec.setResponseHeader("X-UA-Compatible", "IE=EmulateIE7");
				} catch (Throwable ex) { //ignore (it might not be allowed)
				}

//FUTURE: Consider if config.isKeepDesktopAcrossVisits() implies cacheable
//Why yes: the client doesn't need to ask the server for updated content
//Why no: browsers seems fail to handle DHTML correctly (when BACK to
//a DHTML page), so it is better to let the server handle cache, if any
			final boolean cacheable =
				_cacheable != null ?  _cacheable.booleanValue():
					_desktop.getDevice().isCacheable();
			if (!cacheable) {
				//Bug 1520444
				exec.setResponseHeader("Pragma", "no-cache");
				exec.addResponseHeader("Cache-Control", "no-cache");
				exec.addResponseHeader("Cache-Control", "no-store");
				//exec.addResponseHeader("Cache-Control", "private");
				//exec.addResponseHeader("Cache-Control", "max-age=0");
				//exec.addResponseHeader("Cache-Control", "s-maxage=0");
				//exec.addResponseHeader("Cache-Control", "must-revalidate");
				//exec.addResponseHeader("Cache-Control", "proxy-revalidate");
				//exec.addResponseHeader("Cache-Control", "post-check=0");
				//exec.addResponseHeader("Cache-Control", "pre-check=0");
				exec.setResponseHeader("Expires", "-1");

				exec.setAttribute(Attributes.NO_CACHE, Boolean.TRUE);
				//so HtmlPageRenderers.outLangJavaScripts generates JS's keepDesktop correctly
			}
			if (_hdres != null)
				for (Iterator it = _hdres.iterator(); it.hasNext();) {
					final Object[] vals = (Object[])it.next();
					final String nm = (String)vals[0];
					final Object val = vals[1];
					final boolean add = ((Boolean)vals[2]).booleanValue();
					if (val instanceof Date) {
						if (add) exec.addResponseHeader(nm, (Date)val);
						else exec.setResponseHeader(nm, (Date)val);
					} else {
						if (add) exec.addResponseHeader(nm, (String)val);
						else exec.setResponseHeader(nm, (String)val);
					}
				}
		}

		final PageRenderer renderer = (PageRenderer)
			exec.getAttribute(Attributes.PAGE_RENDERER);
		(renderer != null ? renderer: _langdef.getPageRenderer())
			.render(this, out);
	}
	private static boolean shallIE7Compatible() {
		if (_ie7compat == null)
			_ie7compat = Boolean.valueOf("true".equals(
				Library.getProperty("org.zkoss.zk.ui.EmulateIE7")));
		return _ie7compat.booleanValue();
	}
	private static Boolean _ie7compat;

	/** @deprecated As of release 5.0.0, the concept of namespace is
	 * deprecated and replaced with the attributes of a scope (such as
	 * a page and a component).
	 */
	public final Namespace getNamespace() {
		return _ns;
	}
	public void interpret(String zslang, String script, Scope scope) {
		getInterpreter(zslang).interpret(script, scope);
	}
	/** @deprecated As of release 5.0.0, replaced with
	 * {@link #interpret(String,String,Scope)}.
	 */
	public void interpret(String zslang, String script, Namespace ns) {
		interpret(zslang, script, getScope(ns));
	}
	/** @deprecated
	 */
	private static Scope getScope(Namespace ns) {
		if (ns == null) return null;
		Scope s = ns.getOwner();
		return s != null ? s: ns.getOwnerPage();
	}
	public Interpreter getInterpreter(String zslang) {
		zslang = (zslang != null ? zslang: _zslang).toLowerCase();
		Interpreter ip = (Interpreter)_ips.get(zslang);
		if (ip == null) {
			ip = Interpreters.newInterpreter(zslang, this);
			_ips.put(zslang, ip);
				//set first to avoid dead loop if script calls interpret again

			final String script = _langdef.getInitScript(zslang);
			if (script != null)
				ip.interpret(script, this);
		}

		//evaluate deferred zscripts, if any
		evalDeferredZScripts(ip, zslang);
		return ip;
	}

	public Collection getLoadedInterpreters() {
		return _ips != null ? _ips.values(): Collections.EMPTY_LIST; //just in case
	}
	public String getZScriptLanguage() {
		return _zslang;
	}
	public void setZScriptLanguage(String zslang)
	throws InterpreterNotFoundException {
		if (!Objects.equals(zslang, _zslang)) {
			if (!Interpreters.exists(zslang))
				throw new InterpreterNotFoundException(zslang, MZk.NOT_FOUND, zslang);
			_zslang = zslang;
		}
	}
	public void addDeferredZScript(Component parent, ZScript zscript) {
		if (zscript != null) {
			if (_zsDeferred == null)
				_zsDeferred = new LinkedList();
			_zsDeferred.add(new Object[] {parent, zscript});
		}
	}
	/** Evaluates the deferred zscript.
	 * It is called when the interpreter is loaded
	 */
	private void evalDeferredZScripts(Interpreter ip, String zslang) {
		if (_zsDeferred != null) {
			for (Iterator it = _zsDeferred.iterator(); it.hasNext();) {
				final Object[] zsInfo = (Object[])it.next();
				final ZScript zscript = (ZScript)zsInfo[1];
				String targetlang = zscript.getLanguage();
				if (targetlang == null)
					targetlang = _zslang; //use default

				if (targetlang.equalsIgnoreCase(zslang)) { //case insensitive
					it.remove(); //done

					final Component parent = (Component)zsInfo[0];
					if ((parent == null || parent.getPage() == this)
					&& isEffective(zscript, parent)) {
						final Scope scope =
							Scopes.beforeInterpret(parent != null ? (Scope)parent: this);
						try {
							ip.interpret(zscript.getContent(this, parent), scope);
						} finally {
							Scopes.afterInterpret();
						}
					}
				}
			}

			if (_zsDeferred.isEmpty())
				_zsDeferred = null;
		}
	}
	private boolean isEffective(Condition cond, Component comp) {
		return comp != null ? cond.isEffective(comp): cond.isEffective(this);
	}

	public boolean isListenerAvailable(String evtnm) {
		if (_listeners != null) {
			final List l = (List)_listeners.get(evtnm);
			return l != null && !l.isEmpty();
		}
		return false;
	}
	public Iterator getListenerIterator(String evtnm) {
		if (_listeners != null) {
			final List l = (List)_listeners.get(evtnm);
			if (l != null)
				return new ListenerIterator(l);
		}
		return CollectionsX.EMPTY_ITERATOR;
	}

	public final Component getOwner() {
		return _owner;
	}
	public final void setOwner(Component comp) {
		if (_owner != null)
			throw new IllegalStateException("owner can be set only once");
		_owner = comp;
		if (_owner instanceof Includer)
			((Includer)_owner).setChildPage(this);
	}

	/** @deprecated As of release 5.0.0, the default parent is no longe meaningful. */
 	public Component getDefaultParent() {
 		return null;
 	}
	/** @deprecated As of release 5.0.0, the default parent is no longe meaningful. */
 	public void setDefaultParent(Component comp) {
 	}

	public void sessionWillPassivate(Desktop desktop) {
		for (Iterator it = getRoots().iterator(); it.hasNext();)
			((ComponentCtrl)it.next()).sessionWillPassivate(this);

		willPassivate(_attrs.getAttributes().values());
		willPassivate(_attrs.getListeners());

		if (_listeners != null)
			for (Iterator it = _listeners.values().iterator(); it.hasNext();)
				willPassivate((Collection)it.next());

		willPassivate(_resolvers);

		//backward compatible (we store variables in attributes)
		for (Iterator it = _attrs.getAttributes().values().iterator();
		it.hasNext();) {
			final Object val = it.next();
			if (val instanceof NamespaceActivationListener) //backward compatible
				((NamespaceActivationListener)val).willPassivate(_ns);
		}
	}
	public void sessionDidActivate(Desktop desktop) {
		_desktop = desktop;

		if (_ownerUuid != null) {
			setOwner(_desktop.getComponentByUuid(_ownerUuid));
			_ownerUuid = null;
		}

		for (Iterator it = getRoots().iterator(); it.hasNext();)
			((ComponentCtrl)it.next()).sessionDidActivate(this);

		didActivate(_attrs.getAttributes().values());
		didActivate(_attrs.getListeners());

		if (_listeners != null)
			for (Iterator it = _listeners.values().iterator(); it.hasNext();)
				didActivate((Collection)it.next());

		didActivate(_resolvers);

		//backward compatible (we store variables in attributes)
		for (Iterator it = _attrs.getAttributes().values().iterator(); it.hasNext();) {
			final Object val = it.next();
			if (val instanceof NamespaceActivationListener) //backward compatible
				((NamespaceActivationListener)val).didActivate(_ns);
		}
	}
	private void willPassivate(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				willPassivate(it.next());
	}
	private void willPassivate(Object o) {
		if (o instanceof PageSerializationListener)
			((PageActivationListener)o).willPassivate(this);
	}
	private void didActivate(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				didActivate(it.next());
	}
	private void didActivate(Object o) {
		if (o instanceof PageSerializationListener)
			((PageActivationListener)o).didActivate(this);
	}

	public LanguageDefinition getLanguageDefinition() {
		return _langdef;
	}
	public ComponentDefinitionMap getComponentDefinitionMap() {
		return _compdefs;
	}
	public ComponentDefinition getComponentDefinition(String name, boolean recurse) {
		final ComponentDefinition compdef = _compdefs.get(name);
		if (!recurse || compdef != null)
			return compdef;

		try {
			return _langdef.getComponentDefinition(name);
		} catch (DefinitionNotFoundException ex) {
		}
		return null;
	}
	public ComponentDefinition getComponentDefinition(Class cls, boolean recurse) {
		final ComponentDefinition compdef = _compdefs.get(cls);
		if (!recurse || compdef != null)
			return compdef;

		try {
			return _langdef.getComponentDefinition(cls);
		} catch (DefinitionNotFoundException ex) {
		}
		return null;
	}
	public Class getExpressionFactoryClass() {
		return _expfcls;
	}
	public void setExpressionFactoryClass(Class expfcls) {
		if (expfcls != null && !ExpressionFactory.class.isAssignableFrom(expfcls))
			throw new IllegalArgumentException(expfcls+" must implement "+ExpressionFactory.class);
		_expfcls = expfcls;
	}

	//-- Serializable --//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		s.writeObject(_langdef != null ? _langdef.getName(): null);
		s.writeObject(_owner != null ? _owner.getUuid(): null);

		final Map attrs = _attrs.getAttributes();
		willSerialize(attrs.values());
		Serializables.smartWrite(s, attrs);
		final List lns = _attrs.getListeners();
		willSerialize(lns);
		Serializables.smartWrite(s, lns);

		if (_listeners != null)
			for (Iterator it = _listeners.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				s.writeObject(me.getKey());

				final Collection ls = (Collection)me.getValue();
				willSerialize(ls);
				Serializables.smartWrite(s, ls);
			}
		s.writeObject(null);

		willSerialize(_resolvers);
		Serializables.smartWrite(s, _resolvers);

		//Handles interpreters
		for (Iterator it = _ips.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final Object ip = me.getValue();
			if (ip instanceof SerializableAware) {
				s.writeObject((String)me.getKey()); //zslang

				((SerializableAware)ip).write(s, null);
			}
		}
		s.writeObject(null); //denote end-of-interpreters
	}
	private void willSerialize(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				willSerialize(it.next());
	}
	private void willSerialize(Object o) {
		if (o instanceof PageSerializationListener)
			((PageSerializationListener)o).willSerialize(this);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();

		final String langnm = (String)s.readObject();
		if (langnm != null)
			_langdef = LanguageDefinition.lookup(langnm);

		_ownerUuid = (String)s.readObject();
			//_owner is restored later when sessionDidActivate is called

		final Map attrs = _attrs.getAttributes();
		Serializables.smartRead(s, attrs);
		final List lns = _attrs.getListeners();
		Serializables.smartRead(s, lns);

		for (;;) {
			final String evtnm = (String)s.readObject();
			if (evtnm == null) break; //no more

			if (_listeners == null) _listeners = new HashMap();
			final Collection ls = Serializables.smartRead(s, (Collection)null);
			_listeners.put(evtnm, ls);
		}

		_resolvers = (List)Serializables.smartRead(s, _resolvers); //might be null

		//Handles interpreters
		for (;;) {
			final String zslang = (String)s.readObject();
			if (zslang == null) break; //no more

			((SerializableAware)getInterpreter(zslang)).read(s);
		}

		//didDeserialize
		didDeserialize(attrs.values());
		didDeserialize(lns);
		didDeserialize(_resolvers);
		if (_listeners != null)
			for (Iterator it = _listeners.values().iterator(); it.hasNext();)
				didDeserialize((Collection)it.next());
	}
	private void didDeserialize(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				didDeserialize(it.next());
	}
	private void didDeserialize(Object o) {
		if (o instanceof PageSerializationListener)
			((PageSerializationListener)o).didDeserialize(this);
	}

	//-- Object --//
	public String toString() {
		return "[Page "+(_id.length() > 0 ? _id: _uuid)+']';
	}

	/** @deprecated */
	private class NS implements Namespace {
		//Namespace//
		public Component getOwner() {
			return null;
		}
		public Page getOwnerPage() {
			return PageImpl.this;
		}
		public Set getVariableNames() {
			return _attrs.getAttributes().keySet();
		}
		public boolean containsVariable(String name, boolean local) {
			return hasAttributeOrFellow(name, !local)
				|| getXelVariable(null, null, name, true) != null;
		}
		public Object getVariable(String name, boolean local) {
			final Object o = getAttributeOrFellow(name, !local);
			return o != null ? o: getXelVariable(null, null, name, true);
		}
		public void setVariable(String name, Object value, boolean local) {
			setAttribute(name, value);
		}
		public void unsetVariable(String name, boolean local) {
			removeAttribute(name);
		}

		/** @deprecated */
		public Namespace getParent() {
			return null;
		}
		/** @deprecated */
		public void setParent(Namespace parent) {
			throw new UnsupportedOperationException();
		}
		/** @deprecated */
		public boolean addChangeListener(NamespaceChangeListener listener) {
			return false;
		}
		/** @deprecated */
		public boolean removeChangeListener(NamespaceChangeListener listener) {
			return false;
		}
	}
}
