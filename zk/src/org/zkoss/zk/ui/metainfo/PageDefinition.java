/* PageDefinition.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 11:27:07     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.jsp.el.FunctionMapper;

import org.zkoss.lang.Classes;
import org.zkoss.util.resource.Locator;
import org.zkoss.el.FunctionMappers;
import org.zkoss.el.Taglib;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.Namespaces;
import org.zkoss.zk.scripting.VariableResolver;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.PageCtrl;

/**
 * A page definition.
 * It represents a ZUL page.
 *
 * @author tomyeh
 * @see InstanceDefinition
 * @see ComponentDefinition
 */
public class PageDefinition extends InstanceDefinition {
	private final Locator _locator;
	private String _id, _title, _style;
	/** The request path. */
	private String _path = "";
	private final List _taglibs = new LinkedList();
	private FunctionMapper _funmap;
	/* List(InitiatorDefinition). */
	private final List _initdefs = new LinkedList();
	/** List(VariableResolverDefinition). */
	private final List _resolvdefs = new LinkedList();
	/** List(Header). */
	private final List _headerdefs = new LinkedList();
	private final ComponentDefinitionMap _compdefs = new ComponentDefinitionMap();

	/** Constructor.
	 * @param langdef the default language which is used if no namespace
	 * is specified. Note: a page might have components from different
	 * languages.
	 */
	public PageDefinition(LanguageDefinition langdef, Locator locator) {
		super(langdef);

		if (langdef == null)
			throw new IllegalArgumentException("null langdef");
		if (locator == null)
			throw new IllegalArgumentException("null locator");

		_locator = locator;
	}
	/** Constructor.
	 * @param langdef the default language which is used if no namespace
	 * is specified. Note: a page might have components from different
	 * languages.
	 * @param id the identifier. See {@link #setId}.
	 * @param title the title. See {@link #setTitle}.
	 * @param style the CSS style. See {@link #setStyle}.
	 */
	public PageDefinition(LanguageDefinition langdef, String id, String title,
	String style, Locator locator) {
		this(langdef, locator);
		setId(id);
		setTitle(title);
		setStyle(style);
	}

	/** Returns the locator associated with this page definition.
	 */
	public Locator getLocator() {
		return _locator;
	}

	/** Returns the identitifer that will be assigned to pages created from
	 * this definition, or null if the identifier shall be generated automatically.
	 * <p>Note: the returned value might contain EL expressions.
	 */
	public String getId() {
		return _id;
	}
	/** Sets the identifier that will be assigned to pages created from this
	 * definition.
	 * @param id the identifier. It might contain EL expressions.
	 * If null or empty (null is assumed), page's ID is generated automatically.
	 * If not empty, ID (after evaluated) must be unquie in the same request.
	 */
	public void setId(String id) {
		_id = id != null && id.length() > 0 ? id: null;
	}
		
	/** Returns the title that will be assigned to pages created from
	 * this definition, or null if no title is assigned at the beginning.
	 * <p>Note: the returned value might contain EL expressions.
	 */
	public String getTitle() {
		return _title;
	}
	/** Sets the title that will be assigned to pages created from
	 * this definition, or null if no title is assigned at the beginning.
	 * @param title the title. If empty, null is assumed.
	 */
	public void setTitle(String title) {
		_title = title != null && title.length() > 0 ? title: null;
	}

	/** Returns the CSS style that will be assigned to pages created from
	 * this definition, or null if no style is assigned at the beginning.
	 * <p>Note: the returned value might contain EL expressions.
	 */
	public String getStyle() {
		return _style;
	}
	/** Sets the CSS style that will be assigned to pages created from
	 * this definition, or null if no style is assigned at the beginning.
	 * @param style the CSS style. If empty, null is assumed.
	 */
	public void setStyle(String style) {
		_style = style != null && style.length() > 0 ? style: null;
	}

	/** Returns the request path of this page definition, or ""
	 * if not available.
	 * <p>It is the same as the servlet path
	 * (javax.servlet.http.HttpServletRequest's getServletPath), if ZK is running
	 * at a servlet container.
	 */
	public String getRequestPath() {
		return _path;
	}
	/** Sets the request path of this page definition.
	 */
	public void setRequestPath(String path) {
		_path = path != null ? path: "";
	}

	/** Imports the component definitions from the specified definition.
	 */
	public void imports(PageDefinition pgdef) {
		for (Iterator it = pgdef._initdefs.iterator(); it.hasNext();)
			addInitiatorDefinition((InitiatorDefinition)it.next());
		for (Iterator it = pgdef._compdefs.getAll().iterator(); it.hasNext();)
			addComponentDefinition((ComponentDefinition)it.next());
	}

	/** Adds a defintion of {@link org.zkoss.zk.ui.util.Initiator}. */
	public void addInitiatorDefinition(InitiatorDefinition init) {
		if (init == null)
			throw new IllegalArgumentException("null");
		synchronized (_initdefs) {
			_initdefs.add(init);
		}
	}
	/** Returns a list of all {@link Initiator} and invokes
	 * its {@link Initiator#doInit} before returning.
	 * It never returns null.
	 */
	public List doInit(Page page) {
		if (_initdefs.isEmpty())
			return Collections.EMPTY_LIST;

		final List inits = new LinkedList();
		synchronized (_initdefs) {
			for (Iterator it = _initdefs.iterator(); it.hasNext();) {
				final InitiatorDefinition def = (InitiatorDefinition)it.next();
				try {
					final Initiator init = def.newInitiator(page);
					if (init != null) {
						init.doInit(page, def.getArguments(page));
						inits.add(init);
					}
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
				}
			}
		}
		return inits;
	}

	/** Adds a defintion of {@link org.zkoss.zk.ui.util.VariableResolver}. */
	public void addVariableResolverDefinition(VariableResolverDefinition resolver) {
		if (resolver == null)
			throw new IllegalArgumentException("null");
		synchronized (_resolvdefs) {
			_resolvdefs.add(resolver);
		}
	}
	/** Retrieves a list of variable resolvers defined for this page
	 * definition.
	 */
	public List newVariableResolvers(Page page) {
		if (_resolvdefs.isEmpty())
			return Collections.EMPTY_LIST;

		final List resolvs = new LinkedList();
		synchronized (_resolvdefs) {
			for (Iterator it = _resolvdefs.iterator(); it.hasNext();) {
				final VariableResolverDefinition def =
					(VariableResolverDefinition)it.next();
				try {
					final VariableResolver resolv = def.newVariableResolver(page);
					if (resolv != null) resolvs.add(resolv);
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
				}
			}
		}
		return resolvs;
	}

	/** Adds a header definition ({@link Header}.
	 */
	public void addHeader(Header header) {
		if (header == null)
			throw new IllegalArgumentException("null");
		synchronized (_headerdefs) {
			_headerdefs.add(header);
		}
	}
	/** Converts the header definitions (added by {@link #addHeader} to
	 * HTML tags.
	 */
	public String getHeaders(Page page) {
		if (_headerdefs.isEmpty())
			return "";

		final StringBuffer sb = new StringBuffer(256);
		synchronized (_headerdefs) {
			for (Iterator it = _headerdefs.iterator(); it.hasNext();)
				sb.append(((Header)it.next()).toHTML(page)).append('\n');
		}
		return sb.toString();
	}

	/** Returns the map of component definition (never null).
	 */
	public ComponentDefinitionMap getComponentDefinitionMap() {
		return _compdefs;
	}
	/** Adds a component definition belonging to this page definition only.
	 *
	 * <p>It is the same as calling {@link ComponentDefinitionMap#add} 
	 * against {@link #getComponentDefinitionMap}
	 */
	public void addComponentDefinition(ComponentDefinition compdef) {
		final LanguageDefinition langdef = compdef.getLanguageDefinition();
		if (langdef != null) {
			final LanguageDefinition ld2 = getLanguageDefinition();
			if (langdef != ld2
			&& !langdef.getClientType().equals(ld2.getClientType()))
				throw new UiException("Component definition, "+compdef
					+", does not belong to the same client type of the page definition, "
					+ld2.getClientType());
		}
		_compdefs.add(compdef);
	}
	/** Returns the component definition of the specified name, or null
	 * if not found.
	 *
	 * <p>Note: unlike {@link LanguageDefinition#getComponentDefinition},
	 * this method doesn't throw ComponentNotFoundException if not found.
	 * It just returns null.
	 *
	 * @param recur whether to look up the component from {@link #getLanguageDefinition}
	 */
	public ComponentDefinition getComponentDefinition(String name, boolean recur) {
		final ComponentDefinition compdef = _compdefs.get(name);
		if (!recur || compdef != null)
			return compdef;

		try {
			return getLanguageDefinition().getComponentDefinition(name);
		} catch (DefinitionNotFoundException ex) {
		}
		return null;
	}
	/** Returns the component definition of the specified class, or null
	 * if not found.
	 *
	 * <p>Note: unlike {@link LanguageDefinition#getComponentDefinition},
	 * this method doesn't throw ComponentNotFoundException if not found.
	 * It just returns null.
	 *
	 * @param recur whether to look up the component from {@link #getLanguageDefinition}
	 */
	public ComponentDefinition getComponentDefinition(Class cls, boolean recur) {
		final ComponentDefinition compdef = _compdefs.get(cls);
		if (!recur || compdef != null)
			return compdef;

		try {
			return getLanguageDefinition().getComponentDefinition(cls);
		} catch (DefinitionNotFoundException ex) {
		}
		return null;
	}

	/** Adds a tag lib. */
	public void addTaglib(Taglib taglib) {
		if (taglib == null)
			throw new IllegalArgumentException("null");
		synchronized (_taglibs) {
			_taglibs.add(taglib);
			_funmap = null; //ask for re-parse
		}
	}
	/** Returns the function mapper. */
	public FunctionMapper getFunctionMapper() {
		if (_funmap == null) {
			synchronized (this) {
				if (_funmap == null) {
					_funmap = FunctionMappers.getFunctionMapper(_taglibs, _locator);
					if (_funmap == null)
						_funmap = FunctionMappers.EMPTY_MAPPER;
				}
			}
		}
		return _funmap != FunctionMappers.EMPTY_MAPPER ? _funmap: null;
	}

	/** Initializes a page after execution is activated.
	 * It setup the identifier and title, adds it to desktop,
	 * and then iInterpret all scripts unpon the page.
	 *
	 * @param evalTopZscripts whether to evaluate the zscript declared at
	 * the top level
	 */
	public void init(Page page, boolean evalHeaders, boolean evalTopZscripts) {
		((PageCtrl)page).init(
			_id, _title, _style, evalHeaders ? getHeaders(page): "");

		if (evalTopZscripts) {
			final List scripts = getLanguageDefinition().getScripts();
			if (!scripts.isEmpty()) {
				final Namespace ns = Namespaces.beforeInterpret(null, page);
				try {
					for (Iterator it = scripts.iterator(); it.hasNext();)
						page.interpret((String)it.next(), ns);
				} finally {
					Namespaces.afterInterpret(ns);
				}
			}
		}
	}

	//-- super --//
	public void addCustomAttributes(CustomAttributes custAttrs) {
		throw new UnsupportedOperationException();
	}
	public void addProperty(String name, String value, Condition cond) {
		throw new UnsupportedOperationException();
	}
	public void addEventHandler(String name, String script) {
		throw new UnsupportedOperationException();
	}
	public Component newInstance(Page page) {
		throw new UnsupportedOperationException();
	}
	public String getMoldURI(String name) {
		throw new UnsupportedOperationException();
	}
	public Milieu getMilieu() {
		throw new UnsupportedOperationException();
	}

	//Object//
	public String toString() {
		return "[PageDefinition:"+getName()+']';
	}
}
