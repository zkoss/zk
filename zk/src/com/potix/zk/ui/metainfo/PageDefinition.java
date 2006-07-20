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
import com.potix.zk.ui.sys.WebAppCtrl;
import com.potix.zk.ui.sys.PageCtrl;
import com.potix.zk.ui.sys.RequestInfo;

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
	/** List(String src). */
	private final List _imports = new LinkedList();
	/** A map of component definition defined in this page. */
	private Map _compdefs;
	/** Map(String clsnm, ComponentDefinition compdef). */
	private Map _compdefsByClass;

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

	/** Adds a src (URI) of a ZUML page to import. */
	public void addImport(String src) {
		if (src == null || src.length() == 0)
			throw new IllegalArgumentException("empty");
		synchronized (_imports) {
			_imports.add(src);
		}
	}
	/** Returns the imported content (added by {@link #addImport}), or null
	 * no import at all.
	 */
	public Imports getImports(Page page) {
		if (_imports.isEmpty()) return null;

		final Imports imports = new Imports();
		return imports;
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
	 *
	 * @param imports the import info returned by {@link #getImports}.
	 */
	public List doInit(Page page, Imports imports) {
		if (_initdefs.isEmpty())
			return Collections.EMPTY_LIST;

		final List inits = new LinkedList();
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
	 *
	 * @param imports the import info returned by {@link #getImports}.
	 */
	public List newVariableResolvers(Page page, Imports imports) {
		if (_resolvdefs.isEmpty())
			return Collections.EMPTY_LIST;

		final List resolvs = new LinkedList();
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
		return resolvs;
	}

	/** Adds a component definition belonging to this page definition only.
	 */
	public void addComponentDefinition(ComponentDefinition compdef) {
		if (compdef == null)
			throw new IllegalArgumentException("null");

		if (_compdefs == null) {
			synchronized (this) {
				if (_compdefs == null) {
					final Map defs = new HashMap(5),
						defsByClass = new HashMap(5);
					defs.put(compdef.getName(), compdef);
					_compdefs = defs;

					final Object implcls = compdef.getImplementationClass();
					if (implcls instanceof Class)
						defsByClass.put(((Class)implcls).getName(), compdef);
					else //String
						defsByClass.put(implcls, compdef);
					_compdefsByClass = defsByClass;
					return; //done
				}
			}
		}
		synchronized (_compdefs) {
			_compdefs.put(compdef.getName(), compdef);
		}
		synchronized (_compdefsByClass) {
			final Object implcls = compdef.getImplementationClass();
			if (implcls instanceof Class)
				_compdefsByClass.put(((Class)implcls).getName(), compdef);
			else //String
				_compdefsByClass.put(implcls, compdef);
		}
	}
	/** Returns the component defintion of the specified name, or null
	 * if not found.
	 */
	public ComponentDefinition getComponentDefinition(String name) {
		if (_compdefs == null) return null;
		synchronized (_compdefs) {
			return (ComponentDefinition)_compdefs.get(name);
		}
	}
	/** Returns the component defintion of the specified name, or null
	 * if not found.
	 */
	public ComponentDefinition getComponentDefinition(Class cls) {
		if (_compdefsByClass == null) return null;

		synchronized (_compdefsByClass) {
			for (; cls != null; cls = cls.getSuperclass()) {
				final ComponentDefinition compdef =
					(ComponentDefinition)_compdefsByClass.get(cls.getName());
				if (compdef != null) return compdef;
			}
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
	public FunctionMapper getFunctionMapper(Imports imports) {
		if (_funmap == null) {
			synchronized (this) {
				if (_funmap == null)
					_funmap = FunctionMappers.getFunctionMapper(_taglibs, _locator);
			}
		}
		return _funmap;
	}

	/** Initializes a page after execution is activated.
	 * It setup the identifier and title, adds it to desktop,
	 * and then iInterpret all scripts unpon the page.
	 */
	public void init(Page page) {
		((PageCtrl)page).init(_id, _title, _style);

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

	/** The infomation returned by {@link PageDefinition#getImports}.
	 */
	public static class Imports {
		private Imports() {
		}
	}

	//Object//
	public String toString() {
		return "[PageDefinition:"+getName()+']';
	}
}
