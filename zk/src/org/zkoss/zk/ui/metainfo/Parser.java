/* Parser.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 14:26:18     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.io.File;
import java.io.Reader;
import java.io.FileNotFoundException;
import java.net.URL;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.lang.PotentialDeadLockException;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.Locator;
import org.zkoss.idom.Namespace;
import org.zkoss.idom.Document;
import org.zkoss.idom.Element;
import org.zkoss.idom.Text;
import org.zkoss.idom.CData;
import org.zkoss.idom.Item;
import org.zkoss.idom.Attribute;
import org.zkoss.idom.ProcessingInstruction;
import org.zkoss.idom.util.IDOMs;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.el.Taglib;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.impl.ZScriptInitiator;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.impl.ConditionImpl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.impl.RequestInfoImpl;

/**
 * Used to prase the ZUL file
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Parser {
	private static final Log log = Log.lookup(Parser.class);

	private final WebApp _wapp;
	private final Locator _locator;

	/** Constructor.
	 *
	 * @param locator the locator used to locate taglib and other resources.
	 * If null, wapp is assumed ({@link WebApp} is also assumed).
	 */	
	public Parser(WebApp wapp, Locator locator) {
		if (wapp == null)
			throw new IllegalArgumentException("null");
		_wapp = wapp;
		_locator = locator != null ? locator: (Locator)wapp;
	}

	/** Parses the specified file.
	 */
	public PageDefinition parse(File file) throws Exception {
		if (log.debugable()) log.debug("Parsing "+file);
		return parse(new SAXBuilder(true, false, true).build(file),
			getExtension(file.getName()));
	}
	/** Parses the specified URL.
	 */
	public PageDefinition parse(URL url) throws Exception {
		if (log.debugable()) log.debug("Parsing "+url);
		return parse(new SAXBuilder(true, false, true).build(url),
			getExtension(url.toExternalForm()));
	}

	private static final String getExtension(String nm) {
		int j = nm == null ? -1: nm.lastIndexOf('.');
		if (j < 0 || j < nm.lastIndexOf('/'))
			return null;

		final StringBuffer sb = new StringBuffer(8);
		for (final int len = nm.length(); ++j < len;) {
			final char cc = nm.charAt(j);
			if ((cc < 'a' || cc > 'z') && (cc < 'A' || cc > 'Z')
			&& (cc < '0' || cc > '9'))
				break;
			sb.append(cc);
		}
		return sb.toString();
	}
	/** Parses from the specified reader.
	 *
	 * @param extension the default extension if doc (of reader) doesn't specify
	 * an language. Ignored if null.
	 * If doc doesn't specify an language, {@link LanguageDefinition#lookupByExtension}
	 * is called.
	 */
	public PageDefinition parse(Reader reader, String extension)
	throws Exception {
		if (log.debugable()) log.debug("Parsing "+reader);
		return parse(new SAXBuilder(true, false, true).build(reader), extension);
	}
	/** Parss the raw content directly from a DOM tree.
	 *
	 * @param extension the default extension if doc doesn't specify
	 * an language. Ignored if null.
	 * If doc doesn't specify an language, {@link LanguageDefinition#lookupByExtension}
	 * is called.
	 */
	public PageDefinition parse(Document doc, String extension)
	throws Exception {
		//1. parse the page and import directive if any
		final List pis = new LinkedList();
		final List imports = new LinkedList();
		String lang = null, title = null, id = null, style = null;
		for (Iterator it = doc.getChildren().iterator(); it.hasNext();) {
			final Object o = it.next();
			if (!(o instanceof ProcessingInstruction)) continue;

			final ProcessingInstruction pi = (ProcessingInstruction)o;
			final String target = pi.getTarget();
			if ("page".equals(target)) {
				for (Iterator i2 = pi.parseData().entrySet().iterator(); i2.hasNext();) {
					final Map.Entry me = (Map.Entry)i2.next();
					final String nm = (String)me.getKey();
					final String val = (String)me.getValue();
					if ("language".equals(nm)) {
						lang = val;
						noEL("language", lang, pi);
					} else if ("title".equals(nm)) {
						title = val;
					} else if ("style".equals(nm)) {
						style = val;
					} else if ("id".equals(nm)) {
						id = val;
					} else {
						log.warning("Ignored unknown attribute: "+nm+", "+pi.getLocator());
					}
				}
			} else if ("import".equals(target)) { //import
				final Map params = pi.parseData();
				final String src = (String)params.remove("src");
				if (isEmpty(src))
					throw new UiException("The src attribute is required, "+pi.getLocator());
				if (!params.isEmpty())
					log.warning("Ignored unknown attributes: "+params.keySet()+", "+pi.getLocator());
				noEL("src", src, pi);
				imports.add(src);
			} else {
				pis.add(pi);
			}
		}

		//2. Create PageDefinition
		final LanguageDefinition langdef =
			extension != null && lang == null?
				LanguageDefinition.lookupByExtension(extension):
				LanguageDefinition.lookup(lang);
		final PageDefinition pgdef =
			new PageDefinition(langdef, id, title, style, getLocator());

		//3. resolve imports
		if (!imports.isEmpty()) {
			final RequestInfo ri =
				new RequestInfoImpl(_wapp, null, null, null, _locator);
			final UiFactory uf = ((WebAppCtrl)_wapp).getUiFactory();
			for (Iterator it = imports.iterator(); it.hasNext();) {
				final String path = (String)it.next();
				try {
					final PageDefinition pd = uf.getPageDefinition(ri, path);
					if (pd == null)
						throw new UiException("Import page not found: "+path);
					pgdef.imports(pd);
				} catch (PotentialDeadLockException ex) {
					throw new UiException("Recursive import: "+path, ex);
				}
			}
		}

		//4. Processing the rest of processing instructions at the top level
		for (Iterator it = pis.iterator(); it.hasNext();)
			parse(pgdef, (ProcessingInstruction)it.next());

		//5. Processing from the root element
		final Element root = doc.getRootElement();
		if (root != null) parse(pgdef, pgdef, root);
		return pgdef;
	}
	private static Class locateClass(String clsnm) throws Exception {
		try {
			return Classes.forNameByThread(clsnm);
		} catch (ClassNotFoundException ex) {
			throw new ClassNotFoundException("Class not found: "+clsnm, ex);
		}
	}

	//-- derive to override --//
	/** returns locator for locating resources. */
	public Locator getLocator() {
		return _locator;
	}

	/** Parse processing instruction.
	 */
	private void parse(PageDefinition pgdef, ProcessingInstruction pi)
	throws Exception {
		final String target = pi.getTarget();
		final Map params = pi.parseData();
		if ("taglib".equals(target)) {
			final String uri = (String)params.remove("uri");
			final String prefix = (String)params.remove("prefix");
			if (!params.isEmpty())
				log.warning("Ignored unknown attributes: "+params.keySet()+", "+pi.getLocator());
			if (uri == null || prefix == null)
				throw new UiException("Both uri and prefix attribute are required, "+pi.getLocator());
			if (D.ON && log.debugable()) log.debug("taglib: prefix="+prefix+" uri="+uri);
			noEL("prefix", prefix, pi);
			noEL("uri", uri, pi); //not support EL (kind of chicken-egg issue)
			pgdef.addTaglib(new Taglib(prefix, uri));
		} else if ("init".equals(target)) {
			final List args = new LinkedList();
			for (int j = 0;; ++j) {
				final String arg = (String)params.remove("arg" + j);
				if (arg == null) break;
				args.add(arg);
			}

			final String clsnm = (String)params.remove("class");
			final String zsrc = (String)params.remove("zscript");

			if (!params.isEmpty())
				log.warning("Ignored unknown attributes: "+params.keySet()+", "+pi.getLocator());

			if (isEmpty(clsnm)) {
				if (isEmpty(zsrc))
					throw new UiException("Either the class or zscript attribute must be specified, "+pi.getLocator());

				final ZScript zs;
				if (zsrc.indexOf("${") >= 0) {
					zs = new ZScript(zsrc, null, getLocator());
				} else {
					final URL url = getLocator().getResource(zsrc);
					if (url == null) throw new FileNotFoundException("File not found: "+zsrc+", at "+pi.getLocator());
					zs = new ZScript(url, null);
				}

				pgdef.addInitiatorDefinition(
					new InitiatorDefinition(new ZScriptInitiator(zs), args));
			} else {
				if (!isEmpty(zsrc))
					throw new UiException("You cannot specify both class and zscript, "+pi.getLocator());

				pgdef.addInitiatorDefinition(
					clsnm.indexOf("${") >= 0 ? //class supports EL
						new InitiatorDefinition(clsnm, args):
						new InitiatorDefinition(locateClass(clsnm), args));
					//Note: we don't resolve the class name later because
					//no zscript run before init (and better performance)
			}
		} else if ("variable-resolver".equals(target)) {
			final String clsnm = (String)params.remove("class");
			if (isEmpty(clsnm))
				throw new UiException("The class attribute is required, "+pi.getLocator());
			if (!params.isEmpty())
				log.warning("Ignored unknown attributes: "+params.keySet()+", "+pi.getLocator());

			pgdef.addVariableResolverDefinition(
				clsnm.indexOf("${") >= 0 ? //class supports EL
					new VariableResolverDefinition(clsnm):
					new VariableResolverDefinition(locateClass(clsnm)));
		} else if ("component".equals(target)) { //declare a component
			final String name = (String)params.remove("name");
			if (isEmpty(name)) throw new UiException("name is required, "+pi.getLocator());
			noEL("name", name, pi); //note: macro-uri supports EL

			final String macroUri = (String)params.remove("macro-uri");
			final String extds = (String)params.remove("extends");
			final String clsnm = (String)params.remove("class");
			ComponentDefinition compdef;
			if (macroUri != null) {
				if (log.finerable()) log.finer("macro component definition: "+name);

				noEL("macro-uri", macroUri, pi);
					//no EL because pagedef must be loaded to resolve
					//the impl class before creating an instance of macro

				compdef = new ComponentDefinition(null, name, macroUri);
				pgdef.getLanguageDefinition().initMacroDefinition(compdef);
				if (!isEmpty(clsnm)) {
					noEL("class", clsnm, pi);
					compdef.setImplementationClass(clsnm);
						//Resolve later since might defined in zscript
				}
			} else if (extds != null) { //extends
				if (log.finerable()) log.finer("Override component definition: "+name);

				noEL("extends", extds, pi);
				final ComponentDefinition ref = pgdef.getLanguageDefinition()
					.getComponentDefinition(extds);
				if (ref.isMacro())
					throw new UiException("Unable to extend from a macro component, "+pi.getLocator());

				compdef = (ComponentDefinition)ref.clone(name);
				compdef.setLanguageDefinition(null);
				if (!isEmpty(clsnm)) {
					noEL("class", clsnm, pi);
					compdef.setImplementationClass(clsnm);
						//Resolve later since might defined in zscript
				}
			} else {
				if (log.finerable()) log.finer("Add component definition: name="+name);

				if (isEmpty(clsnm)) throw new UiException("class is required");
				noEL("class", clsnm, pi);

				compdef = new ComponentDefinition(null, name, (Class)null);
				compdef.setImplementationClass(clsnm);
					//Resolve later since might be defined in zscript
			}

			pgdef.addComponentDefinition(compdef);

			final String moldnm = (String)params.remove("mold-name");
			noEL("mold-name", moldnm, pi);
			final String molduri = (String)params.remove("mold-uri");
			if (!isEmpty(molduri))
				compdef.addMold(isEmpty(moldnm) ? "default": moldnm, molduri);
			for (Iterator e = params.entrySet().iterator(); e.hasNext();) {
				final Map.Entry me = (Map.Entry)e.next();
				compdef.addProperty(
					(String)me.getKey(), (String)me.getValue(), null);
			}
		} else if ("link".equals(target) || "meta".equals(target)) { //declare a header element
			pgdef.addHeader(new Header(target, params));
		} else if ("page".equals(target)) {
			log.warning("Ignored page directive at "+pi.getLocator());
		} else if ("import".equals(target)) { //import
			throw new UiException("The import directive can be used only at the top level, "+pi);
		} else {
			log.warning("Unknown processing instruction: "+target);
		}
	}
	private static void noEL(String nm, String val, Item item)
	throws UiException {
		if (val != null && val.indexOf("${") >= 0)
			throw new UiException(nm+" does not support EL expressions, "+item.getLocator());
	}

	/** Parses the specified elements.
	 */
	private void parse(PageDefinition pgdef, InstanceDefinition parent,
	Collection items)
	throws Exception {
		for (Iterator it = items.iterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof Element) {
				parse(pgdef, parent, (Element)o);
			} else if (o instanceof ProcessingInstruction) {
				parse(pgdef, (ProcessingInstruction)o);
			} else if ((o instanceof Text) || (o instanceof CData)) {
				String label = ((Item)o).getText();

				final LanguageDefinition parentlang =
					getLanguageDefinition(pgdef, parent);
				if (!parentlang.isRawLabel())
					label = label.trim();

				if (label.trim().length() > 0) //consider as a label
					parentlang.newLabelDefinition(parent, label);
			}
		}
	}
	private static final
	LanguageDefinition getLanguageDefinition(PageDefinition pgdef,
	InstanceDefinition instdef) {
		for (; instdef != null; instdef = instdef.getParent()) {
			LanguageDefinition langdef = instdef.getLanguageDefinition();
			if (langdef != null)
				return langdef;
		}
		return pgdef.getLanguageDefinition();
	}

	/** Parse an component definition specified in the given element.
	 */
	private void parse(PageDefinition pgdef,
	InstanceDefinition parent, Element el) throws Exception {
		final String nm = el.getLocalName();
		final Namespace ns = el.getNamespace();
		final LanguageDefinition langdef = pgdef.getLanguageDefinition();
		if ("zscript".equals(nm) && isZkElement(langdef, nm, ns)) {
			//if (!el.getElements().isEmpty())
			//	throw new UiException("Child elements are not allowed for the zscript element, "+el.getLocator());

			if (el.getAttributeItem("forEach") != null)
				throw new UiException("forEach not applicable to zscript, "+el.getLocator());

			final String ifc = el.getAttribute("if"),
				unless = el.getAttribute("unless"),
				zsrc = el.getAttribute("src");

			final Condition cond = ConditionImpl.getInstance(ifc, unless);
			if (!isEmpty(zsrc)) {
				final ZScript zs;
				if (zsrc.indexOf("${") >= 0) {
					zs = new ZScript(zsrc, cond, getLocator());
				} else {
					final URL url = getLocator().getResource(zsrc);
					if (url == null) throw new FileNotFoundException("File not found: "+zsrc+", at "+el.getLocator());
					zs = new ZScript(url, cond);
				}

				parent.appendChild(zs);
			}

			final String script = el.getText(true);
			if (!isEmpty(script))
				parent.appendChild(new ZScript(script, cond));
		} else if ("attribute".equals(nm) && isZkElement(langdef, nm, ns)) {
			//if (!el.getElements().isEmpty())
			//	throw new UiException("Child elements are not allowed for the attribute element, "+el.getLocator());

			if (el.getAttributeItem("forEach") != null)
				throw new UiException("forEach not applicable to attribute, "+el.getLocator());

			//FUTURE: handling the namespace of if and unless
			final String attnm = IDOMs.getRequiredAttributeValue(el, "name");
			final String attval = el.getText(false); //don't trim!!
			if (!isEmpty(attval)) {
				addAttribute(parent, null, attnm, attval,
					ConditionImpl.getInstance(
						el.getAttribute("if"), el.getAttribute("unless")));
			}
		} else if ("custom-attributes".equals(nm) && isZkElement(langdef, nm, ns)) {
			//if (!el.getElements().isEmpty())
			//	throw new UiException("Child elements are not allowed for the custom-attributes element, "+el.getLocator());
			if (parent instanceof PageDefinition)
				throw new UiException("custom-attributes must be used under a component, "+el.getLocator());

			String ifc = null, unless = null, scope = null;
			final Map attrs = new HashMap();
			for (Iterator it = el.getAttributeItems().iterator();
			it.hasNext();) {
				final Attribute attr = (Attribute)it.next();
				final String attnm = attr.getLocalName();
				final String attval = attr.getValue();
				if ("if".equals(attnm)) {
					ifc = attval;
				} else if ("unless".equals(attnm)) {
					unless = attval;
				} else if ("scope".equals(attnm)) {
					scope = attval;
				} else if ("forEach".equals(attnm)) {
					throw new UiException("forEach not applicable to custom-attributes, "+el.getLocator());
				} else {
					attrs.put(attnm, attval);
				}
			}

			if (!attrs.isEmpty())
				parent.addCustomAttributes(
					new CustomAttributes(attrs, scope, ConditionImpl.getInstance(ifc, unless)));
		} else {
			if (D.ON && log.debugable()) log.debug("component: "+nm+", ns:"+ns);

			final InstanceDefinition instdef;
			if ("zk".equals(nm) && isZkElement(langdef, nm, ns)) {
				instdef = new InstanceDefinition(
					parent, ComponentDefinition.ZK, "zk"); 
			} else {
				final String pref = ns != null ? ns.getPrefix(): "";
				final String uri = ns != null ? ns.getURI(): "";
				if (LanguageDefinition.ZK_NAMESPACE.equals(uri))
					throw new UiException("Unknown ZK component: "+el);

				final LanguageDefinition complangdef;
				if (isDefault(langdef, pref, uri)) {
					complangdef = langdef;
				} else {
					complangdef = LanguageDefinition.lookup(uri);
				}

				ComponentDefinition compdef =
					pgdef.getComponentDefinitionMap().get(nm);
				if (compdef != null) {
					instdef = new InstanceDefinition(parent, compdef);
				} else if (complangdef.hasComponentDefinition(nm)) {
					compdef = complangdef.getComponentDefinition(nm);
					instdef = new InstanceDefinition(parent, compdef);
				} else {
					compdef = complangdef.getDynamicTagDefinition();
					if (compdef == null)
						throw new DefinitionNotFoundException("Component definition not found: "+nm+" in "+complangdef);
					instdef = new InstanceDefinition(parent, compdef, nm);
				}

				//process use first because addProperty needs it
				final String use = el.getAttribute("use");
				if (!isEmpty(use)) {
					noEL("use", use, el);
					instdef.setImplementationClass(use);
						//Resolve later since might defined in zscript
				}
			}

			String ifc = null, unless = null,
				forEach = null, forEachBegin = null, forEachEnd = null;
			for (Iterator it = el.getAttributeItems().iterator();
			it.hasNext();) {
				final Attribute attr = (Attribute)it.next();
				final String attnm = attr.getLocalName();
				final String attval = attr.getValue();
				if ("if".equals(attnm)) {
					ifc = attval;
				} else if ("unless".equals(attnm)) {
					unless = attval;
				} else if ("forEach".equals(attnm)) {
					forEach = attval;
				} else if ("forEachBegin".equals(attnm)) {
					forEachBegin = attval;
				} else if ("forEachEnd".equals(attnm)) {
					forEachEnd = attval;
				} else if (!"use".equals(attnm)) {
					final Namespace attns = attr.getNamespace();
					final String attpref = attns != null ? attns.getPrefix(): "";
					final String attruri = attns != null ? attns.getURI(): "";
					if (!"xmlns".equals(attpref)
					&& !("xmlns".equals(attnm) && "".equals(attpref))
					&& !"http://www.w3.org/2001/XMLSchema-instance".equals(attruri))
						addAttribute(instdef, attns, attnm, attval, null);
				}
			}
			instdef.setCondition(ConditionImpl.getInstance(ifc, unless));
			instdef.setForEach(forEach, forEachBegin, forEachEnd);
			parse(pgdef, instdef, el.getChildren()); //recursive
		}
	}
	/** Whether the name space belongs to the default language. */
	private static final
	boolean isDefault(LanguageDefinition langdef, String pref, String uri) {
		return ("".equals(pref) && "".equals(uri))
			|| langdef.getNamespace().equals(uri);
	}
	/** Whether a string is null or empty. */
	private static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
	/** Returns whether the element is conflict with the language definition.
	 */
	private static final boolean
	isZkElement(LanguageDefinition langdef, String nm, Namespace ns) {
		final String pref = ns != null ? ns.getPrefix(): "",
			uri = ns != null ? ns.getURI(): "";
		if (isDefault(langdef, pref, uri))
			return !langdef.hasComponentDefinition(nm);
		return LanguageDefinition.ZK_NAMESPACE.equals(uri);
	}

	/** Parse an attribute and adds it to the definition.
	 */
	private void addAttribute(InstanceDefinition instdef, Namespace attrns,
	String name, String value, Condition cond) throws Exception {
		if (Events.isValid(name)) {
			boolean bZkAttr = attrns == null;
			if (!bZkAttr) {
				final String pref = attrns.getPrefix(), uri = attrns.getURI();
				final LanguageDefinition langdef = instdef.getLanguageDefinition();
				if (langdef == null)
					bZkAttr = true;
				else if (isDefault(langdef, pref, uri))
					bZkAttr = !langdef.isDynamicReservedAttributes("[event]");
				else
					bZkAttr = LanguageDefinition.ZK_NAMESPACE.equals(uri);
			}
			if (bZkAttr) {
				instdef.addEventHandler(name, value, cond);
				return; //done
			}
		}
		instdef.addProperty(name, value, cond);
	}
}
