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
import com.potix.util.prefs.Apps;
import com.potix.el.FunctionMappers;
import com.potix.el.Taglib;
import com.potix.web.servlet.StyleSheet;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.Condition;
import com.potix.zk.ui.util.Initiator;
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
	private FunctionMapper _mapper;
	private final List _styleSheets = new LinkedList(),
		_roStyleSheets = Collections.unmodifiableList(_styleSheets);
	private final List _initdefs = new LinkedList();
	private final List _resolvdefs = new LinkedList();
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

	/** Adds a style sheet. */
	public void addStyleSheet(StyleSheet ss) {
		if (ss == null)
			throw new IllegalArgumentException("null");
		synchronized (_styleSheets) {
			_styleSheets.add(ss);
		}
	}
	/** Returns a readonly list of all style sheets, {@link StyleSheet}.
	 */
	public List getStyleSheets() {
		return _roStyleSheets;
	}

	/** Adds a defintion of {@link com.potix.zk.ui.util.Initiator}. */
	public void addInitiatorDefinition(InitiatorDefinition init) {
		synchronized (_initdefs) {
			_initdefs.add(init);
		}
	}
	/** Returns a list of all {@link Initiator} and invokes
	 * its {@link Initiator#doInit} before returning.
	 * It never returns null.
	 */
	public List doInit(Page page) {
		if (_initdefs == null || _initdefs.isEmpty())
			return Collections.EMPTY_LIST;

		final List inits = new LinkedList();
		for (Iterator it = _initdefs.iterator(); it.hasNext();) {
			final InitiatorDefinition def = (InitiatorDefinition)it.next();
			try {
				final Initiator init = def.newInitiator(this, page);
				if (init != null) {
					init.doInit(page, def.getArguments(this, page));
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
		synchronized (_resolvdefs) {
			_resolvdefs.add(resolver);
		}
	}
	/** Adds all variable resolvers to the specified page.
	 */
	public void addVariableResolvers(Page page) {
		if (_resolvdefs == null || _resolvdefs.isEmpty())
			return;

		for (Iterator it = _resolvdefs.iterator(); it.hasNext();) {
			final VariableResolverDefinition def =
				(VariableResolverDefinition)it.next();
			try {
				final VariableResolver resolv =
					def.newVariableResolver(this, page);
				if (resolv != null)
					page.addVariableResolver(resolv);
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
	}

	/** Adds a component definition belonging to this page definition only.
	 */
	public void addComponentDefinition(ComponentDefinition compdef) {
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
		synchronized (_taglibs) {
			_taglibs.add(taglib);
			_mapper = null; //ask for re-parse
		}
	}
	/** Returns the function mapper. */
	public FunctionMapper getFunctionMapper() {
		if (_mapper == null) {
			synchronized (this) {
				if (_mapper == null)
					_mapper = FunctionMappers
						.getFunctionMapper(_taglibs, _locator);
			}
		}
		return _mapper;
	}

	/** Initializes a page after execution is activated.
	 * It setup the identifier and title, adds it to desktop,
	 * and then iInterpret all scripts unpon the page.
	 */
	public void init(Page page) {
		((PageCtrl)page).init(_id, _title, _style);

		for (Iterator it = getLanguageDefinition().getScripts().iterator();
		it.hasNext();) {
			final String script = (String)it.next();
			page.interpret(null, script);
		}
	}

	//-- super --//
	public void addCustomAttributes(CustomAttributes custAttrs) {
		throw new UnsupportedOperationException("No custom attributes supported by page definition");
	}
	public void addProperty(String name, String value, Condition cond) {
		throw new UnsupportedOperationException("No property initialization allowed");
	}
	public void applyProperties(Component comp) {
	}
	public void addEventHandler(String name, String script) {
		throw new UnsupportedOperationException("No event handler allowed");
	}
	public Component newInstance() {
		throw new UnsupportedOperationException("You create component from page definition");
	}
	public String getMoldURI(String name) {
		return null;
	}

	//Object//
	public String toString() {
		return "[PageDefinition:"+getName()+']';
	}
}
