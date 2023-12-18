/* PageImpl.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 18:17:32     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.io.Serializables;
import org.zkoss.lang.ClassResolver;
import org.zkoss.lang.Classes;
import org.zkoss.lang.ImportedClassResolver;
import org.zkoss.lang.Library;
import org.zkoss.lang.Objects;
import org.zkoss.lang.SimpleClassResolver;
import org.zkoss.lang.Strings;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.DualCollection;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.FunctionMapperExt;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
import org.zkoss.xel.util.Evaluators;
import org.zkoss.zk.au.out.AuSetTitle;
import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.scripting.HierarchicalAware;
import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.InterpreterNotFoundException;
import org.zkoss.zk.scripting.Interpreters;
import org.zkoss.zk.scripting.SerializableAware;
import org.zkoss.zk.ui.AbstractPage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.Includer;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.ext.Scopes;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinitionMap;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.sys.Attributes;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zk.ui.sys.PageConfig;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.PageRenderer;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.PageActivationListener;
import org.zkoss.zk.ui.util.PageSerializationListener;
import org.zkoss.zk.ui.util.Template;

/**
 * An implementation of {@link Page} and {@link PageCtrl}.
 * Refer to them for more details.
 *
 * <p>Note: though {@link PageImpl} is serializable, it is designed
 * to work with Web container to enable the serialization of sessions.
 * It is not suggested to serialize and deserialize it directly since
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
	private static final Logger log = LoggerFactory.getLogger(PageImpl.class);
	private static final long serialVersionUID = 20110726L;

	/** The component that includes this page, or null if not included. */
	private transient Component _owner;
	/** Used to restore _owner. */
	private transient String _ownerUuid;
	private transient Desktop _desktop;
	private String _id = "", _uuid;
	private String _title = "", _style = "", _viewport = "auto";
	private String _path;
	private String _zslang;
	/** A list of deferred zscript [Component parent, {@link ZScript}]. */
	private List<Object[]> _zsDeferred;
	/** A map of attributes. */
	private transient SimpleScope _attrs;
	/** A map of event listener: Map(evtnm, List(EventListener)). */
	private transient Map<String, List<EventListener<? extends Event>>> _listeners;
	/** The reason to store it is PageDefinition is not serializable. */
	private final ComponentDefinitionMap _compdefs;
	/** The reason to store it is PageDefinition is not serializable. */
	private transient LanguageDefinition _langdef;
	/** The header tags. */
	private String _hdbfr = "", _hdaft = "";
	/** The response headers. */
	private Collection<Object[]> _hdres;
	/** The root attributes. */
	private String _rootAttrs = "";
	private String _contentType, _docType, _firstLine, _wgtcls;
	private Boolean _cacheable;
	private Boolean _autoTimeout;
	/** The expression factory (ExpressionFactory).*/
	private Class<? extends ExpressionFactory> _expfcls;
	/** A map of interpreters Map(String zslang, Interpreter ip). */
	private transient Map<String, Interpreter> _ips;
	/** The mapper representing all mappers being added to this page. */
	private final FunctionMapper _mapper = new PageFuncMapper();
	/** A list of {@link FunctionMapper}. */
	private transient List<FunctionMapper> _mappers;
	/** A list of {@link VariableResolver}. */
	private transient List<VariableResolver> _resolvers;
	/** A list of class's name pattern (String). */
	private ClassResolver _clsresolver;
	/** Whether _clsresolver is shared with other pages. */
	private boolean _clsresolverShared;
	private boolean _complete;
	private List<String> _impclss;

	private Map<String, Template> _templates;

	/** Constructs a page by giving the page definition.
	 *
	 * <p>Note: when a page is constructed, it doesn't belong to a desktop
	 * yet. Caller has to invoke {@link #init} to complete
	 * the creation of a page.
	 * Why two phase? Constructor could be called before execution
	 * is activated, but {@link #init} must be called in an execution.
	 *
	 * <p>Also note that {@link #getId} and {@link #getTitle}
	 * are not ready until {@link #init} is called.
	 *
	 * @param pgdef the page definition (never null).
	 */
	public PageImpl(PageDefinition pgdef) {
		constr(pgdef.getLanguageDefinition(), pgdef.getRequestPath(), pgdef.getZScriptLanguage());

		_compdefs = pgdef.getComponentDefinitionMap();
		_clsresolver = pgdef.getImportedClassResolver();
		_clsresolverShared = true;
		_impclss = pgdef.getImportedClasses();

		//NOTE: don't store pgdef since it is not serializable
	}

	/** Constructs a page without page definition and richlet.
	 *
	 * @param langdef the language definition (never null)
	 * @param compdefs the component definition map.
	 * If null, an empty map is assumed.
	 * @param path the request path. If null, empty is assumed.
	 * @param zslang the zscript language. If null, "Java" is assumed.
	 */
	public PageImpl(LanguageDefinition langdef, ComponentDefinitionMap compdefs, String path, String zslang) {
		constr(langdef, path, zslang);
		_compdefs = compdefs != null ? compdefs
				: new ComponentDefinitionMap(_langdef.getComponentDefinitionMap().isCaseInsensitive());
		_clsresolver = new SimpleClassResolver();
	}

	/** Constructs a page with another page as instance
	 * @since 6.0.0
	 */
	public PageImpl(Page ref) {
		this(ref.getLanguageDefinition(), ref.getComponentDefinitionMap(), ref.getRequestPath(),
				ref.getZScriptLanguage());
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
		constr(richlet.getLanguageDefinition(), path, null);

		_compdefs = new ComponentDefinitionMap(_langdef.getComponentDefinitionMap().isCaseInsensitive());
		_clsresolver = new SimpleClassResolver();
	}

	private void constr(LanguageDefinition langdef, String path, String zslang) {
		init();
		_langdef = langdef;
		_path = path != null ? path : "";
		_zslang = zslang != null ? zslang : "Java";
	}

	/** Initialized the page when constructed or deserialized.
	 */
	protected void init() {
		_ips = new LinkedHashMap<String, Interpreter>(2);
		_attrs = new SimpleScope(this);
	}

	public void init(PageConfig config) {
		final Execution exec = Executions.getCurrent();
		if (((ExecutionCtrl) exec).isRecovering()) {
			final String uuid = config.getUuid(), id = config.getId();
			if (uuid == null || id == null)
				throw new IllegalArgumentException("both id and uuid are required in recovering");
			_uuid = uuid;
			_id = id;
		} else {
			_uuid = ((DesktopCtrl) _desktop).getNextUuid(this);

			if (_id == null || _id.length() == 0)
				_id = config.getId();
			if (_id != null)
				_id = (String) exec.evaluate(this, _id, String.class);
			if (_id == null) {
				_id = "";
			} else if (_id.length() != 0) {
				final String INVALID = ".&\\%";
				if (Strings.anyOf(_id, INVALID, 0) < _id.length())
					throw new IllegalArgumentException("Invalid page ID: " + _id + ". Invalid characters: " + INVALID);
			}
		}

		//Note: the evaluation order was changed since 5.0.2
		((DesktopCtrl) _desktop).addPage(this);
		//add page before evaluate title and others

		String s;
		if (_title.length() == 0) {
			s = config.getTitle();
			if (s != null)
				setTitle(s);
		}
		if (_style.length() == 0) {
			s = config.getStyle();
			if (s != null)
				setStyle(s);
		}
		if ("auto".equals(_viewport)) {
			s = config.getViewport();
			if (s != null)
				setViewport(s);
		}

		s = config.getBeforeHeadTags();
		if (s != null)
			_hdbfr = s;
		s = config.getAfterHeadTags();
		if (s != null)
			_hdaft = s;
		_hdres = config.getResponseHeaders();
		if (_hdres.isEmpty())
			_hdres = null;

		//ZK-4914
		EventListener scriptErrorListener = null;
		try {
			String scriptErrorListenerClass = Library.getProperty(Attributes.CLIENT_SCRIPT_ERROR_LISTENER_CLASS);
			if (!Strings.isEmpty(scriptErrorListenerClass))
				scriptErrorListener = (EventListener) Classes.newInstanceByThread(scriptErrorListenerClass);
		} catch (Exception e) {
			log.warn("", e);
		}
		if (scriptErrorListener != null)
			addEventListener(Events.ON_SCRIPT_ERROR, scriptErrorListener);
	}

	/** Returns the UI engine.
	 */
	private final UiEngine getUiEngine() {
		return ((WebAppCtrl) _desktop.getWebApp()).getUiEngine();
	}

	//-- Page --//
	private final Execution getExecution() {
		return _desktop != null ? _desktop.getExecution() : Executions.getCurrent();
	}

	public final FunctionMapper getFunctionMapper() {
		return _mapper;
	}

	public boolean addFunctionMapper(FunctionMapper mapper) {
		if (mapper == null)
			return false;

		if (_mappers == null)
			_mappers = new LinkedList<FunctionMapper>();
		else if (_mappers.contains(mapper))
			return false;

		_mappers.add(0, mapper); //FILO order
		return true;
	}

	public boolean removeFunctionMapper(FunctionMapper mapper) {
		return _mappers != null && _mappers.remove(mapper);
	}

	public boolean hasFunctionMapper(FunctionMapper mapper) {
		return _mappers != null && _mappers.contains(mapper);
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
		_id = id != null ? id : "";
		//No need to update client since it is allowed only before init(...)
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		if (title == null)
			title = "";
		if (!_title.equals(title)) {
			_title = title;
			if (_desktop != null) {
				final Execution exec = getExecution();
				if (_title.length() > 0) {
					_title = (String) exec.evaluate(this, _title, String.class);
					if (_title == null)
						_title = "";
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
		if (style == null)
			style = "";
		if (!_style.equals(style)) {
			_style = style;
			if (_desktop != null) {
				final Execution exec = getExecution();
				if (_style.length() > 0) {
					_style = (String) exec.evaluate(this, _style, String.class);
					if (_style == null)
						_style = "";
				}
				//FUTURE: might support the change of style dynamically
			}
		}
	}

	public String getViewport() {
		return _viewport;
	}

	public void setViewport(String viewport) {
		if (viewport == null)
			viewport = "auto";
		if (!_viewport.equals(viewport)) {
			_viewport = viewport;
			if (_desktop != null) {
				final Execution exec = getExecution();
				if (_viewport.length() > 0) {
					_viewport = (String) exec.evaluate(this, _viewport, String.class);
					if (_viewport == null)
						_viewport = "auto";
				}
			}
		}
	}

	public Map<String, Object> getAttributes(int scope) {
		switch (scope) {
		case DESKTOP_SCOPE:
			if (_desktop != null)
				return _desktop.getAttributes();
			break;
		case SESSION_SCOPE:
			if (_desktop != null)
				return _desktop.getSession().getAttributes();
			break;
		case APPLICATION_SCOPE:
			if (_desktop != null)
				return _desktop.getWebApp().getAttributes();
		case PAGE_SCOPE:
			return _attrs.getAttributes();
		case REQUEST_SCOPE:
			final Execution exec = getExecution();
			if (exec != null)
				return exec.getAttributes();
		}
		return Collections.emptyMap();
	}

	public Map<String, Object> getAttributes() {
		return _attrs.getAttributes();
	}

	public Object getAttribute(String name, int scope) {
		return getAttributes(scope).get(name);
	}

	public Object getAttribute(String name) {
		return _attrs.getAttribute(name);
	}

	public Object getAttribute(String name, boolean recurse) {
		Object val = getAttribute(name);
		return val != null || !recurse || hasAttribute(name) ? val
				: _desktop != null ? _desktop.getAttribute(name, true) : null;
	}

	public boolean hasAttribute(String name, int scope) {
		return getAttributes(scope).containsKey(name);
	}

	public boolean hasAttribute(String name) {
		return _attrs.hasAttribute(name);
	}

	public boolean hasAttribute(String name, boolean recurse) {
		return hasAttribute(name) || (recurse && _desktop != null && _desktop.hasAttribute(name, true));
	}

	public Object setAttribute(String name, Object value, int scope) {
		final Map<String, Object> attrs = getAttributes(scope);
		if (attrs == Collections.EMPTY_MAP)
			throw new IllegalStateException("This component doesn't belong to any ID space: " + this);
		return attrs.put(name, value);
	}

	public Object setAttribute(String name, Object value) {
		return _attrs.setAttribute(name, value);
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

	public Object removeAttribute(String name, int scope) {
		final Map<String, Object> attrs = getAttributes(scope);
		if (attrs == Collections.EMPTY_MAP)
			throw new IllegalStateException("This component doesn't belong to any ID space: " + this);
		return attrs.remove(name);
	}

	public Object removeAttribute(String name) {
		return _attrs.removeAttribute(name);
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

		return recurse && _desktop != null ? _desktop.getAttribute(name, true) : null;
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

	public boolean addClassResolver(ClassResolver resolver) {
		//Currently we support only SimpleClassResolver and ImportedClassResolver
		//but it is good enough
		if (resolver == null)
			return false;

		if (!(resolver instanceof SimpleClassResolver)) {
			if (!(resolver instanceof ImportedClassResolver))
				throw new UnsupportedOperationException("Only ImportedClassResolver supported");

			if (_clsresolver instanceof SimpleClassResolver) {
				_clsresolver = resolver;
				_clsresolverShared = true;
			} else {
				ImportedClassResolver cur = (ImportedClassResolver) _clsresolver;
				if (_clsresolverShared) {
					ImportedClassResolver newcr = new ImportedClassResolver();
					newcr.addAll(cur);
					_clsresolver = newcr;
					_clsresolverShared = false;
				}
				cur.addAll((ImportedClassResolver) resolver);
			}
		}
		return true;
	}

	public Class<?> resolveClass(String clsnm) throws ClassNotFoundException {
		try {
			return _clsresolver.resolveClass(clsnm);
		} catch (ClassNotFoundException ex) {
			final Class<?> cls = getZScriptClass(clsnm);
			if (cls != null)
				return cls;
			throw ex;
		}
	}

	public ClassResolver getClassResolver() {
		return _clsresolver;
	}

	public Class<?> getZScriptClass(String clsnm) {
		for (Interpreter ip : getLoadedInterpreters()) {
			Class<?> cls = ip.getClass(clsnm);
			if (cls != null)
				return cls;
		}
		return null; //Since ZK 6, we don't look for the current thread's class loader
	}

	public Function getZScriptFunction(String name, Class[] argTypes) {
		for (Interpreter ip : getLoadedInterpreters()) {
			Function mtd = ip.getFunction(name, argTypes);
			if (mtd != null)
				return mtd;
		}
		return null;
	}

	public Function getZScriptFunction(Component comp, String name, Class[] argTypes) {
		for (Interpreter ip : getLoadedInterpreters()) {
			Function mtd = ip instanceof HierarchicalAware ? ((HierarchicalAware) ip).getFunction(comp, name, argTypes)
					: ip.getFunction(name, argTypes);
			if (mtd != null)
				return mtd;
		}
		return null;
	}

	public Object getZScriptVariable(String name) {
		for (Interpreter ip : getLoadedInterpreters()) {
			final Object val = ip.getVariable(name);
			if (val != null)
				return val;
		}
		return null;
	}

	public Object getZScriptVariable(Component comp, String name) {
		for (Interpreter ip : getLoadedInterpreters()) {
			final Object val = ip instanceof HierarchicalAware ? ((HierarchicalAware) ip).getVariable(comp, name)
					: ip.getVariable(name);
			if (val != null)
				return val;
		}
		return null;
	}

	public Object getXelVariable(String name) {
		return getXelVariable(null, null, name, false);
	}

	public Object getXelVariable(XelContext ctx, Object base, Object name, boolean ignoreExec) {
		if (!ignoreExec) {
			final Execution exec = getExecution();
			if (exec != null)
				return Evaluators.resolveVariable(ctx, exec.getVariableResolver(), base, name);
			//note: ExecutionResolver will call back this method
		}

		if (_resolvers != null) {
			for (Iterator<VariableResolver> it = CollectionsX.comodifiableIterator(_resolvers); it.hasNext();) {
				Object o = Evaluators.resolveVariable(ctx, it.next(), base, name);
				if (o != null)
					return o;
			}
		}
		return null;
	}

	public boolean addVariableResolver(VariableResolver resolver) {
		if (resolver == null)
			return false;

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

	public boolean addEventListener(String evtnm, EventListener<? extends Event> listener) {
		if (evtnm == null || listener == null)
			throw new IllegalArgumentException("null");
		if (!Events.isValid(evtnm))
			throw new IllegalArgumentException("Invalid event name: " + evtnm);

		if (_listeners == null)
			_listeners = new HashMap<String, List<EventListener<? extends Event>>>(2);

		List<EventListener<? extends Event>> l = _listeners.get(evtnm);
		if (l != null) {
			if (duplicateListenerIgnored()) {
				for (EventListener<? extends Event> li : l) {
					if (listener.equals(li))
						return false;
				}
			}
		} else {
			_listeners.put(evtnm, l = new LinkedList<EventListener<? extends Event>>());
		}
		l.add(listener);
		return true;
	}

	public boolean removeEventListener(String evtnm, EventListener<? extends Event> listener) {
		if (evtnm == null || listener == null)
			throw new NullPointerException();

		if (_listeners != null) {
			final List<EventListener<? extends Event>> ls = _listeners.get(evtnm);
			if (ls != null) {
				for (Iterator<EventListener<? extends Event>> it = ls.iterator(); it.hasNext();) {
					final EventListener<? extends Event> li = it.next();
					if (listener.equals(li)) {
						if (ls.size() == 1)
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

	private static boolean duplicateListenerIgnored() {
		if (dupListenerIgnored == null)
			dupListenerIgnored = Boolean
					.valueOf("true".equals(Library.getProperty("org.zkoss.zk.ui.EventListener.duplicateIgnored")));
		return dupListenerIgnored.booleanValue();
	}

	private static Boolean dupListenerIgnored;

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

		_desktop.getWebApp().getConfiguration().init(this);
	}

	public void destroy() {
		super.destroy();
		try {
			if (_ips != null) {
				final List<Interpreter> ips = new ArrayList<Interpreter>(_ips.values());
				_ips.clear();
				_ips = null; //not just clear since it is better to NPE than memory leak
				for (Interpreter ip : ips) {
					try {
						ip.destroy();
					} catch (Throwable ex) {
						log.warn("Failed to destroy " + ip, ex);
					}
				}
			}
		} catch (Throwable ex) { //avoid racing
			log.warn("Failed to clean up interpreters of " + this, ex);
		}

		//theoretically, the following is not necessary, but, to be safe...
		_desktop = null;
		_owner = null;
		_listeners = null;
		_resolvers = null;
		_mappers = null;
		_attrs.getAttributes().clear();
	}

	public boolean isAlive() {
		return _ips != null;
	}

	public String getBeforeHeadTags() {
		return _hdbfr;
	}

	public String getAfterHeadTags() {
		return _hdaft;
	}

	public void addBeforeHeadTags(String tags) {
		if (tags != null && tags.length() > 0)
			_hdbfr += '\n' + tags;
	}

	public void addAfterHeadTags(String tags) {
		if (tags != null && tags.length() > 0)
			_hdaft += '\n' + tags;
	}

	public Collection<Object[]> getResponseHeaders() {
		if (_hdres != null)
			return _hdres;
		return Collections.emptyList();
	}

	public String getRootAttributes() {
		return _rootAttrs;
	}

	public void setRootAttributes(String rootAttrs) {
		_rootAttrs = rootAttrs != null ? rootAttrs : "";
	}

	public String getContentType() {
		return _contentType;
	}

	public void setContentType(String contentType) {
		_contentType = contentType;
	}

	public String getWidgetClass() {
		return _wgtcls;
	}

	public void setWidgetClass(String wgtcls) {
		_wgtcls = wgtcls != null && wgtcls.length() > 0 ? wgtcls : null;
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

	public void redraw(Writer out) throws IOException {
		String ctl;
		final Execution exec = getExecution();
		final boolean au = exec.isAsyncUpdate(null);
		if (!au && !exec.isIncluded()
				&& ((ctl = ExecutionsCtrl.getPageRedrawControl(exec)) == null || "desktop".equals(ctl))) {
			//FUTURE: Consider if config.isKeepDesktopAcrossVisits() implies cacheable
			//Why yes: the client doesn't need to ask the server for updated content
			//Why no: browsers seems fail to handle DHTML correctly (when BACK to
			//a DHTML page), so it is better to let the server handle cache, if any
			final boolean cacheable = _cacheable != null ? _cacheable.booleanValue()
					: _desktop.getDevice().isCacheable();
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
				for (Object[] vals : _hdres) {
					final String nm = (String) vals[0];
					final Object val = vals[1];
					final boolean add = ((Boolean) vals[2]).booleanValue();
					if (val instanceof Date) {
						if (add)
							exec.addResponseHeader(nm, (Date) val);
						else
							exec.setResponseHeader(nm, (Date) val);
					} else {
						if (add)
							exec.addResponseHeader(nm, (String) val);
						else
							exec.setResponseHeader(nm, (String) val);
					}
				}
		}

		final PageRenderer renderer = (PageRenderer) exec.getAttribute(Attributes.PAGE_RENDERER);
		final Object oldrendering = exec.setAttribute(Attributes.PAGE_RENDERING, Boolean.TRUE);
		try {
			(renderer != null ? renderer : _langdef.getPageRenderer()).render(this, out);
		} finally {
			if (oldrendering != null)
				exec.setAttribute(Attributes.PAGE_RENDERING, oldrendering);
			else
				exec.removeAttribute(Attributes.PAGE_RENDERING);
		}
	}

	private static Boolean _ieAutoCompat;

	private static boolean shallDisableAutoCompatible() {
		if (_ieAutoCompat == null)
			_ieAutoCompat = Boolean
					.valueOf("true".equals(Library.getProperty("org.zkoss.zk.ui.IEAutoCompatible.disabled")));
		return _ieAutoCompat.booleanValue();
	}

	public void interpret(String zslang, String script, Scope scope) {
		if (script != null && script.length() > 0) //optimize for better performance
			getInterpreter(zslang).interpret(script, scope);
	}

	public Interpreter getInterpreter(String zslang) {
		zslang = (zslang != null ? zslang : _zslang).toLowerCase(java.util.Locale.ENGLISH);
		Interpreter ip = _ips.get(zslang);
		if (ip == null) {
			if (_desktop != null //might be null, if deserialized
					&& !_desktop.getWebApp().getConfiguration().isZScriptEnabled())
				throw new UiException("zscript is not allowed since <disable-zscript> is configured");

			ip = Interpreters.newInterpreter(zslang, this);
			_ips.put(zslang, ip);
			//set first to avoid dead loop if script calls interpret again

			String script = _langdef.getInitScript(zslang);
			if (script != null) {
				//Bug ZK-1498: also add <?import ?> directive to zscript
				//Bug ZK-1514: _impclss may be null
				if (_impclss != null && !_impclss.isEmpty() && "java".equals(zslang)) {
					StringBuilder sb = new StringBuilder();
					for (String name : _impclss)
						sb.append("\nimport ").append(name).append(";");
					script += sb.toString();
					sb = null;
				}
				ip.interpret(script, this);
			}
		}

		//evaluate deferred zscripts, if any
		evalDeferredZScripts(ip, zslang);
		return ip;
	}

	public Collection<Interpreter> getLoadedInterpreters() {
		if (_ips != null)
			return _ips.values();
		return Collections.emptyList(); //just in case
	}

	public String getZScriptLanguage() {
		return _zslang;
	}

	public void setZScriptLanguage(String zslang) throws InterpreterNotFoundException {
		if (!Objects.equals(zslang, _zslang)) {
			if (!Interpreters.exists(zslang))
				throw new InterpreterNotFoundException(zslang, MZk.NOT_FOUND, zslang);
			_zslang = zslang;
		}
	}

	public void addDeferredZScript(Component parent, ZScript zscript) {
		if (zscript != null) {
			if (_zsDeferred == null)
				_zsDeferred = new LinkedList<Object[]>();
			_zsDeferred.add(new Object[] { parent, zscript });
		}
	}

	/** Evaluates the deferred zscript.
	 * It is called when the interpreter is loaded
	 */
	private void evalDeferredZScripts(Interpreter ip, String zslang) {
		if (_zsDeferred != null) {
			for (Iterator<Object[]> it = _zsDeferred.iterator(); it.hasNext();) {
				final Object[] zsInfo = it.next();
				final ZScript zscript = (ZScript) zsInfo[1];
				String targetlang = zscript.getLanguage();
				if (targetlang == null)
					targetlang = _zslang; //use default

				if (targetlang.equalsIgnoreCase(zslang)) { //case insensitive
					it.remove(); //done

					final Component parent = (Component) zsInfo[0];
					if (parent == null || parent.getPage() == this) {
						final Scope scope = Scopes.beforeInterpret(parent != null ? (Scope) parent : this);
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
		return comp != null ? cond.isEffective(comp) : cond.isEffective(this);
	}

	public boolean isListenerAvailable(String evtnm) {
		if (_listeners != null) {
			final List<EventListener<? extends Event>> ls = _listeners.get(evtnm);
			return ls != null && !ls.isEmpty();
		}
		return false;
	}

	public Iterable<EventListener<? extends Event>> getEventListeners(String evtnm) {
		if (_listeners != null) {
			final List<EventListener<? extends Event>> l = _listeners.get(evtnm);
			if (l != null)
				return new Iterable<EventListener<? extends Event>>() {
					public Iterator<EventListener<? extends Event>> iterator() {
						return CollectionsX.comodifiableIterator(l);
					}
				};
		}
		return CollectionsX.emptyIterable();
	}

	public final Component getOwner() {
		return _owner;
	}

	public final void setOwner(Component comp) {
		if (_owner != null)
			throw new IllegalStateException("owner can be set only once");
		_owner = comp;
		if (_owner instanceof Includer)
			((Includer) _owner).setChildPage(this);
	}

	public void sessionWillPassivate(Desktop desktop) {
		for (Component root = getFirstRoot(); root != null; root = root.getNextSibling())
			((ComponentCtrl) root).sessionWillPassivate(this);

		willPassivate(_attrs.getAttributes().values());
		willPassivate(_attrs.getListeners());

		if (_listeners != null)
			for (Iterator<List<EventListener<? extends Event>>> it = CollectionsX
					.comodifiableIterator(_listeners.values()); it.hasNext();)
				willPassivate(it.next());
		willPassivate(_resolvers);
		willPassivate(_mappers);
	}

	public void sessionDidActivate(Desktop desktop) {
		_desktop = desktop;

		if (_ownerUuid != null) {
			setOwner(_desktop.getComponentByUuid(_ownerUuid));
			_ownerUuid = null;
		}

		for (Component root = getFirstRoot(); root != null; root = root.getNextSibling())
			((ComponentCtrl) root).sessionDidActivate(this);

		didActivate(_attrs.getAttributes().values());
		didActivate(_attrs.getListeners());

		if (_listeners != null)
			for (Iterator<List<EventListener<? extends Event>>> it = CollectionsX
					.comodifiableIterator(_listeners.values()); it.hasNext();)
				didActivate(it.next());

		didActivate(_resolvers);
		didActivate(_mappers);
	}

	private void willPassivate(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				willPassivate(it.next());
	}

	private void willPassivate(Object o) {
		if (o instanceof PageSerializationListener)
			((PageActivationListener) o).willPassivate(this);
	}

	private void didActivate(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				didActivate(it.next());
	}

	private void didActivate(Object o) {
		if (o instanceof PageSerializationListener)
			((PageActivationListener) o).didActivate(this);
	}

	public LanguageDefinition getLanguageDefinition() {
		return _langdef;
	}

	public ComponentDefinitionMap getComponentDefinitionMap() {
		return _compdefs;
	}

	public ComponentDefinition getComponentDefinition(String name, boolean recurse) {
		ComponentDefinition compdef = _compdefs.get(name);
		if (!recurse || compdef != null)
			return compdef;

		compdef = _langdef.getComponentDefinitionIfAny(name);
		if (compdef == null)
			compdef = _langdef.getShadowDefinitionIfAny(name);
		return compdef;
	}

	public ComponentDefinition getComponentDefinition(Class<? extends Component> cls, boolean recurse) {
		ComponentDefinition compdef = _compdefs.get(cls);
		if (!recurse || compdef != null)
			return compdef;

		compdef = _langdef.getComponentDefinitionIfAny(cls);
		if (compdef == null)
			compdef = _langdef.getShadowDefinitionIfAny(cls);
		return compdef;
	}

	public Class<? extends ExpressionFactory> getExpressionFactoryClass() {
		return _expfcls;
	}

	public void setExpressionFactoryClass(Class<? extends ExpressionFactory> expfcls) {
		if (expfcls != null && !ExpressionFactory.class.isAssignableFrom(expfcls))
			throw new IllegalArgumentException(expfcls + " must implement " + ExpressionFactory.class);
		_expfcls = expfcls;
	}

	//ZK-2623: page scope template
	public void addTemplate(String name, Template template) {
		if (name == null)
			throw new IllegalArgumentException("template name should not be null");
		if (_templates == null)
			_templates = new LinkedHashMap<String, Template>();
		if (template != null)
			_templates.put(name, template);
	}

	public void removeTemplate(String name) {
		_templates.remove(name);
	}

	public Template getTemplate(String name) {
		return _templates != null ? _templates.get(name) : null;
	}

	//-- Serializable --//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();

		s.writeObject(_langdef != null ? _langdef.getName() : null);
		s.writeObject(_owner != null ? _owner.getUuid() : null);

		final Map<String, Object> attrs = _attrs.getAttributes();
		willSerialize(attrs.values());
		Serializables.smartWrite(s, attrs);
		final List<ScopeListener> lns = _attrs.getListeners();
		willSerialize(lns);
		Serializables.smartWrite(s, lns);

		if (_listeners != null)
			for (Map.Entry<String, List<EventListener<? extends Event>>> me : _listeners.entrySet()) {
				s.writeObject(me.getKey());

				final List<EventListener<? extends Event>> ls = me.getValue();
				willSerialize(ls);
				Serializables.smartWrite(s, ls);
			}
		s.writeObject(null);

		willSerialize(_resolvers);
		Serializables.smartWrite(s, _resolvers);

		willSerialize(_mappers);
		Serializables.smartWrite(s, _mappers);

		//Handles interpreters
		for (Map.Entry<String, Interpreter> me : _ips.entrySet()) {
			final Interpreter ip = me.getValue();
			if (ip instanceof SerializableAware) {
				s.writeObject(me.getKey()); //zslang

				((SerializableAware) ip).write(s, null);
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
			((PageSerializationListener) o).willSerialize(this);
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();

		final String langnm = (String) s.readObject();
		if (langnm != null)
			_langdef = LanguageDefinition.lookup(langnm);

		_ownerUuid = (String) s.readObject();
		//_owner is restored later when sessionDidActivate is called

		final Map<String, Object> attrs = _attrs.getAttributes();
		Serializables.smartRead(s, attrs);
		final List<ScopeListener> lns = _attrs.getListeners();
		Serializables.smartRead(s, lns);

		for (;;) {
			final String evtnm = (String) s.readObject();
			if (evtnm == null)
				break; //no more

			if (_listeners == null)
				_listeners = new HashMap<String, List<EventListener<? extends Event>>>();
			final List<EventListener<? extends Event>> ls = Serializables.smartRead(s,
					(List<EventListener<? extends Event>>) null);
			_listeners.put(evtnm, ls);
		}

		_resolvers = Serializables.smartRead(s, _resolvers); //might be null
		_mappers = Serializables.smartRead(s, _mappers); //might be null

		//Handles interpreters
		for (;;) {
			final String zslang = (String) s.readObject();
			if (zslang == null)
				break; //no more

			((SerializableAware) getInterpreter(zslang)).read(s);
		}

		//didDeserialize
		didDeserialize(attrs.values());
		didDeserialize(lns);
		didDeserialize(_resolvers);
		didDeserialize(_mappers);
		if (_listeners != null)
			for (List<EventListener<? extends Event>> ls : _listeners.values())
				didDeserialize(ls);
	}

	private void didDeserialize(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				didDeserialize(it.next());
	}

	private void didDeserialize(Object o) {
		if (o instanceof PageSerializationListener)
			((PageSerializationListener) o).didDeserialize(this);
	}

	//-- Object --//
	public String toString() {
		return "[Page " + (_id.length() > 0 ? _id : _uuid) + ']';
	}

	private class PageFuncMapper implements FunctionMapper, FunctionMapperExt, java.io.Serializable {
		public Function resolveFunction(String prefix, String name) throws XelException {
			if (_mappers != null) {
				for (Iterator<FunctionMapper> it = CollectionsX.comodifiableIterator(_mappers); it.hasNext();) {
					final Function f = it.next().resolveFunction(prefix, name);
					if (f != null)
						return f;
				}
			}
			return null;
		}

		public Collection<String> getClassNames() {
			Collection<String> coll = null;
			if (_mappers != null) {
				for (Iterator it = CollectionsX.comodifiableIterator(_mappers); it.hasNext();) {
					final FunctionMapper mapper = (FunctionMapper) it.next();
					if (mapper instanceof FunctionMapperExt)
						coll = combine(coll, ((FunctionMapperExt) mapper).getClassNames());
				}
			}
			if (coll != null)
				return coll;
			return Collections.emptyList();
		}

		public Class resolveClass(String name) throws XelException {
			if (_mappers != null) {
				for (Iterator it = CollectionsX.comodifiableIterator(_mappers); it.hasNext();) {
					final FunctionMapper mapper = (FunctionMapper) it.next();
					if (mapper instanceof FunctionMapperExt) {
						final Class c = ((FunctionMapperExt) mapper).resolveClass(name);
						if (c != null)
							return c;
					}
				}
			}
			return null;
		}
	}

	private static <E> Collection<E> combine(Collection<E> first, Collection<E> second) {
		return DualCollection.combine(first != null && !first.isEmpty() ? first : null,
				second != null && !second.isEmpty() ? second : null);
	}
}
