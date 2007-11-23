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

import org.zkoss.xel.FunctionMapper;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
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
 * @see ComponentDefinition
 */
public class PageDefinition extends NodeInfo {
	private final LanguageDefinition _langdef;
	private final Locator _locator;
	private String _id, _title, _style;
	/** The request path. */
	private String _path = "";
	/** The zscript language. */
	private String _zslang = "Java";
	private final List _taglibs = new LinkedList();
	private FunctionMapper _funmap;
	/* List(InitiatorInfo). */
	private final List _initdefs = new LinkedList();
	/** List(VariableResolverInfo). */
	private final List _resolvdefs = new LinkedList();
	/** List(HeaderInfo). */
	private final List _headerdefs = new LinkedList();
	private final ComponentDefinitionMap _compdefs;

	/** Constructor.
	 * @param langdef the default language which is used if no namespace
	 * is specified. Note: a page might have components from different
	 * languages.
	 */
	public PageDefinition(LanguageDefinition langdef, Locator locator) {
		if (langdef == null)
			throw new IllegalArgumentException("null langdef");
		if (locator == null)
			throw new IllegalArgumentException("null locator");

		_langdef = langdef;
		_locator = locator;
		_compdefs = new ComponentDefinitionMap(
			_langdef.getComponentDefinitionMap().isCaseInsensitive());
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

	/** Returns the language definition that this page is default to be.
	 */
	public LanguageDefinition getLanguageDefinition() {
		return _langdef;
	}
	/** Returns the parent (always null).
	 */
	public NodeInfo getParent() {
		return null;
	}

	/** Returns the locator associated with this page definition.
	 */
	public Locator getLocator() {
		return _locator;
	}

	/** Returns the default scripting language which is assumed when
	 * a zscript element doesn't specify any language.
	 *
	 * <p>Default: Java.
	 */
	public String getZScriptLanguage() {
		return _zslang;
	}
	/** Sets the default scripting language which is assumed when
	 * a zscript element doesn't specify any language.
	 *
	 * @param zslang the default scripting language.
	 */
	public void setZScriptLanguage(String zslang) {
		if (zslang == null || zslang.length() == 0)
			throw new IllegalArgumentException("null or empty");
		_zslang = zslang;
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
			addInitiatorInfo((InitiatorInfo)it.next());
		for (Iterator it = pgdef._compdefs.getNames().iterator();
		it.hasNext();) {
			final String name = (String)it.next();
			addComponentDefinition(pgdef._compdefs.get(name));
		}
	}

	/** Adds a defintion of {@link org.zkoss.zk.ui.util.Initiator}. */
	public void addInitiatorInfo(InitiatorInfo init) {
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
				final InitiatorInfo def = (InitiatorInfo)it.next();
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

	/** Adds a defintion of {@link org.zkoss.zk.scripting.VariableResolver}. */
	public void addVariableResolverInfo(VariableResolverInfo resolver) {
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
				final VariableResolverInfo def =
					(VariableResolverInfo)it.next();
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

	/** Adds a header definition ({@link HeaderInfo}.
	 */
	public void addHeaderInfo(HeaderInfo header) {
		if (header == null)
			throw new IllegalArgumentException("null");
		synchronized (_headerdefs) {
			_headerdefs.add(header);
		}
	}
	/** Converts the header definitions (added by {@link #addHeaderInfo} to
	 * HTML tags.
	 */
	public String getHeaders(Page page) {
		if (_headerdefs.isEmpty())
			return "";

		final StringBuffer sb = new StringBuffer(256);
		synchronized (_headerdefs) {
			for (Iterator it = _headerdefs.iterator(); it.hasNext();)
				sb.append(((HeaderInfo)it.next()).toHTML(page)).append('\n');
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
			&& !langdef.getDeviceType().equals(ld2.getDeviceType()))
				throw new UiException("Component definition, "+compdef
					+", does not belong to the same device type of the page definition, "
					+ld2.getDeviceType());
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
	 * It setup the identifier and title, and adds it to desktop.
	 */
	public void init(Page page, boolean evalHeaders) {
		((PageCtrl)page).init(
			_id, _title, _style, evalHeaders ? getHeaders(page): "", null);
	}

	//NodeInfo//
	public PageDefinition getPageDefinition() {
		return this;
	}

	//Object//
	public String toString() {
		return "[PageDefinition:"+(_id != null ? _id: _title)+']';
	}
}
