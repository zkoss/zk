/* ZkdemoWebAppInit.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 12, 2008 2:49:39 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zksandbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.zkoss.lang.Library;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 * @author jumperchen
 *
 */
public class DemoWebAppInit implements WebAppInit {

	private static final Log log = Log.lookup(DemoWebAppInit.class);
	
	final static String PATH = "/";

	final static String CONFIG = "zksandbox.properties";

	final static String CATEGORY_TYPE = "CATEGORY";
	
	final static String LINK_TYPE = "LINK";

	private final static String THEME_CLASSICBLUE_NAME = Themes.CLASSICBLUE_THEME;
	private final static String THEME_CLASSICBLUE_DISPLAY = "Classic blue";
	private final static int THEME_CLASSICBLUE_PRIORITY = 1000;
	
	private final static String THEME_SILVERGREY_NAME = "silvergray";
	private final static String SILVERGREY_DISPLAY = "Silver Gray";
	private final static int SILVERGREY_PRIORITY = 9000;
	/*package*/ final static String KEY_SILVERGREY_PROPERTY = "org.zkoss.zul.themejar.silvergray";

	/*package*/ final static String THEME_DEFAULT = "org.zkoss.theme.default";
	private final static String PREFIX_KEY_PRIORITY = "org.zkoss.theme.priority.";
	private final static String PREFIX_KEY_THEME_DISPLAYS = "org.zkoss.theme.display.";
	
	private static Map _cateMap = new LinkedHashMap () {
		 public Object remove(Object key) {
			 throw new UnsupportedOperationException();
		 }
		 public void clear() {
			 throw new UnsupportedOperationException();			 
		 }
	};
	
	public void init(WebApp wapp) throws Exception {
		loadProperites((ServletContext)wapp.getNativeContext());
		setThemeProperites();
		initThemes();
	}

	private static void initThemes() {
		addTheme(THEME_CLASSICBLUE_NAME, THEME_CLASSICBLUE_DISPLAY, THEME_CLASSICBLUE_PRIORITY);
		if(Themes.hasSilvergrayLib())
			addTheme(THEME_SILVERGREY_NAME, SILVERGREY_DISPLAY, SILVERGREY_PRIORITY);
	}
	
	/**
	 * Sets silvergray library property if there is silvergray.jar 
	 */
	private void setThemeProperites () {
		String prop = Library.getProperty(KEY_SILVERGREY_PROPERTY);
		if (prop == null) {
			final ClassLocator loc = new ClassLocator();
			try {
				for (Enumeration en = loc.getResources("metainfo/zk/lang-addon.xml");
				en.hasMoreElements();) {
					final URL url = (URL)en.nextElement();
					if (url.toString().lastIndexOf("silvergray-tod.jar") >= 0) {
						Library.setProperty(KEY_SILVERGREY_PROPERTY, "true");
						return;
					}
				}
			} catch (Exception ex) {
				log.error(ex); //keep running
			}
			Library.setProperty(KEY_SILVERGREY_PROPERTY, "false");
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

	private static void appendThemeName(String theme) {
		String themes = Library.getProperty(Themes.THEME_NAMES);
		if (themes == null)
			Library.setProperty(Themes.THEME_NAMES, theme + ";");
		else if (!Themes.containTheme(themes, theme))
			Library.setProperty(Themes.THEME_NAMES, themes + ";" + theme);
	}
	
	private static void setThemeDisplay(String name, String display) {
		Library.setProperty(PREFIX_KEY_THEME_DISPLAYS + name, display);
	}

	private static void updateFirstPriority(String name, int priority) {
		Library.setProperty(PREFIX_KEY_PRIORITY + name, "" + priority);
		
		String defaultTheme = Library.getProperty(THEME_DEFAULT);
		if (Library.getIntProperty(PREFIX_KEY_PRIORITY + defaultTheme, Integer.MAX_VALUE) < priority)
			return;
		Library.setProperty(THEME_DEFAULT, name);
	}

	private static void addTheme(String themeName, String themeDisplay, int themePriority){
		appendThemeName(themeName);
		setThemeDisplay(themeName, themeDisplay);
		updateFirstPriority(themeName, themePriority);
	}
}
