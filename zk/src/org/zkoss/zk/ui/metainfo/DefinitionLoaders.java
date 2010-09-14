/* DefinitionLoaders.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 29 21:57:08     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.net.URL;

import org.zkoss.lang.Library;
import org.zkoss.lang.Classes;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.resource.XMLResourcesLocator;
import org.zkoss.idom.Document;
import org.zkoss.idom.Element;
import org.zkoss.idom.Attribute;
import org.zkoss.idom.ProcessingInstruction;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.util.IDOMs;
import org.zkoss.xel.taglib.Taglib;
import org.zkoss.html.JavaScript;
import org.zkoss.html.StyleSheet;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.metainfo.impl.*;
import org.zkoss.zk.ui.ext.Macro;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.sys.ConfigParser;
import org.zkoss.zk.ui.sys.PageRenderer;
import org.zkoss.zk.ui.impl.Utils;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.device.Device;

/**
 * Utilities to load language definitions.
 *
 * @author tomyeh
 */
public class DefinitionLoaders {
	private static final Log log = Log.lookup(DefinitionLoaders.class);

	/** List<Object[Locator, URL]> */
	private static List<Object[]> _addons;
	/** A map of (String ext, String lang). */
	private static Map<String, String> _exts;
	private static boolean _loaded, _loading;

	//CONSIDER:
	//Sotre language definitions per WebApp, since diff app may add its
	//own definitions thru WEB-INF's lang-addon, or a JAR in WEB-INF/lib.
	//
	//CONSEQUENCE:
	//Other Web app is affected by another in the current implementation
	//
	//WORKAROUND:
	//Copy ZK libraries into WEB-INF/lib
	//
	//DIFFICULTY TO SUPPORT
	//To support it we have to pass WebApp around. It is a challenge for
	//1) a working thread (other than servlet/event thread);
	//2) deserialize LanguageDefinition (and maybe ComponentDefinition)

	/** Adds a language addon.
	 * It is usually used when an application want to load additional addon
	 * dynamically. For example, if you want to load an addon only if
	 * the professional edition is ready, you can register a
	 * {@link org.zkoss.zk.ui.util.WebAppInit} listener.
	 */
	public static void addAddon(Locator locator, URL url) {
		if (locator == null || url == null)
			throw new IllegalArgumentException("null");

		if (_loaded) {
			loadAddon(locator, url);
		} else {
			if (_addons == null)
				_addons = new LinkedList<Object[]>();
			_addons.add(new Object[] {locator, url});
		}
	}
	/** Associates an extension to a language.
	 *
	 * @param lang the language name. It cannot be null.
	 * @param ext the extension, e.g., "svg". It cannot be null.
	 * @since 3.0.0
	 */
	public static final void addExtension(String ext, String lang) {
		if (_loaded) {
			LanguageDefinition.addExtension(ext, lang);
		} else {
			if (lang == null || ext == null)
				throw new IllegalArgumentException("null");
			if (_exts == null)
				_exts = new HashMap<String, String>();
			_exts.put(ext, lang);
		}
	}

	/** Loads all config.xml, lang.xml and lang-addon.xml found in
	 * the /metainfo/zk path.
	 *
	 * <p>Remember to call {@link #addAddon}, if necessary, before
	 * calling this method.
	 */
	/*package*/ static void load() throws java.io.IOException {
		if (!_loaded) {
			synchronized (DefinitionLoaders.class) {
				if (!_loaded && !_loading) {
					try {
						_loading = true; //prevent re-entry for the same thread
						load0();
					} finally {
						_loaded = true; //only once
						_loading = false;
					}
				}
			}
		}
	}
	private static void load0() throws java.io.IOException {
		final XMLResourcesLocator locator = Utils.getXMLResourcesLocator();

		//1. parse config.xml
		final ConfigParser parser = new ConfigParser();
		parser.parseConfigXml(null); //only system default configs

		//2. process lang.xml (no particular dependency)
		for (Enumeration en = locator.getResources("metainfo/zk/lang.xml");
		en.hasMoreElements();) {
			final URL url = (URL)en.nextElement();
			if (log.debugable()) log.debug("Loading "+url);
			try {
				final Document doc = new SAXBuilder(true, false, true).build(url);
				if (ConfigParser.checkVersion(url, doc, true))
					parseLang(doc, locator, url, false);
			} catch (Exception ex) {
				log.error("Failed to load "+url, ex);
				throw UiException.Aide.wrap(ex, "Failed to load "+url);
					//abort since it is hardly to work then
			}
		}

		//3. process lang-addon.xml (with dependency)
		final List xmls = locator.getDependentXMLResources(
			"metainfo/zk/lang-addon.xml", "addon-name", "depends");
		for (Iterator it = xmls.iterator(); it.hasNext();) {
			final XMLResourcesLocator.Resource res =
				(XMLResourcesLocator.Resource)it.next();
			try {
				if (ConfigParser.checkVersion(res.url, res.document, true))
					parseLang(res.document, locator, res.url, true);
			} catch (Exception ex) {
				log.realCauseBriefly("Failed to load "+res.url, ex);
				//keep running
			}
		}

		//4. process other addon (from addAddon)
		if (_addons != null) {
			for (Iterator it = _addons.iterator(); it.hasNext();) {
				final Object[] p = (Object[])it.next();
				loadAddon((Locator)p[0], (URL)p[1]);
			}
			_addons = null; //free memory
		}

		//5. process the extension
		if (_exts != null) {
			for (Map.Entry<String, String> me: _exts.entrySet()) {
				final String ext = me.getKey();
				final String lang = me.getValue();
				try {
					LanguageDefinition.addExtension(ext, lang);
				} catch (DefinitionNotFoundException ex) {
					log.warning("Extension "+ext+" ignored since language "+lang+" not found");
				}
			}
			_exts = null;
		}
	}
	/** Loads a language addon.
	 */
	private static void loadAddon(Locator locator, URL url) {
		try {
			parseLang(
				new SAXBuilder(true, false, true).build(url), locator, url, true);
		} catch (Exception ex) {
			log.error("Failed to load addon: "+url, ex);
			//keep running
		}
	}

	private static
	void parseLang(Document doc, Locator locator, URL url, boolean addon)
	throws Exception {
		final Element root = doc.getRootElement();
		final String lang = IDOMs.getRequiredElementValue(root, "language-name");
		final LanguageDefinition langdef;
		final Device device;
		if (addon) {
			if (log.debugable()) log.debug("Addon language to "+lang+" from "+root.getElementValue("addon-name", true));
			langdef = LanguageDefinition.lookup(lang);
			device = Devices.getDevice(langdef.getDeviceType());

			if (root.getElement("case-insensitive") != null)
				throw new UiException("case-insensitive not allowed in addon");
		} else {
			final String ns =
				IDOMs.getRequiredElementValue(root, "namespace");
			final String deviceType =
				IDOMs.getRequiredElementValue(root, "device-type");

			//if (log.debugable()) log.debug("Load language: "+lang+", "+ns);

			PageRenderer pageRenderer = (PageRenderer)
				locateClass(IDOMs.getRequiredElementValue(root, "renderer-class"), PageRenderer.class)
				.newInstance();

			final List<String> exts = parseExtensions(root);
			if (exts.isEmpty())
				throw new UiException("The extension must be specified for "+lang);

			String ignoreCase = root.getElementValue("case-insensitive", true);
			String bNative = root.getElementValue("native-namespace", true);

			langdef = new LanguageDefinition(
				deviceType, lang, ns, exts, pageRenderer,
				"true".equals(ignoreCase), "true".equals(bNative), locator);
			device = Devices.getDevice(deviceType);
		}

		parsePI(langdef, doc);
		parseLabelTemplate(langdef, root);
		parseDynamicTag(langdef, root);
		parseMacroTemplate(langdef, root);
		parseNativeTemplate(langdef, root);

		for (Element el: root.getElements("library-property")) {
			final String nm = IDOMs.getRequiredElementValue(el, "name");
			final String val = IDOMs.getRequiredElementValue(el, "value");
			Library.setProperty(nm, val);
		}
		for (Iterator it = root.getElements("system-property").iterator();
		it.hasNext();) {
			final Element el = (Element)it.next();
			final String nm = IDOMs.getRequiredElementValue(el, "name");
			final String val = IDOMs.getRequiredElementValue(el, "value");
			System.setProperty(nm, val);
		}

		for (Iterator it = root.getElements("javascript").iterator();
		it.hasNext();) {
			final Element el = (Element)it.next();
			String src = el.getAttributeValue("src"),
				pkg = el.getAttributeValue("package");
			final boolean merge = "true".equals(el.getAttributeValue("merge"));
			final boolean ondemand = "true".equals(el.getAttributeValue("ondemand"));
			if (pkg != null) {
				if (src != null)
					log.warning("The src attribute ignored because package is specified, "+el.getLocator());
				if (!ondemand && !merge) {
					src = "~." + device.packageToPath(pkg);
					pkg = null;
				}
			}

			final String ctn = el.getText(true);
			final JavaScript js;
			if (pkg != null && pkg.length() > 0) {
				if (ondemand) {
					langdef.removeJavaScript("~." + device.packageToPath(pkg));
					langdef.removeMergeJavaScriptPackage(pkg);
				} else {
					langdef.addMergeJavaScriptPackage(pkg);
				}
				continue; //TODO
			} else if (src != null && src.length() > 0) {
				if (ctn != null && ctn.length() > 0)
					throw new UiException("You cannot specify the content if the src attribute is specified, "+el.getLocator());
				final String charset = el.getAttributeValue("charset");
				js = new JavaScript(src, charset);
			} else if (ctn != null && ctn.length() > 0) {
				js = new JavaScript(ctn);
			} else {
				log.warning("Ignored: none of the src or package attribute, or the content specified, "+el.getLocator());
				continue;
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
				ss = new StyleSheet(href, el.getAttributeValue("type"), el.getAttributeValue("media"), false);
			} else if (ctn != null && ctn.length() > 0) {
				ss = new StyleSheet(ctn, el.getAttributeValue("type"), el.getAttributeValue("media"), true);
			} else {
				throw new UiException("You must specify either the href attribute or the content, "+el.getLocator());
			}
			langdef.addStyleSheet(ss);
		}

		for (Iterator it = root.getElements("zscript").iterator();
		it.hasNext();) {
			final Element el = (Element)it.next();
			final String zslang;
			final Attribute attr = el.getAttributeItem("language");
			if (attr == null) {
				zslang = "Java";
			} else {
				zslang = attr.getValue();
				if (zslang == null || zslang.length() == 0)
					throw new UiException("The language attribute cannot be empty, "+attr.getLocator());
			}
			final String s = el.getText(true);
			final String eachTime = el.getAttributeValue("each-time");
			if ("true".equals(eachTime))
				langdef.addEachTimeScript(zslang, s);
			else
				langdef.addInitScript(zslang, s);
		}

		for (Iterator it = root.getElements("component").iterator();
		it.hasNext();) {
			final Element el = (Element)it.next();
			final String name =
				IDOMs.getRequiredElementValue(el, "component-name");

			String clsnm = el.getElementValue("component-class", true);
			Class<? extends Component> cls = null;
			if (clsnm != null) {
				if (clsnm.length() > 0) {
					noEL("component-class", clsnm, el);
					try {
						cls = locateClass(clsnm, Component.class);
					} catch (Throwable ex) { //Feature 1873426
						log.warning("Component "+name+" ignored. Reason: unable to load "+clsnm+" due to "
							+ex.getClass().getName()+": "+ex.getMessage()
							+(ex instanceof NoClassDefFoundError?"":"\n"+el.getLocator()));
						log.debug(ex);
						//keep processing (Feature 2060367)
					}
				} else {
					clsnm = null;
				}
			}

			final String macroURI = el.getElementValue("macro-uri", true);
			final ComponentDefinitionImpl compdef;
			if (macroURI != null && macroURI.length() != 0) {
				if (log.finerable()) log.finer("macro component definition: "+name);

				final String inline = el.getElementValue("inline", true);
				compdef = (ComponentDefinitionImpl)
					langdef.getMacroDefinition(
						name, macroURI, "true".equals(inline), null);

				if (cls != null)
					compdef.setImplementationClass(cls);
				else if (clsnm != null)
					compdef.setImplementationClass(clsnm);

				compdef.setDeclarationURL(url);
				langdef.addComponentDefinition(compdef);
			} else if (el.getElement("extends") != null) { //extends
				final String extnm = el.getElementValue("extends", true);
				if (log.finerable()) log.finer("Extends component definition, "+name+", from "+extnm);
				final ComponentDefinition ref = langdef.getComponentDefinitionIfAny(extnm);
				if (ref == null) {
					log.warning("Component "+name+" ignored. Reason: extends a non-existent component "+extnm+".\n"+el.getLocator());
						//not throw exception since the derived component might be
						//ignored due to class-not-found
					continue;
				}

				if (ref.isMacro())
					throw new UiException("Unable to extend from a macro component, "+el.getLocator());

				if (extnm.equals(name)) {
					compdef = (ComponentDefinitionImpl)ref;
				} else {
					compdef = (ComponentDefinitionImpl)
						ref.clone(ref.getLanguageDefinition(), name);
					compdef.setDeclarationURL(url);
				}

				if (cls != null)
					compdef.setImplementationClass(cls);
				else if (clsnm != null)
					compdef.setImplementationClass(clsnm);

				langdef.addComponentDefinition(compdef);
					//Note: setImplementationClass before addComponentDefinition
			} else {
				if (log.finerable()) log.finer("Add component definition: name="+name);

				if (cls == null && clsnm == null)
					throw new UiException("component-class is required, "+el.getLocator());
				compdef = cls != null ?
					new ComponentDefinitionImpl(langdef, null, name, cls):
					new ComponentDefinitionImpl(langdef, null, name, clsnm);
				compdef.setDeclarationURL(url);
				langdef.addComponentDefinition(compdef);
			}

			String s = el.getElementValue("text-as", true);
			if (s != null) { //empty means cleanup (for overriding)
				noEL("text-as", s, el);
				compdef.setTextAs(s);
			}

			s = el.getElementValue("preserve-blank", true);
			if (s != null && !"false".equals(s))
				compdef.setBlankPreserved(true);

			String wgtnm = el.getElementValue("widget-class", true);
			WidgetDefinition wgtdef = null;
			if (wgtnm != null) {
				if (!withEL(wgtnm))
					wgtdef = getWidgetDefinition(langdef, compdef, wgtnm);
				compdef.setDefaultWidgetClass(wgtnm);
			}

			s = el.getElementValue("component-apply", true);
			if (s == null) s = el.getElementValue("apply", true); //backward-compatible
			compdef.setApply(s);

			for (Iterator i = el.getElements("mold").iterator(); i.hasNext();) {
				final Element e = (Element)i.next();
				final String nm = IDOMs.getRequiredElementValue(e, "mold-name");
				final String moldURI = e.getElementValue("mold-uri", true);
				String cssURI = e.getElementValue("css-uri", true);
				final String wn = e.getElementValue("widget-class", true);
				noEL("mold-uri", moldURI, e); //5.0 limitation
				noEL("css-uri", cssURI, e);

				compdef.addMold(nm, wn != null ? wn: wgtnm);

				WidgetDefinition wd = wn == null ? wgtdef:
					withEL(wn) ? null: getWidgetDefinition(langdef, compdef, wn);
				if (moldURI != null) {
					if (wd != null)
						wd.addMold(nm, moldURI);
					else
						log.error("Mold "+nm+" for "+name+" ignored because "+
							((wn != null && withEL(wn)) || (wgtnm != null && withEL(wgtnm)) ?
								"widget-class contains EL expressions":"widget-class is required")
							+", "+e.getLocator());
				}

				if (cssURI != null && cssURI.length() > 0) {
					final char cc = cssURI.charAt(0);
					if (cc != '/' && cc != '~') {
						String n = wn != null ? wn: wgtnm;
						if (!withEL(n)) {
							int k = n.lastIndexOf('.');
							cssURI = "~." + device.toAbsolutePath(
								n.substring(0, k).replace('.', '/') + '/' + cssURI);
						} else {
							log.error("Absolute path required for cssURI, since the widget class contains EL expressions, "+e.getLocator());
						}
					}
					langdef.addCSSURI(cssURI);
				}
			}

			for (Iterator e = parseCustAttrs(el).entrySet().iterator(); e.hasNext();) {
				final Map.Entry me = (Map.Entry)e.next();
				compdef.addCustomAttribute((String)me.getKey(), (String)me.getValue());
			}

			for (Iterator e = parseProps(el).entrySet().iterator(); e.hasNext();) {
				final Map.Entry me = (Map.Entry)e.next();
				compdef.addProperty((String)me.getKey(), (String)me.getValue());
			}

			parseAnnots(compdef, el);
		}
	}
	private static WidgetDefinition getWidgetDefinition(
	LanguageDefinition langdef, ComponentDefinition compdef, String wgtnm) {
		WidgetDefinition wgtdef = langdef.getWidgetDefinitionIfAny(wgtnm);
		if (wgtdef != null)
			return wgtdef;

		wgtdef = new WidgetDefinitionImpl(wgtnm, compdef.isBlankPreserved());
		langdef.addWidgetDefinition(wgtdef);
		return wgtdef;
	}
	@SuppressWarnings("unchecked")
	private static <T> Class<? extends T> locateClass(String clsnm, Class<?>... clses)
	throws Exception {
		final Class<?> c = Classes.forNameByThread(clsnm);
		if (clses != null)
			for (Class<?> cls: clses)
				if (!cls.isAssignableFrom(c))
					throw new UiException(c + " must implement "+cls);
		return (Class<? extends T>)c;
	}
	private static void noEL(String nm, String val, Element el)
	throws UiException {
		if (withEL(val))
			throw new UiException(nm+" does not support EL expressions, "+el.getLocator());
	}
	private static boolean withEL(String val) {
		return val != null && val.indexOf("${") >= 0;
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
					throw new UiException("Both uri and prefix attribute are required, "+pi.getLocator());
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
			final Class<? extends Component> cls =
				locateClass(IDOMs.getRequiredElementValue(el, "macro-class"),
					Component.class, Macro.class);
			langdef.setMacroTemplate(cls);
		}
	}
	private static
	void parseNativeTemplate(LanguageDefinition langdef, Element el)
	throws Exception {
		el = el.getElement("native-template");
		if (el != null) {
			final Class<? extends Component> cls =
				locateClass(IDOMs.getRequiredElementValue(el, "native-class"),
					Component.class, Native.class);
			langdef.setNativeTemplate(cls);
		}
	}
	private static
	void parseDynamicTag(LanguageDefinition langdef, Element el)
	throws ClassNotFoundException {
		el = el.getElement("dynamic-tag");
		if (el != null) {
			final String compnm =
				IDOMs.getRequiredElementValue(el, "component-name");
			final Set<String> reservedAttrs = new HashSet<String>(8);
			for (Element e: el.getElements("reserved-attribute"))
				reservedAttrs.add(e.getText(true));
			langdef.setDynamicTagInfo(compnm, reservedAttrs);
		}
		//if (log.finerable()) log.finer(el);
	}
	private static List<String> parseExtensions(Element elm) {
		final List<String> exts = new LinkedList<String>();
		for (Element el: elm.getElements("extension")) {
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
	private static Map<String, String> parseProps(Element elm) {
		return IDOMs.parseParams(elm, "property", "property-name", "property-value");
	}
	private static Map<String, String> parseCustAttrs(Element elm) {
		return IDOMs.parseParams(elm, "custom-attribute", "attribute-name", "attribute-value");
	}
	private static Map<String, String> parseAttrs(Element elm) {
		return IDOMs.parseParams(elm, "attribute", "attribute-name", "attribute-value");
	}

	private static void parseAnnots(ComponentDefinitionImpl compdef, Element top) {
		for (Element el: top.getElements("annotation")) {
			final String annotName = IDOMs.getRequiredElementValue(el, "annotation-name");
			final Map<String, String> annotAttrs = parseAttrs(el);
			final String prop = el.getElementValue("property-name", true);
			if (prop == null || prop.length() == 0)
				compdef.addAnnotation(annotName, annotAttrs);
			else
				compdef.addAnnotation(prop, annotName, annotAttrs);
		}
	}
	/** Configures an integer. */
	private static Integer parseInteger(Element el, String subnm,
	boolean positiveOnly) throws UiException {
		//Warning instead of exception since config.xml is embedded in jar, so
		//better not to stop the process
		String val = el.getElementValue(subnm, true);
		if (val != null && val.length() > 0) {
			try { 
				final int v = Integer.parseInt(val);
				if (!positiveOnly || v > 0)
					return new Integer(v);
				log.warning("Ignored: the "+subnm+" element must be a positive number, not "+val+", at "+el.getLocator());
			} catch (NumberFormatException ex) { //eat
				log.warning("Ignored: the "+subnm+" element must be a number, not "+val+", at "+el.getLocator());
			}
		}
		return null;
	}
}
