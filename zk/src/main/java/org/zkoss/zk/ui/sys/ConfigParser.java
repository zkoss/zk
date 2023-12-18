/* ConfigParser.java

	Purpose:
		
	Description:
		
	History:
		Sun Mar 26 18:09:10     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import static org.zkoss.lang.Generics.cast;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.idom.Attribute;
import org.zkoss.idom.Document;
import org.zkoss.idom.Element;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.util.IDOMs;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.mesg.MCommon;
import org.zkoss.util.Cache;
import org.zkoss.util.IllegalSyntaxException;
import org.zkoss.util.Pair;
import org.zkoss.util.Utils;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.resource.XMLResourcesLocator;
import org.zkoss.web.fn.ThemeFns;
import org.zkoss.web.theme.ThemeRegistry;
import org.zkoss.web.theme.ThemeResolver;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.zk.au.AuDecoder;
import org.zkoss.zk.au.AuWriter;
import org.zkoss.zk.au.AuWriters;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.scripting.Interpreters;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.metainfo.DefinitionLoaders;
import org.zkoss.zk.ui.util.CharsetFinder;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.DataHandlerInfo;
import org.zkoss.zk.ui.util.ThemeProvider;
import org.zkoss.zk.ui.util.ThemeURIHandler;
import org.zkoss.zk.ui.util.URIInfo;

/**
 * Used to parse WEB-INF/zk.xml, metainfo/zk/zk.xml 
 * and metainfo/zk/config.xml into {@link Configuration}.
 *
 * @author tomyeh
 */
public class ConfigParser {
	private static final Logger log = LoggerFactory.getLogger(ConfigParser.class);

	/** The number of segments in a version.
	 */
	private static final int MAX_VERSION_SEGMENT = 4;
	private static volatile int[] _zkver;
	private static volatile List<org.zkoss.zk.ui.util.ConfigParser> _parsers;
	// Map<int, boolean>: whether an instance of Configuration
	private static final Map<Integer, Boolean> _syscfgLoadedConfigs = new HashMap<Integer, Boolean>(4);
	private static boolean _syscfgLoaded;

	/** Checks and returns whether the loaded document's version is correct.
	 * It is the same as checkVersion(url, doc, false).
	 * @since 3.5.0
	 */
	public static boolean checkVersion(URL url, Document doc) throws Exception {
		return checkVersion(url, doc, false);
	}

	/** Checks and returns whether the loaded document's version is correct.
	 * @param zk5required whether ZK 5 or later is required.
	 * If true and zk-version is earlier than 5, doc will be ignored
	 * (and false is returned).
	 * @since 5.0.0
	 */
	public static boolean checkVersion(URL url, Document doc, boolean zk5required) throws Exception {
		final Element el = doc.getRootElement().getElement("version");
		if (el == null)
			return true; //version is optional (3.0.5)

		if (_zkver == null)
			_zkver = Utils.parseVersion(org.zkoss.zk.Version.UID);

		String s = el.getElementValue("zk-version", true);
		if (s != null) {
			final int[] reqzkver = Utils.parseVersion(s);
			if (Utils.compareVersion(_zkver, reqzkver) < 0) {
				log.info("Ignore " + url + "\nCause: ZK version must be " + s + " or later, not " + org.zkoss.zk.Version.UID);
				return false;
			}
			if (zk5required && reqzkver.length > 0 && reqzkver[0] < 5) {
				log.info("Ingore " + url + "\nCause: version " + s + " not supported");
				return false;
			}
		}

		final String clsnm = el.getElementValue("version-class", true);
		if (clsnm == null) {
			return true; //version is optional 3.0.5
		}

		if (clsnm.length() == 0)
			log.warn("Ignored: empty version-class, " + el.getLocator());

		final String uid = IDOMs.getRequiredElementValue(el, "version-uid");
		final Class cls = Classes.forNameByThread(clsnm);
		final Field fld = cls.getField("UID");
		final String uidInClass = (String) fld.get(null);
		if (!uid.equals(uidInClass)) {
			log.info("Ignore " + url + "\nCause: version not matched; expected=" + uidInClass + ", xml=" + uid);
			return false;
		}

		return true; //matched
	}

	/** Used to provide backward compatibility to 2.3.0's richlet definition. */
	private int _richletnm;

	/** Parses metainfo/zk/config.xml placed in class-path.
	 *
	 * <p>Note: the application-independent configurations (a.k.a.,
	 * the system default configurations) are loaded only once,
	 * no matter how many times this method is called.
	 *
	 * @param config the object to store configurations.
	 * If null, only the application-independent configurations are parsed.
	 * @since 3.5.0
	 */
	public void parseConfigXml(Configuration config) {
		boolean syscfgLoaded;
		boolean syscfgLoadedConfig;
		synchronized (ConfigParser.class) {
			syscfgLoaded = _syscfgLoaded;
			_syscfgLoaded = true;
			syscfgLoadedConfig = config != null ? _syscfgLoadedConfigs.put(new Integer(System.identityHashCode(config)),
					//chance of two instances with same code is almost zero
					Boolean.TRUE) != null : syscfgLoaded;
		}
		if (!syscfgLoaded)
			log.info("Loading system default");
		else if (config == null)
			return; //nothing to do

		try {
			final XMLResourcesLocator locator = org.zkoss.zk.ui.impl.Utils.getXMLResourcesLocator();
			final List<XMLResourcesLocator.Resource> xmls = locator.getDependentXMLResources("metainfo/zk/config.xml",
					"config-name", "depends");
			for (XMLResourcesLocator.Resource res : xmls) {
				if (log.isDebugEnabled())
					log.debug("Loading " + res.url);
				try {
					if (checkVersion(res.url, res.document)) {
						final Element el = res.document.getRootElement();
						if (!syscfgLoaded) {
							parseSubZScriptConfig(el);
							parseSubDeviceConfig(el);
						}
						if (!syscfgLoadedConfig) { //config not null
							parseSubSystemConfig(config, el);
							parseSubClientConfig(config, el);
						}
						if (!syscfgLoaded) {
							parseProperties(el);
							parseLangConfigs(locator, el);
						}

						if (config != null) {
							parseListeners(config, el);
							parsePreferences(config, el);
							//F80 - store subtree's binder annotation count
							if (config.getBinderInitAttribute() == null
									&& "zkbind".equals(el.getElement("config-name").getText()))
								parseBinderConfig(config, el);
						}
					}
				} catch (Exception ex) {
					throw UiException.Aide.wrap(ex, "Failed to load " + res.url);
					//abort since it is hardly to work then
				}
			}
		} catch (java.io.IOException ex) {
			throw UiException.Aide.wrap(ex); //abort
		}
	}

	private static void parseSubZScriptConfig(Element root) {
		for (Iterator it = root.getElements("zscript-config").iterator(); it.hasNext();) {
			final Element el = (Element) it.next();
			Interpreters.add(el);
			//Note: zscript-config is applied to the whole system, not just langdef
		}
	}

	private static void parseSubDeviceConfig(Element root) {
		for (Iterator it = root.getElements("device-config").iterator(); it.hasNext();) {
			final Element el = (Element) it.next();
			Devices.add(el);
		}
	}

	private static void parseSubSystemConfig(Configuration config, Element root) throws Exception {
		for (Element el : root.getElements("system-config")) {
			if (config != null) {
				parseSystemConfig(config, el);
			} else {
				Class<org.zkoss.zk.ui.util.ConfigParser> cls = parseClass(el, "au-writer-class", AuWriter.class);
				if (cls != null)
					AuWriters.setImplementationClass(cls);
				cls = parseClass(el, "config-parser-class", org.zkoss.zk.ui.util.ConfigParser.class);
				if (cls != null) {
					if (_parsers == null)
						_parsers = new LinkedList<org.zkoss.zk.ui.util.ConfigParser>();
					_parsers.add(cls.newInstance());
				}
			}
		}
	}

	/** Unlike other private parseXxx, config might be null. */
	private static void parseSubClientConfig(Configuration config, Element root) throws Exception {
		for (Element el : root.getElements("client-config")) {
			if (config != null) {
				parseClientConfig(config, el);
			}
		}
	}

	private static void parseListeners(Configuration config, Element root) throws Exception {
		for (Element el : root.getElements("listener")) {
			parseListener(config, el);
		}
	}

	private static void parseListener(Configuration config, Element el) {
		try {
			config.addListener(parseClass(el, "listener-class", null, true));
		} catch (Exception ex) {
			log.error("Unable to load a listener, " + el.getLocator(), ex);
		}
	}

	/** Parses zk.xml, specified by url, into the configuration.
	 *
	 * @param url the URL of zk.xml.
	 */
	public void parse(URL url, Configuration config, Locator locator) throws Exception {
		if (url == null || config == null)
			throw new IllegalArgumentException("null");
		log.info("Parsing " + url);
		parse(new SAXBuilder(true, false, true).build(url).getRootElement(), config, locator);
	}

	/** Parses zk.xml from an input stream into the configuration.
	 * @param is the input stream of zk.xml
	 * @since 5.0.7
	 */
	public void parse(InputStream is, Configuration config, Locator locator) throws Exception {
		if (is == null || config == null)
			throw new IllegalArgumentException("null");
		parse(new SAXBuilder(true, false, true).build(is).getRootElement(), config, locator);
	}

	/** Parses zk.xml, specified by the root element.
	 * @since 3.0.1
	 */
	public void parse(Element root, Configuration config, Locator locator) throws Exception {
		l_out: for (Iterator it = root.getElements().iterator(); it.hasNext();) {
			final Element el = (Element) it.next();
			final String elnm = el.getName();
			// B65-ZK-1671: ThemeProvider specified in metainfo/zk/zk.xml may get overridden by default
			//   config-name/depends elements were introduced to enforce that default configurations are  
			//   loaded in the sequence of zul -> zkex -> zkmax. User-supplied ThemeProvider, ThemeRegistry,
			//   and ThemeResolver will not get overridden by using flag variables. But multiple such
			//   configurations in different metainfo/zk/zk.xml still needs to be resolved by the assistance
			//   of config-name/depends.
			if ("config-name".equals(elnm) || "depends".equals(elnm))
				// known elements; not actual config items
				continue;
			else if ("listener".equals(elnm)) {
				parseListener(config, el);
			} else if ("richlet".equals(elnm)) {
				final String clsnm = IDOMs.getRequiredElementValue(el, "richlet-class");
				final Map<String, String> params = IDOMs.parseParams(el, "init-param", "param-name", "param-value");

				 //syntax since 2.4.0
				final String nm = IDOMs.getRequiredElementValue(el, "richlet-name");
				try {
					config.addRichlet(nm, clsnm, params);
				} catch (Throwable ex) {
					log.error("Illegal richlet definition at " + el.getLocator(), ex);
				}
			} else if ("richlet-mapping".equals(elnm)) { //syntax since 2.4.0
				final String nm = IDOMs.getRequiredElementValue(el, "richlet-name");
				final String path = IDOMs.getRequiredElementValue(el, "url-pattern");
				try {
					config.addRichletMapping(nm, path);
				} catch (Throwable ex) {
					log.error("Illegal richlet mapping at " + el.getLocator(), ex);
				}
			} else if ("desktop-config".equals(elnm)) {
				//desktop-config
				//	desktop-timeout
				//  disable-theme-uri
				//	file-check-period
				//	extendlet-check-period
				//	theme-provider-class
				//	theme-registry-class	/// since 6.5.2
				//	theme-resolver-class	/// since 6.5.2
				//	theme-uri
				//	theme-uri-handler-class	/// since 9.6.0
				//	repeat-uuid
				parseDesktopConfig(config, el);
				parseClientConfig(config, el); //backward compatible with 2.4

			} else if ("client-config".equals(elnm)) { //since 3.0.0
				//client-config
				//  keep-across-visits
				//  processing-prompt-delay
				//	error-reload
				//	tooltip-delay
				//  resend-delay
				//  debug-js
				//  enable-source-map
				//  auto-resend-timeout
				parseClientConfig(config, el);

			} else if ("session-config".equals(elnm)) {
				//session-config
				//	session-timeout
				//	max-desktops-per-session
				//  max-requests-per-session
				//	max-pushes-per-session
				//  timer-keep-alive
				//	timeout-uri
				//  automatic-timeout
				Integer v = parseInteger(el, "session-timeout", ANY_VALUE);
				if (v != null)
					config.setSessionMaxInactiveInterval(v.intValue());

				v = parseInteger(el, "max-desktops-per-session", ANY_VALUE);
				if (v != null)
					config.setSessionMaxDesktops(v.intValue());

				v = parseInteger(el, "max-requests-per-session", ANY_VALUE);
				if (v != null)
					config.setSessionMaxRequests(v.intValue());

				v = parseInteger(el, "max-pushes-per-session", ANY_VALUE);
				if (v != null)
					config.setSessionMaxPushes(v.intValue());

				String s = el.getElementValue("timer-keep-alive", true);
				if (s != null)
					config.setTimerKeepAlive("true".equals(s));

				parseTimeoutURI(config, el);
			} else if ("language-config".equals(elnm)) {
				//language-config
				//	addon-uri
				parseLangConfig(locator, el);
			} else if ("language-mapping".equals(elnm)) {
				//language-mapping
				//	language-name/extension
				DefinitionLoaders.addExtension(IDOMs.getRequiredElementValue(el, "extension"),
						IDOMs.getRequiredElementValue(el, "language-name"));
				//Note: we don't add it to LanguageDefinition now
				//since addon-uri might be specified later
				//(so we cannot load definitions now)
			} else if ("system-config".equals(elnm)) {
				//system-config
				//  disable-event-thread
				//	disable-zscript
				//	max-spare-threads
				//  max-suspended-threads
				//	event-time-warning
				//  max-upload-size
				//  upload-charset
				//  upload-charset-finder-class
				//  max-process-time
				//	response-charset
				//  cache-provider-class
				//  ui-factory-class
				//  failover-manager-class
				//	engine-class
				//	id-generator-class
				//  web-app-class
				//	method-cache-class
				//	url-encoder-class
				//	au-writer-class
				//	au-decoder-class
				parseSystemConfig(config, el);
			} else if ("xel-config".equals(elnm)) {
				//xel-config
				//	evaluator-class
				Class<? extends ExpressionFactory> cls = parseClass(el, "evaluator-class", ExpressionFactory.class);
				if (cls != null)
					config.setExpressionFactoryClass(cls);
			} else if ("zscript-config".equals(elnm)) {
				//zscript-config
				Interpreters.add(el);
				//Note: zscript-config is applied to the whole system, not just langdef
			} else if ("device-config".equals(elnm)) {
				//device-config
				Devices.add(el);
				//Note: device-config is applied to the whole system, not just langdef
				parseTimeoutURI(config, el);
			} else if ("error-page".equals(elnm)) {
				//error-page
				final Class cls = parseClass(el, "exception-type", Throwable.class, true);
				final String loc = IDOMs.getRequiredElementValue(el, "location");
				String deviceType = el.getElementValue("device-type", true);
				if (deviceType == null)
					deviceType = "ajax";
				else if (deviceType.length() == 0)
					log.error("device-type not specified at " + el.getLocator());

				config.addErrorPage(deviceType, cls, loc);
			} else if ("preference".equals(elnm)) {
				parsePreference(config, el);
			} else if ("library-property".equals(elnm)) {
				parseLibProperty(el);
			} else if ("system-property".equals(elnm)) {
				parseSysProperty(el);
			} else {
				if (_parsers != null)
					for (org.zkoss.zk.ui.util.ConfigParser parser : _parsers) {
						if (parser.parse(config, el))
							continue l_out;
					}
				log.error("Unknown element: " + elnm + ", at " + el.getLocator());
			}
		}
	}

	private static void parseProperties(Element root) {
		for (Iterator it = root.getElements("library-property").iterator(); it.hasNext();) {
			parseLibProperty((Element) it.next());
		}
		for (Iterator it = root.getElements("system-property").iterator(); it.hasNext();) {
			parseSysProperty((Element) it.next());
		}
	}

	/**
	 * Internal use only
	 */
	public static void parseLibProperty(Element el) {
		final String nm = IDOMs.getRequiredElementValue(el, "name");
		Element valueElmn = el.getElement("value");
		Element appendableElmn = el.getElement("appendable");
		boolean appendable = appendableElmn != null ? "true".equals(appendableElmn.getText(true)) : false;
		Element listElmn = el.getElement("list");
		if (valueElmn != null && listElmn != null)
			throw new IllegalSyntaxException("You should not use <value> and <list> in <library-property> at the same time");
		else if (listElmn != null) {
			List<Element> valElements = listElmn.getElements("value");
			if (valElements == null || valElements.isEmpty())
				throw new IllegalSyntaxException(MCommon.XML_ELEMENT_REQUIRED, new Object[] {"value", el.getLocator()});
			List<String> values = new LinkedList<String>();
			for (Element valElmn : valElements) {
				String val = valElmn.getText(true);
				if (val != null & val.length() != 0)
					values.add(val);
			}
			if (appendable)
				Library.addProperties(nm, values);
			else
				Library.setProperties(nm, values);
		} else if (valueElmn != null) {
			String val = valueElmn.getText(true);
			if (appendable)
				Library.addProperty(nm, val);
			else
				Library.setProperty(nm, val);
		} else {
			throw new IllegalSyntaxException(MCommon.XML_ELEMENT_REQUIRED, new Object[] {"<value> or <list>", el.getLocator()});
		}
	}

	private static void parseSysProperty(Element el) {
		final String nm = IDOMs.getRequiredElementValue(el, "name");
		final String val = IDOMs.getRequiredElementValue(el, "value");
		System.setProperty(nm, val);
	}

	private static void parsePreferences(Configuration config, Element root) {
		for (Iterator it = root.getElements("preference").iterator(); it.hasNext();) {
			parsePreference(config, (Element) it.next());
		}
	}

	private static void parsePreference(Configuration config, Element el) {
		final String nm = IDOMs.getRequiredElementValue(el, "name");
		final String val = IDOMs.getRequiredElementValue(el, "value");
		config.setPreference(nm, val);
	}

	/** Parses timeout-uri an other info. */
	private static void parseTimeoutURI(Configuration config, Element conf) throws Exception {
		String deviceType = conf.getElementValue("device-type", true);
		String s = conf.getElementValue("timeout-uri", true);
		if (s != null)
			config.setTimeoutURI(deviceType, s, URIInfo.SEND_REDIRECT);

		s = conf.getElementValue("timeout-message", true);
		if (s != null)
			config.setTimeoutMessage(deviceType, s);

		s = conf.getElementValue("automatic-timeout", true);
		if (s != null)
			config.setAutomaticTimeout(deviceType, !"false".equals(s));
	}

	/** Parses desktop-config. */
	private static void parseDesktopConfig(Configuration config, Element conf) throws Exception {
		//theme-uri
		for (Iterator<Element> it = conf.getElements("theme-uri").iterator(); it.hasNext();) {
			final Element el = (Element) it.next();
			final String uri = el.getText(true);
			if (uri.length() != 0)
				config.addThemeURI(uri);
		}

		//disable-theme-uri
		for (Iterator<Element> it = conf.getElements("disable-theme-uri").iterator(); it.hasNext();) {
			final Element el = (Element) it.next();
			final String uri = el.getText(true);
			if (uri.length() != 0)
				config.addDisabledThemeURI(uri);
		}

		// ZK-1671
		Class<?> cls = null;
		//theme-provider-class
		if (!config.isCustomThemeProvider()) {
			cls = parseClass(conf, "theme-provider-class", ThemeProvider.class);
			if (cls != null) {
				if (!cls.getName().startsWith("org.zkoss."))
					config.setCustomThemeProvider(true);
				if (log.isDebugEnabled())
					log.debug("ThemeProvider: " + cls.getName());
				config.setThemeProvider((ThemeProvider) cls.newInstance());
			}
		}

		//theme-registry-class
		//since 6.5.2
		if (!config.isCustomThemeRegistry()) {
			cls = parseClass(conf, "theme-registry-class", ThemeRegistry.class);
			if (cls != null) {
				if (!cls.getName().startsWith("org.zkoss."))
					config.setCustomThemeRegistry(true);
				if (log.isDebugEnabled())
					log.debug("ThemeRegistry: " + cls.getName());
				ThemeFns.setThemeRegistry((ThemeRegistry) cls.newInstance());
			}
		}

		//theme-resolver-class
		//since 6.5.2
		if (!config.isCustomThemeResolver()) {
			cls = parseClass(conf, "theme-resolver-class", ThemeResolver.class);
			if (cls != null) {
				if (!cls.getName().startsWith("org.zkoss."))
					config.setCustomThemeResolver(true);
				if (log.isDebugEnabled())
					log.debug("ThemeResolver: " + cls.getName());
				ThemeFns.setThemeResolver((ThemeResolver) cls.newInstance());
			}
		}

		//theme-uri-handler-class
		//since 9.6.0
		cls = parseClass(conf, "theme-uri-handler-class", ThemeURIHandler.class);
		if (cls != null) {
			if (log.isDebugEnabled())
				log.debug("ThemeURIHandler: " + cls.getName());
			config.addThemeURIHandler((ThemeURIHandler) cls.newInstance());
		}

		//desktop-timeout
		Integer v = parseInteger(conf, "desktop-timeout", ANY_VALUE);
		if (v != null)
			config.setDesktopMaxInactiveInterval(v.intValue());

		//file-check-period
		v = parseInteger(conf, "file-check-period", POSITIVE_ONLY);
		if (v != null)
			Library.setProperty("org.zkoss.util.resource.checkPeriod", v.toString());
		//library-wide property

		//extendlet-check-period
		v = parseInteger(conf, "extendlet-check-period", POSITIVE_ONLY);
		if (v != null)
			Library.setProperty("org.zkoss.util.resource.extendlet.checkPeriod", v.toString());
		//library-wide property
	}

	/** Parses client-config. */
	@SuppressWarnings("deprecation")
	private static void parseSystemConfig(Configuration config, Element el) throws Exception {
		String s = el.getElementValue("disable-event-thread", true);
		if (s != null) {
			final boolean enable = "false".equals(s);
			if (!enable)
				log.info("The event processing thread is disabled");
			config.enableEventThread(enable);
		}
		s = el.getElementValue("disable-zscript", true);
		if (s != null)
			config.enableZScript(!"true".equals(s));

		Integer v = parseInteger(el, "max-spare-threads", ANY_VALUE);
		if (v != null)
			config.setMaxSpareThreads(v.intValue());

		v = parseInteger(el, "max-suspended-threads", ANY_VALUE);
		if (v != null)
			config.setMaxSuspendedThreads(v.intValue());

		v = parseInteger(el, "event-time-warning", ANY_VALUE);
		if (v != null)
			config.setEventTimeWarning(v.intValue());

		v = parseInteger(el, "max-upload-size", ANY_VALUE);
		if (v != null)
			config.setMaxUploadSize(v.intValue());

		v = parseInteger(el, "file-size-threshold", ANY_VALUE);
		if (v != null)
			config.setFileSizeThreshold(v.intValue());

		v = parseInteger(el, "max-process-time", POSITIVE_ONLY);
		if (v != null)
			config.setMaxProcessTime(v.intValue());

		s = el.getElementValue("upload-charset", true);
		if (s != null)
			config.setUploadCharset(s);

		s = el.getElementValue("response-charset", true);
		if (s != null)
			config.setResponseCharset(s);

		s = el.getElementValue("crawlable", true);
		if (s != null)
			config.setCrawlable(!"false".equals(s));

		// ZK-3105
		s = el.getElementValue("file-repository", true);
		if (s != null)
			config.setFileRepository(s);

		//bug B50-3316543
		for (Iterator it = el.getElements("label-location").iterator(); it.hasNext();) {
			final Element elinner = (Element) it.next();
			final String path = elinner.getText(true);
			if (!Strings.isEmpty(path))
				config.addLabelLocation(path);
		}

		Class cls = parseClass(el, "upload-charset-finder-class", CharsetFinder.class);
		if (cls != null)
			config.setUploadCharsetFinder((CharsetFinder) cls.newInstance());

		cls = parseClass(el, "cache-provider-class", DesktopCacheProvider.class);
		if (cls != null)
			config.setDesktopCacheProviderClass(cls);

		cls = parseClass(el, "ui-factory-class", UiFactory.class);
		if (cls != null)
			config.setUiFactoryClass(cls);

		cls = parseClass(el, "failover-manager-class", FailoverManager.class);
		if (cls != null)
			config.setFailoverManagerClass(cls);

		cls = parseClass(el, "engine-class", UiEngine.class);
		if (cls != null)
			config.setUiEngineClass(cls);

		cls = parseClass(el, "id-generator-class", IdGenerator.class);
		if (cls != null)
			config.setIdGeneratorClass(cls);

		cls = parseClass(el, "session-cache-class", SessionCache.class);
		if (cls != null)
			config.setSessionCacheClass(cls);

		// ZK-3105
		cls = parseClass(el, "file-item-factory-class", DiskFileItemFactory.class);
		if (cls != null)
			config.setFileItemFactoryClass(cls);

		cls = parseClass(el, "au-decoder-class", AuDecoder.class);
		if (cls != null)
			config.setAuDecoderClass(cls);

		cls = parseClass(el, "web-app-class", WebApp.class);
		if (cls != null)
			config.setWebAppClass(cls);

		cls = parseClass(el, "web-app-factory-class", WebAppFactory.class);
		if (cls != null)
			config.setWebAppFactoryClass(cls);

		cls = parseClass(el, "method-cache-class", Cache.class);
		if (cls != null)
			ComponentsCtrl.setEventMethodCache((Cache) cls.newInstance());

		cls = parseClass(el, "au-writer-class", AuWriter.class);
		if (cls != null)
			AuWriters.setImplementationClass(cls);
	}

	/** Parses client-config. */
	private static void parseClientConfig(Configuration config, Element conf) {
		Integer v = parseInteger(conf, "processing-prompt-delay", POSITIVE_ONLY);
		if (v != null)
			config.setProcessingPromptDelay(v.intValue());

		v = parseInteger(conf, "tooltip-delay", POSITIVE_ONLY);
		if (v != null)
			config.setTooltipDelay(v.intValue());

		v = parseInteger(conf, "auto-resend-timeout", POSITIVE_ONLY);
		if (v != null)
			config.setAutoResendTimeout(v.intValue());

		String s = conf.getElementValue("keep-across-visits", true);
		if (s != null)
			config.setKeepDesktopAcrossVisits(!"false".equals(s));

		s = conf.getElementValue("debug-js", true);
		if (s != null)
			config.setDebugJS(!"false".equals(s));

		s = conf.getElementValue("enable-source-map", true);
		if (s != null)
			config.enableSourceMap(!"false".equals(s));

		//F70-ZK-2495: add new config to customize crash script
		s = conf.getElementValue("init-crash-script", true);
		if (s != null)
			config.setInitCrashScript(s);

		//F70-ZK-2495: add new config to customize timeout
		v = parseInteger(conf, "init-crash-timeout", NON_NEGATIVE);
		if (v != null)
			config.setInitCrashTimeout(v.intValue());

		//ZK-4179: add new config to disable ZK history API
		s = conf.getElementValue("enable-history-state", true);
		if (s != null)
			config.enableHistoryState(Boolean.parseBoolean(s));

		//client (JS) packages
		for (Iterator it = conf.getElements("package").iterator(); it.hasNext();) {
			config.addClientPackage(IDOMs.getRequiredElementValue((Element) it.next(), "package-name"));
		}

		//client data-attr handlers
		for (Iterator<Element> it = conf.getElements("data-handler").iterator(); it.hasNext();) {
			//config.addClientPackage(IDOMs.getRequiredElementValue((Element)it.next(), "package-name"));
			final Element el = it.next();
			String dataName = IDOMs.getRequiredElementValue(el, "name");
			List<Element> elements = el.getElements("script");
			List<Pair<String, String>> scripts = null;
			if (!elements.isEmpty()) {
				scripts = new LinkedList<Pair<String, String>>();
				for (Iterator<Element> itt = elements.iterator(); itt.hasNext();) {
					Element e = itt.next();
					scripts.add(new Pair<String, String>(e.getAttribute("src"), e.getText(true)));
				}
			}
			if (scripts == null)
				throw new IllegalSyntaxException(MCommon.XML_ELEMENT_REQUIRED,
						new Object[] { "script", el.getLocator() });

			boolean override = Boolean.parseBoolean(el.getElementValue("override", true));

			elements = el.getElements("link");
			List<Map<String, String>> links = null;
			if (!elements.isEmpty()) {
				links = new LinkedList<Map<String, String>>();
				for (Iterator<Element> itt = elements.iterator(); itt.hasNext();) {
					Element e = itt.next();
					List<Attribute> attrs = e.getAttributeItems();
					if (attrs == null || attrs.isEmpty())
						continue;
					Map<String, String> attrMap = new LinkedHashMap<String, String>();
					for (Attribute a : attrs)
						attrMap.put(a.getName(), a.getValue());
					links.add(attrMap);
				}
			}
			config.addDataHandler(new DataHandlerInfo(dataName, scripts, override, links));
		}
		//error-reload
		for (Iterator it = conf.getElements("error-reload").iterator(); it.hasNext();) {
			final Element el = (Element) it.next();

			String deviceType = el.getElementValue("device-type", true);
			String connType = el.getElementValue("connection-type", true);
			v = parseInteger(el, "error-code", NON_NEGATIVE);
			if (v == null)
				throw new UiException(message("error-code is required", el));
			String uri = IDOMs.getRequiredElementValue(el, "reload-uri");
			if ("false".equals(uri))
				uri = null;

			config.setClientErrorReload(deviceType, v.intValue(), uri, connType);
		}
	}

	private static String message(String message, org.zkoss.idom.Item el) {
		return org.zkoss.xml.Locators.format(message, el != null ? el.getLocator() : null);
	}

	/** Parse language-config */
	private static void parseLangConfigs(Locator locator, Element root) {
		for (Iterator it = root.getElements("language-config").iterator(); it.hasNext();) {
			parseLangConfig(locator, (Element) it.next());
		}
	}

	/** Parse language-config/addon-uri. */
	private static void parseLangConfig(Locator locator, Element conf) {
		for (Iterator it = conf.getElements("addon-uri").iterator(); it.hasNext();) {
			final Element el = (Element) it.next();
			final String path = el.getText(true);

			final URL url = locator.getResource(path);
			if (url == null)
				log.error("File not found: " + path + ", at " + el.getLocator());
			else
				DefinitionLoaders.addAddon(locator, url);
		}
		for (Iterator it = conf.getElements("language-uri").iterator(); it.hasNext();) {
			final Element el = (Element) it.next();
			final String path = el.getText(true);

			final URL url = locator.getResource(path);
			if (url == null)
				log.error("File not found: " + path + ", at " + el.getLocator());
			else
				DefinitionLoaders.addLanguage(locator, url);
		}
	}

	/** Parse a class, if specified, whether it implements cls.
	 */
	private static <T> Class<T> parseClass(Element el, String elnm, Class cls) {
		return parseClass(el, elnm, cls, false);
	}

	private static <T> Class<T> parseClass(Element el, String elnm, Class<?> cls, boolean required) {
		//Note: we throw exception rather than warning to make sure
		//the developer correct it
		final String clsnm = el.getElementValue(elnm, true);
		if (clsnm != null && clsnm.length() != 0) {
			try {
				final Class<?> klass = Classes.forNameByThread(clsnm);
				if (cls != null && !cls.isAssignableFrom(klass)) {
					String msg = message(clsnm + " must implement " + cls.getName(), el);
					if (required)
						throw new UiException(msg);
					log.error(msg);
					return null;
				}
				//				if (log.debuggable()) log.debug("Using "+clsnm+" for "+cls);
				return cast(klass);
			} catch (Throwable ex) {
				String msg = ex instanceof ClassNotFoundException ? clsnm + " not found" : "Unable to load " + clsnm;
				msg = message(msg, el);
				if (required)
					throw new UiException(msg, ex);
				log.error(msg);
				return null;
			}
		} else if (required)
			throw new UiException(message(elnm + " required", el));
		return null;
	}

	/** Configures an integer.
	 * @param flag one of POSTIVE_ONLY, NON_NEGATIVE and ANY_VALUE.
	 */
	private static Integer parseInteger(Element el, String subnm, int flag) throws UiException {
		//Note: we throw exception rather than warning to make sure
		//the developer correct it
		String val = el.getElementValue(subnm, true);
		if (val != null && val.length() > 0) {
			try {
				final int v = Integer.parseInt(val);
				if ((flag == POSITIVE_ONLY && v <= 0) || (flag == NON_NEGATIVE && v < 0))
					throw new UiException(message(
							"The " + subnm + " element must be a "
									+ (flag == POSITIVE_ONLY ? "positive" : "non-negative") + " number, not " + val,
							el));
				return new Integer(v);
			} catch (NumberFormatException ex) { //eat
				throw new UiException(message("The " + subnm + " element must be a number, not " + val, el));
			}
		}
		return null;
	}

	private static final int POSITIVE_ONLY = 2;
	private static final int NON_NEGATIVE = 1;
	private static final int ANY_VALUE = 0;

	//F80 - store subtree's binder annotation count
	private static void parseBinderConfig(Configuration config, Element conf) {
		Element binderConf = conf.getElement("binder-config");
		if (binderConf != null) {
			config.setBinderInitAttribute(binderConf.getElement("binder-init-attribute").getText());
			List<Element> values = binderConf.getElement("binding-annotations").getElement("list").getElements();
			Set<String> annots = new HashSet<String>();
			for (Element val : values) {
				annots.add("@" + val.getText() + "(");
			}
			config.setBinderAnnotations(annots);
		}
	}
}
