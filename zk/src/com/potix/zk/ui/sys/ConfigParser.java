/* ConfigParser.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Mar 26 18:09:10     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import java.util.Iterator;
import java.net.URL;

import javax.servlet.jsp.el.ExpressionEvaluator;

import com.potix.lang.Classes;
import com.potix.util.resource.Locator;
import com.potix.util.logging.Log;
import com.potix.idom.Element;
import com.potix.idom.input.SAXBuilder;
import com.potix.idom.util.IDOMs;

import com.potix.zk.ui.sys.UiEngine;
import com.potix.zk.ui.sys.DesktopCacheProvider;
import com.potix.zk.ui.sys.UiFactory;
import com.potix.zk.ui.sys.LocaleProvider;
import com.potix.zk.ui.sys.TimeZoneProvider;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.Configuration;
import com.potix.zk.ui.metainfo.DefinitionLoaders;

/**
 * Used to parse WEB-INF/zk.xml into {@link Configuration}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class ConfigParser {
	private static final Log log = Log.lookup(ConfigParser.class);

	/** Parses zk.xml, specified by url, into the configuration.
	 *
	 * @param url the URL of zk.xml.
	 */
	public void parse(URL url, Configuration config, Locator locator)
	throws Exception {
		if (url == null || config == null)
			throw new IllegalArgumentException("null");
		log.info("Parsing "+url);

		final Element root = new SAXBuilder(false, false, true)
			.build(url).getRootElement();
		for (Iterator it = root.getElements().iterator(); it.hasNext();) {
			final Element el = (Element)it.next();
			final String elnm = el.getName();
			if ("listener".equals(elnm)) {
				final String clsnm =
					IDOMs.getRequiredElementValue(el, "listener-class");
				try {
					final Class cls = Classes.forNameByThread(clsnm);
					config.addListener(cls);
				} catch (Throwable ex) {
					throw new UiException("Unable to load "+clsnm, ex);
				}
			} else if ("desktop-config".equals(elnm)) {
			//desktop-config
			//	theme-uri
			//	desktop-timeout
			//	file-check-period
				String s = el.getElementValue("theme-uri", true);
				if (s != null && s.length() > 0) config.setThemeURI(s);

				Integer v = parseInteger(el, "desktop-timeout", false);
				if (v != null) config.setDesktopMaxInactiveInterval(v);

				v = parseInteger(el, "file-check-period", true);
				if (v != null) System.setProperty("com.potix.util.resource.checkPeriod", v.toString());
					//System-wide property
			} else if ("session-config".equals(elnm)) {
			//session-config
			//	session-timeout
			//	max-desktops-per-session
			//	timeout-uri
				Integer v = parseInteger(el, "session-timeout", false);
				if (v != null) config.setSessionMaxInactiveInterval(v);

				v = parseInteger(el, "max-desktops-per-session", true);
				if (v != null) config.setMaxDesktops(v);

				final String s = el.getElementValue("timeout-uri", true);
				if (s != null) config.setTimeoutURI(s);
			} else if ("language-config".equals(elnm)) {
			//language-config
			//	addon-uri
				parseLangAddon(locator, el);
			} else if ("system-config".equals(elnm)) {
			//system-config
			//	max-event-threads
			//  max-upload-size
			//  cache-provider-class
			//  ui-factory-class
			//	engine-class
				Integer v = parseInteger(el, "max-event-threads", true);
				if (v != null) config.setMaxEventThreads(v);
				
				v = parseInteger(el, "max-upload-size", true);
				if (v != null) System.setProperty("com.potix.web.servlet.http.MaxUploadSize", v.toString());
					//System-wide property

				Class cls = parseClass(el, "cache-provider-class",
					DesktopCacheProvider.class);
				if (cls != null) config.setDesktopCacheProviderClass(cls);

				cls = parseClass(el, "ui-factory-class", UiFactory.class);
				if (cls != null) config.setUiFactoryClass(cls);

				cls = parseClass(el, "engine-class", UiEngine.class);
				if (cls != null) config.setUiEngineClass(cls);

				cls = parseClass(el, "locale-provider-class", LocaleProvider.class);
				if (cls != null) config.setLocaleProviderClass(cls);

				cls = parseClass(el, "timeZone-provider-class", TimeZoneProvider.class);
				if (cls != null) config.setTimeZoneProviderClass(cls);
			} else if ("el-config".equals(elnm)) {
			//el-config
			//	evaluator-class
				Class cls = parseClass(el, "evaluator-class", ExpressionEvaluator.class);
				if (cls != null) System.setProperty("com.potix.el.ExpressionEvaluator.class", cls.getName());
					//System-wide property; reason: used in pxcommon.jar
			} else if ("log".equals(elnm)) {
				final String base = el.getElementValue("log-base", true);
				if (base != null)
					com.potix.util.logging.LogService.init(base, null); //start the log service
			} else {
				throw new UiException("Unknown element: "+elnm+", "+el.getLocator());
			}
		}
	}

	/** Parse language-config/addon-uri. */
	private static void parseLangAddon(Locator locator, Element conf) {
		for (Iterator it = conf.getElements("addon-uri").iterator();
		it.hasNext();) {
			final Element el = (Element)it.next();
			final String path = el.getText().trim();

			final URL url = locator.getResource(path);
			if (url == null)
				log.error("File not found: "+path);
			else
				DefinitionLoaders.addLanguage(locator, url);
		}
	}
	/** Parse a class, if specified, whether it implements cls.
	 */
	private static Class parseClass(Element el, String elnm, Class cls) {
		final String clsnm = el.getElementValue(elnm, true);
		if (clsnm != null && clsnm.length() != 0) {
			try {
				final Class klass = Classes.forNameByThread(clsnm);
				if (cls != null && !cls.isAssignableFrom(klass))
					throw new UiException(clsnm+" must implement "+cls.getName()+", "+el.getLocator());
				log.info("Using "+clsnm+" for "+cls);
				return klass;
			} catch (ClassNotFoundException ex) {
				throw new UiException("Class not found: "+clsnm+", "+el.getLocator());
			}
		}
		return null;
	}

	/** Configures an integer. */
	private static Integer parseInteger(Element el, String subnm,
	boolean positiveOnly) throws UiException {
		String val = el.getElementValue(subnm, true);
		if (val != null && val.length() > 0) {
			try { 
				final int v = Integer.parseInt(val);
				if (positiveOnly && v <= 0)
					throw new UiException("The "+subnm+" element must be a positive number, not "+val+", at "+el.getLocator());
				return new Integer(v);
			} catch (NumberFormatException ex) { //eat
				throw new UiException("The "+subnm+" element must be a number, not "+val+", at "+el.getLocator());
			}
		}
		return null;
	}
}
