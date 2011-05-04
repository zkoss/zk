package org.zkoss.zksandbox;

/* DemoWebAppInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 6, 2010 11:57:53 AM , Created by simon
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.zkoss.lang.Library;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 * 1. Adds an AuExtension to handle description search AU request
 * @author simon / sam
 */
public class DemoWebAppInit2 implements WebAppInit {
	
	// ############## from original one below ###################
	private static final Log log = Log.lookup(DemoWebAppInit2.class);
	
	final static String PATH = "/";

	final static String CONFIG = "zksandbox.properties";

	final static String CATEGORY_TYPE = "CATEGORY";
	
	final static String LINK_TYPE = "LINK";
	// ############## from original one above ###################
	
	/*package*/ final static String THEME_DEFAULT = "org.zkoss.theme.default.name";
	private final static String PREFIX_KEY_PRIORITY = "org.zkoss.theme.priority.";
	private final static String PREFIX_KEY_THEME_DISPLAYS = "org.zkoss.theme.display.";
	
	public void init(WebApp wapp) throws Exception {
		loadProperites((ServletContext)wapp.getNativeContext());
		setThemeProperites();
		initThemes();
	}
	
	private static void initThemes() {
		// get all properties from themes.properties
		Properties themesProperty = Themes2.getThemesProperty();

		String theme = themesProperty.getProperty("theme");

		String themeName = themesProperty.getProperty(theme+"_name");
		String themeDisplay = themesProperty.getProperty(theme+"_display");
		int themePriority = Integer.parseInt(themesProperty.getProperty(theme+"_priority"));
		appendThemeName(themeName);
		setThemeDisplay(themeName, themeDisplay);
		updateFirstPriority(themeName, themePriority);
	}

	private static void appendThemeName(String theme) {
		String themes = Library.getProperty(Themes2.getProperty("THEME_NAMES"));
		if (themes == null)
			Library.setProperty(Themes2.getProperty("THEME_NAMES"), theme + ";");
		else if (!Themes2.containTheme(themes, theme))
			Library.setProperty(Themes2.getProperty("THEME_NAMES"), themes + ";" + theme);
	}
	
	private static void setThemeDisplay(String name, String display) {
		Library.setProperty(PREFIX_KEY_THEME_DISPLAYS + name, display);
	}

	// ############## from original one below ###################
	private static void updateFirstPriority(String name, int priority) {
		Library.setProperty(PREFIX_KEY_PRIORITY + name, "" + priority);
		
		String defaultTheme = Library.getProperty(THEME_DEFAULT);
		if (Library.getIntProperty(PREFIX_KEY_PRIORITY + defaultTheme, Integer.MAX_VALUE) < priority)
			return;
		Library.setProperty(THEME_DEFAULT, name);
	}
	
	private static Map _cateMap = new LinkedHashMap () {
		 public Object remove(Object key) {
			 throw new UnsupportedOperationException();
		 }
		 public void clear() {
			 throw new UnsupportedOperationException();			 
		 }
	};
	
	/**
	 * Sets silvergray library property if there is silvergray.jar 
	 */
	private void setThemeProperites () {
		String prop = Library.getProperty("org.zkoss.zksandbox.theme.silvergray");
		if (prop == null) {
			final ClassLocator loc = new ClassLocator();
			try {
				for (Enumeration en = loc.getResources("metainfo/zk/lang-addon.xml");
				en.hasMoreElements();) {
					final URL url = (URL)en.nextElement();
					if (url.toString().lastIndexOf("silvergray.jar") >= 0) {
						Library.setProperty("org.zkoss.zksandbox.theme.silvergray", "true");
						return;
					}
				}
			} catch (Exception ex) {
				log.error(ex); //keep running
			}
			Library.setProperty("org.zkoss.zksandbox.theme.silvergray", "false");
		}
	}
	static Map getCateMap() {
		return _cateMap;
	}
	private void loadProperites(ServletContext context) {
		try {
			BufferedReader bufReader = new BufferedReader(
					new InputStreamReader(context.getResourceAsStream(PATH + CONFIG)));
			String prop = null;
			while ((prop = bufReader.readLine()) != null) {
				int begin = prop.indexOf("=");
				if (prop.startsWith("#") || prop.startsWith("!") || begin == -1)
					continue;
				
				final String key = prop.substring(0, begin).trim();
				final String values = prop.substring(begin + 1, prop.length()).trim();
				final String[] vals = values.split(",");
				if (vals.length == 0)
					continue;
				final String arg0 = vals[0].trim();
				if (CATEGORY_TYPE.equals(arg0)) {
					if (vals.length < 3) {
						log.error("This category has no enough argument: size less than 3, for example, CATEGORY,IconURL,Label");
						continue;
					}
					Category cate = new Category(key, vals[1].trim(), vals[2].trim(), null);
					_cateMap.put(key, cate);
				} else if (LINK_TYPE.equals(arg0) ) {
					if (vals.length < 4) {
						log.error("This category has no enough argument: size less than 4, for example, LINK,IconURL,Label,Href");
						continue;
					}
					Category cate = new Category(key, vals[1].trim(), vals[2].trim(), vals[3].trim());
					_cateMap.put(key, cate);
				} else {
					Category cate = (Category) _cateMap.get(arg0);
					if (cate == null) {
						log.error("This category is undefined: " + arg0);
						continue;
					}
					if (vals.length < 4) {
						log.error("This demo item has no enough argument: size less than 4, for example, categoryId,FileURL,IconURL,Label");
						continue;
					}
					// [ItemId=CategoryId, Demo File URL, Icon URL, Label]
					DemoItem item = new DemoItem(key, arg0, vals[1].trim(), vals[2].trim(), vals[3].trim());
					cate.addItem(item);
				}
			}
			bufReader.close();
		} catch (IOException e) {
			log.error("Ingored: failed to load a properties file, \nCause: "
					+ e.getMessage());
		}
	}
	// ############## from original one above ###################
}
