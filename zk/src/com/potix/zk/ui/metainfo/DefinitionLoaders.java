/* DefinitionLoaders.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug 29 21:57:08     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.metainfo;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Collections;
import java.net.URL;
import java.io.IOException;

import com.potix.lang.D;
import com.potix.lang.Classes;
import com.potix.util.Pair;
import com.potix.util.IllegalSyntaxException;
import com.potix.util.logging.Log;
import com.potix.util.resource.Locator;
import com.potix.util.resource.ClassLocator;
import com.potix.idom.Document;
import com.potix.idom.Element;
import com.potix.idom.ProcessingInstruction;
import com.potix.idom.input.SAXBuilder;
import com.potix.idom.util.IDOMs;
import com.potix.el.Taglib;
import com.potix.web.servlet.JavaScript;
import com.potix.web.servlet.StyleSheet;

import com.potix.zk.ui.UiException;
import com.potix.zk.ui.metainfo.LanguageDefinition;
import com.potix.zk.ui.metainfo.ComponentDefinition;

/**
 * Utilities to load language definitions.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class DefinitionLoaders {
	private static final Log log = Log.lookup(DefinitionLoaders.class);

	private static List _addons = Collections.EMPTY_LIST;

	/** Adds a language addon.
	 *
	 * <p>Note: this method can be called only before definitions have
	 * been loaded.
	 *
	 * @exception IllegalStateException if definitions has been loaded.
	 */
	public static void addLanguage(Locator locator, URL url) {
		if (locator == null || url == null)
			throw new IllegalArgumentException("null");
		if (_addons == null)
			throw new IllegalStateException("Definition has been loaded. You cannot add more.");
		if (_addons == Collections.EMPTY_LIST)
			_addons = new LinkedList();
		_addons.add(new Pair(locator, url));
	}

	/** Loads all lang.xml found in /metainfo/zk. */
	/*package*/ static void load() {
		final ClassLocator locator = new ClassLocator();

		//1. process lang.xml (no particular dependency)
		try {
			for (Enumeration en = locator.getResources("metainfo/zk/lang.xml");
			en.hasMoreElements();) {
				final URL url = (URL)en.nextElement();
				if (log.debugable()) log.debug("Loading "+url);
				parse(new SAXBuilder(false, false, true).build(url), locator, false);
			}
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex); //abort
		}

		//2. process lang-addon.xml (with dependency)
		try {
			final List xmls = locator.getDependentXmlResources(
				"metainfo/zk/lang-addon.xml", "addon-name", "depends");
			for (Iterator it = xmls.iterator(); it.hasNext();) {
				try {
					parse((Document)it.next(), locator, true);
				} catch (Exception ex) {
					log.error("Failed to load addon", ex);
					//keep running
				}
			}
		} catch (Exception ex) {
			log.error("Failed to load addon", ex);
			//keep running
		}

		//3. process other addon (from ConfigParser)
		for (Iterator it = _addons.iterator(); it.hasNext();) {
			final Pair p = (Pair)it.next();
			try {
				parse(new SAXBuilder(false, false, true).build((URL)p.y),
					(Locator)p.x, true);
			} catch (Exception ex) {
				log.error("Failed to load addon: "+p.y, ex);
				//keep running
			}
		}
		_addons = null; //prevent addLanguage being called again
	}

	private static void parse(Document doc, Locator locator, boolean addon)
	throws Exception {
		final Element root = doc.getRootElement();
		final String lang = IDOMs.getRequiredElementValue(root, "language-name");
		final LanguageDefinition langdef;
		if (addon) {
			if (log.debugable()) log.debug("Addon language to "+lang+" from "+root.getElementValue("addon-name", true));
			langdef = LanguageDefinition.lookup(lang);

			if (!root.getElements("case-insensitive").isEmpty())
				throw new UiException("You can not specify case-insensitive in addon");
		} else {
			final String ns = (String)IDOMs.getRequiredElementValue(root, "namespace");
			if (log.debugable()) log.debug("Load language: "+lang+", "+ns);

			final Map pagemolds = parseMolds(root);
			final String desktopURI = (String)pagemolds.get("desktop");
			final String pageURI = (String)pagemolds.get("page");
			if (desktopURI == null || pageURI == null)
				throw new IllegalSyntaxException("Both desktop and page molds must be specified, "+root.getLocator());

			final List exts = parseExtensions(root);
			if (exts.isEmpty())
				throw new IllegalSyntaxException("The extension must be specified for "+lang);

			langdef = new LanguageDefinition(
				lang, ns, exts, desktopURI, pageURI, locator);

			for (Iterator it = root.getElements("case-insensitive").iterator();
			it.hasNext();) {
				final Element el = (Element)it.next();
				final String s = el.getText(true);
				langdef.setCaseInsensitive(!"false".equalsIgnoreCase(s));
			}
		}

		parsePI(langdef, doc);
		parseLabelTemplate(langdef, root);
		parseDynamicTag(langdef, root);
		parseMacroTemplate(langdef, root);

		for (Iterator it = root.getElements("javascript").iterator();
		it.hasNext();) {
			final Element el = (Element)it.next();
			final String src = el.getAttributeValue("src");
			final String ctn = el.getText(true);
			final JavaScript js;
			if (src != null && src.length() > 0) {
				if (ctn != null && ctn.length() > 0)
					throw new UiException("You cannot specify the content if the src attribute is specified, "+el.getLocator());
				final String charset = el.getAttributeValue("charset");
				js = new JavaScript(src, charset);
			} else if (ctn != null && ctn.length() > 0) {
				js = new JavaScript(ctn);
			} else {
				throw new UiException("You must specify either the src attribute or the content, "+el.getLocator());
			}
			langdef.addJavaScript(js);
		}
		for (Iterator it = root.getElements("javascript-module").iterator();
		it.hasNext();) {
			final Element el = (Element)it.next();
			langdef.addJavaScriptModule(
				IDOMs.getRequiredAttributeValue(el, "name"),
				IDOMs.getRequiredAttributeValue(el, "version"));
		}


		for (Iterator it = root.getElements("stylesheet").iterator();
		it.hasNext();) {
			final Element el = (Element)it.next();
			final String href = el.getAttributeValue("href");
			final String ctn = el.getText(true);
			final StyleSheet ss;
			if (href != null && href.length() > 0) {
				if (ctn != null && ctn.length() > 0)
					throw new UiException("You cannot specify the content if the href attribute is specified, "+el.getLocator());
				final String type = el.getAttributeValue("type");
				ss = new StyleSheet(href, type);
			} else if (ctn != null && ctn.length() > 0) {
				ss = new StyleSheet(ctn);
			} else {
				throw new UiException("You must specify either the href attribute or the content, "+el.getLocator());
			}
			langdef.addStyleSheet(ss);
		}

		for (Iterator it = root.getElements("zscript").iterator();
		it.hasNext();) {
			final Element el = (Element)it.next();
			final String s = el.getText(true);
			langdef.addScript(s);
		}

		for (Iterator it = root.getElements("component").iterator();
		it.hasNext();) {
			final Element el = (Element)it.next();
			final String name =
				IDOMs.getRequiredElementValue(el, "component-name");

			final String macroUri = el.getElementValue("macro-uri", true);
			final ComponentDefinition compdef;
			if (macroUri != null && macroUri.length() != 0) {
				if (log.finerable()) log.finer("macro component definition: "+name);

				compdef = new ComponentDefinition(langdef, name, macroUri);
				final String clsnm = el.getElementValue("component-class", true);
				if (clsnm != null && clsnm.length() > 0) {
					noEL("component-class", clsnm, el);
					compdef.setImplementationClass(locateClass(clsnm));
				}

				langdef.initMacroDefinition(compdef);
				langdef.addComponentDefinition(compdef);
			} else if (el.getElement("extends") != null) { //override
				if (log.finerable()) log.finer("Override component definition: "+name);

				final String extnm = el.getElementValue("extends", true);
				final ComponentDefinition ref =
					langdef.getComponentDefinition(extnm);
				if (ref.isMacro())
					throw new UiException("Unable to extend from a macro component, "+el.getLocator());

				if (extnm.equals(name)) {
					compdef = ref;
				} else {
					compdef = ref.clone(name);
					langdef.addComponentDefinition(compdef);
				}

				final String clsnm = el.getElementValue("component-class", true);
				if (clsnm != null && clsnm.length() > 0) {
					noEL("component-class", clsnm, el);
					compdef.setImplementationClass(locateClass(clsnm));
				}
			} else {
				if (log.finerable()) log.finer("Add component definition: name="+name);

				final String clsnm =
					IDOMs.getRequiredElementValue(el, "component-class");
				noEL("component-class", clsnm, el);
				compdef = new ComponentDefinition(
					langdef, name, locateClass(clsnm));
				langdef.addComponentDefinition(compdef);
			}

			Map map = parseMolds(el);
			for (Iterator e = map.entrySet().iterator(); e.hasNext();) {
				final Map.Entry me = (Map.Entry)e.next();
				compdef.addMold((String)me.getKey(), (String)me.getValue());
			}

			map = parseParams(el);
			for (Iterator e = map.entrySet().iterator(); e.hasNext();) {
				final Map.Entry me = (Map.Entry)e.next();
				compdef.addParam((String)me.getKey(), (String)me.getValue());
			}

			map = parseProps(el);
			for (Iterator e = map.entrySet().iterator(); e.hasNext();) {
				final Map.Entry me = (Map.Entry)e.next();
				compdef.addProperty(
					(String)me.getKey(), (String)me.getValue(), null);
			}
		}
	}
	private static Class locateClass(String clsnm) throws Exception {
		try {
			return Classes.forNameByThread(clsnm);
		} catch (ClassNotFoundException ex) {
			throw new ClassNotFoundException("Not found: "+clsnm, ex);
		}
	}
	private static void noEL(String nm, String val, Element el)
	throws UiException {
		if (val != null && val.indexOf("${") >= 0)
			throw new UiException(nm+" does not support EL expressions, "+el.getLocator());
	}
	/** Parse the processing instructions. */
	private static void parsePI(LanguageDefinition langdef, Document doc)
	throws Exception {
		for (Iterator it = doc.getChildren().iterator(); it.hasNext();) {
			final Object o = it.next();
			if (!(o instanceof ProcessingInstruction))
				continue;

			final ProcessingInstruction pi = (ProcessingInstruction)o;
			final String target = pi.getTarget();
			final Map params = pi.parseData();
			if ("taglib".equals(target)) {
				final String uri = (String)params.remove("uri");
				final String prefix = (String)params.remove("prefix");
				if (!params.isEmpty())
					log.warning("Ignored unknown attribute: "+params+", "+pi.getLocator());
				if (uri == null || prefix == null)
					throw new IllegalSyntaxException("Both uri and prefix attribute are required, "+pi.getLocator());
				if (log.debugable()) log.debug("taglib: prefix="+prefix+" uri="+uri);
				langdef.addTaglib(new Taglib(prefix, uri));
			} else {
				log.warning("Unknown processing instruction: "+target);
			}
		}
	}
	/** Parse the component used to represent a label.
	 */
	private static
	void parseLabelTemplate(LanguageDefinition langdef, Element el) {
		el = el.getElement("label-template");
		if (el != null) {
			final Element raw = el.getElement("raw");
			langdef.setLabelTemplate(
				IDOMs.getRequiredElementValue(el, "component-name"),
				IDOMs.getRequiredElementValue(el, "component-attribute"),
				raw != null && !"false".equals(raw.getText(true)));
		}
	}
	private static
	void parseMacroTemplate(LanguageDefinition langdef, Element el)
	throws Exception {
		el = el.getElement("macro-template");
		if (el != null) {
			langdef.setMacroTemplate(
				locateClass(IDOMs.getRequiredElementValue(el, "macro-class")),
				IDOMs.getRequiredElementValue(el, "macro-uri"));
		}
	}
	private static
	void parseDynamicTag(LanguageDefinition langdef, Element el)
	throws ClassNotFoundException {
		el = el.getElement("dynamic-tag");
		if (el != null) {
			final String compnm =
				IDOMs.getRequiredElementValue(el, "component-name");
			final Set reservedAttrs = new HashSet(5);
			for (Iterator it = el.getElements("reserved-attribute").iterator();
			it.hasNext();)
				reservedAttrs.add(((Element)it.next()).getText(true));
			langdef.setDynamicTagInfo(compnm, reservedAttrs);
		}
		//if (log.finerable()) log.finer(el);
	}
	private static List parseExtensions(Element elm) {
		final List exts = new LinkedList();
		for (Iterator it = elm.getElements("extension").iterator(); it.hasNext();) {
			final Element el = (Element)it.next();
			final String ext = el.getText(true);
			if (ext.length() != 0) {
				for (int j = 0, len = ext.length(); j < len; ++j) {
					final char cc = ext.charAt(j);
					if ((cc < 'a' || cc > 'z') && (cc < 'A' || cc > 'Z')
					&& (cc < '0' || cc > '9'))
						throw new UiException("Invalid extension; only letters and numbers are allowed: "+ext);
				}
				exts.add(ext);
			}
		}
		///if (log.finerable()) log.finer(exts);
		return exts;
	}
	private static final
	Map parseMap(Element elm, String type, String name, String value) {
		final Map map = new HashMap();
		for (Iterator it = elm.getElements(type).iterator(); it.hasNext();) {
			final Element el = (Element)it.next();
			final String nm = IDOMs.getRequiredElementValue(el, name);
			final String uri = IDOMs.getRequiredElementValue(el, value);
			map.put(nm, uri);
		}
		return map;
	}
	private static Map parseProps(Element elm) {
		return parseMap(elm, "property", "property-name", "property-value");
	}
	private static Map parseMolds(Element elm) {
		return parseMap(elm, "mold", "mold-name", "mold-uri");
	}
	private static Map parseParams(Element elm) {
		return parseMap(elm, "param", "param-name", "param-value");
	}

	private static class Addon {
		private final Document document;
		private final int priority;
		private Addon(Document document) {
			this.document = document;

			final String p = document.getRootElement().getElementValue("priority", true);
			this.priority = p != null && p.length() > 0 ? Integer.parseInt(p): 0;
		}
		private static void add(List addons, Document document) {
			final Addon addon = new Addon(document);
			for (ListIterator it = addons.listIterator(); it.hasNext();) {
				final Addon a = (Addon)it.next();
				if (a.priority < addon.priority) {
					it.previous();
					it.add(addon);
					return; //done
				}
			}
			addons.add(addon);
		}
	}
}
