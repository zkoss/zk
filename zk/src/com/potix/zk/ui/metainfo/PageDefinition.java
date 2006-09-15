/* PageDefinition.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 11:27:07     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.metainfo;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.jsp.el.FunctionMapper;

import com.potix.lang.Classes;
import com.potix.util.resource.Locator;
import com.potix.el.FunctionMappers;
import com.potix.el.Taglib;

import com.potix.zk.ui.Execution;
import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.Condition;
import com.potix.zk.ui.util.Initiator;
import com.potix.zk.ui.util.Namespace;
import com.potix.zk.ui.util.Namespaces;
import com.potix.zk.ui.util.VariableResolver;
import com.potix.zk.ui.sys.ComponentCtrl;
import com.potix.zk.ui.sys.PageCtrl;

/**
 * A page definition.
 * It represents a ZUL page.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @see InstanceDefinition
 * @see ComponentDefinition
 */
public class PageDefinition extends InstanceDefinition {
	private final Locator _locator;
	private final String _id, _title, _style;
	private final List _taglibs = new LinkedList();
	private FunctionMapper _funmap;
	/* List(InitiatorDefinition). */
	private final List _initdefs = new LinkedList();
	/** List(VariableResolverDefinition). */
	private final List _resolvdefs = new LinkedList();
	/** List(Header). */
	private final List _headerdefs = new LinkedList();
	private final ComponentDefinitionMap _compdefs = new ComponentDefinitionMap();
	/**
	 * @param langdef the default language which is used if no namespace
	 * is specified. Note: a page might have components from different
	 * languages.
	 * @param id ID used to identify a page created by this definition.
	 * If null or empty, page's ID is generated automatically.
	 * If not empty, ID must be unquie in the same request.
	 * @param title the tile. If not empty, it is used as the title when
	 * this page is displayed as a single page (rather than being included). 
	 */
	public PageDefinition(LanguageDefinition langdef, String id, String title,
	String style, Locator locator) {
		super(langdef);

		if (locator == null)
			throw new NullPointerException("locator");

		_title = title != null && title.length() > 0 ? title: null;
		_id = id != null && id.length() > 0 ? id: null;
		_style = style != null && style.length() > 0 ? style: null;
		_locator = locator;
	}

	/** Imports the component definitions from the specified definition.
	 */
	public void imports(PageDefinition pgdef) {
		for (Iterator it = pgdef._initdefs.iterator(); it.hasNext();)
			addInitiatorDefinition((InitiatorDefinition)it.next());
		for (Iterator it = pgdef._compdefs.getAll().iterator(); it.hasNext();)
			addComponentDefinition((ComponentDefinition)it.next());
	}

	/** Adds a defintion of {@link com.potix.zk.ui.util.Initiator}. */
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

	/** Adds a defintion of {@link com.potix.zk.ui.util.VariableResolver}. */
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
		((PageCtrl)page).init(_id, _title, _style,
			evalHeaders ? getHeaders(page): "");

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
	public Millieu getMillieu() {
		throw new UnsupportedOperationException();
	}

	//Object//
	public String toString() {
		return "[PageDefinition:"+getName()+']';
	}
}
