/* ThemeProperties.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 31, 2012 4:36:37 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.web.fn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.lang.Strings;
import org.zkoss.util.Maps;
import org.zkoss.util.resource.Locators;
import org.zkoss.web.util.resource.ServletContextLocator;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.taglib.Taglib;
import org.zkoss.xel.taglib.Taglibs;
import org.zkoss.xel.util.SimpleResolver;
import org.zkoss.xel.util.SimpleXelContext;
import org.zkoss.xel.zel.ELFactory;

/**
 * A utility theme properties loader
 * @author simonpai
 * @author jumperchen
 * @since 6.5.0
 */
public class ThemeProperties {
	private ThemeProperties() {}
	private static final ELFactory _ELF = new ELFactory();
	private static final String THEME_FN_URL = "http://www.zkoss.org/dsp/web/theme";
	private static final String CORE_FN_URL = "http://www.zkoss.org/dsp/web/core";
	
	/**
	 * Loads a properties file and apply them into the request scope
	 */
	public static boolean loadProperties(ServletRequest req, String bundleName) {
		final Locators.StreamLocation loc =
			Locators.locateAsStream(bundleName, 
					null, Locators.getDefault());
		if (loc != null)
			return loadProperties(req, loc.stream);
		else {
			// add ability to load theme properties from a folder
			// @since 6.5.2
			String root = ((HttpServletRequest)req).getContextPath();
			ServletContext context = ServletFns.getCurrentServletContext();
			bundleName = bundleName.replace(root, "");
			
			return loadProperties(req, new ServletContextLocator(context).getResourceAsStream(bundleName));
		}
	}
	
	/**
	 * Loads a properties file and apply them into the request scope
	 */
	public static boolean loadProperties(ServletRequest req, InputStream in) {
		Map<String, Object> pmap = new LinkedHashMap<String, Object>(); // preserve order
		try {
			Maps.load(pmap, in);
			return loadProperties(req, pmap);
		} catch (IOException e) {
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPropertyMap(ServletRequest req, String key) {
		Object obj = req.getAttribute(key);
		if (obj == null) {
			Map<String, Object> m = new HashMap<String, Object>(200);
			req.setAttribute(key, m);
			Enumeration names = req.getAttributeNames();
	        while(names.hasMoreElements()) {
	        	String name = (String) names.nextElement();
				m.put(name, req.getAttribute(name));
	        }
			return m;
		}
		if (obj instanceof Map<?, ?>)
			return (Map<String, Object>) obj;
		throw new IllegalStateException("Root node is not a Map: " + key);
	}
	
	private static boolean loadProperties(ServletRequest req, Map<String, Object> pmap) {
		Map<String, Object> map = getPropertyMap(req, "_theme");
		XelContext ctx = buildXelContext(map);
		for (Map.Entry<String, Object> e : pmap.entrySet()) {
			Object objv = e.getValue();
			if (objv == null)
				continue;
			String v = objv.toString();
			if (Strings.isBlank(v))
				continue;
			Object w = v.contains("${") ? _ELF.evaluate(ctx, v, Object.class) : v;

			map.put(e.getKey(), w);
			req.setAttribute(e.getKey(), w);
			
		}
		return true;
	}
	
	private static XelContext buildXelContext(Map<String, Object> map) {
		List<Taglib> libs = Arrays.asList(new Taglib("t", THEME_FN_URL), new Taglib("c", CORE_FN_URL));
		FunctionMapper mapper = Taglibs.getFunctionMapper(libs, Locators.getDefault());
		VariableResolver resolver = new SimpleResolver(map);
		return new SimpleXelContext(resolver, mapper);
	}
}
