/* DemoWebAppInit.java

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
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 * @author jumperchen
 *
 */
public class DemoWebAppInit implements WebAppInit {

	private static final Logger log = LoggerFactory.getLogger(DemoWebAppInit.class);
	
	final static String PATH = "/";
	final static String CONFIG = "zksandbox.properties";
	final static String CATEGORY_TYPE = "CATEGORY";
	final static String LINK_TYPE = "LINK";
	
	@SuppressWarnings("serial")
	private static Map<String, Category> _cateMap = new LinkedHashMap<String, Category> () {
		
		public Category remove(Object key) {
			throw new UnsupportedOperationException();
		}
		
		public void clear() {
			throw new UnsupportedOperationException();
		}
	};

	@SuppressWarnings("serial")
	private static Map<String, Category> _mobileCateMap = new LinkedHashMap<String, Category> () {
		
		public Category remove(Object key) {
			throw new UnsupportedOperationException();
		}
		
		public void clear() {
			throw new UnsupportedOperationException();
		}
	};
	
	
	public void init(WebApp wapp) throws Exception {
		loadProperites(wapp.getServletContext());
	}
	
	static Map<String, Category> getCateMap() {
		return _cateMap;
	}
	
	static Map<String, Category> getMobilCateMap() {
		return _mobileCateMap;
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
					_cateMap.put(key, new Category(key, vals[1].trim(), vals[2].trim(), null));
					_mobileCateMap.put(key, new Category(key, vals[1].trim(), vals[2].trim(), null));
				} else if (LINK_TYPE.equals(arg0) ) {
					if (vals.length < 4) {
						log.error("This category has no enough argument: size less than 4, for example, LINK,IconURL,Label,Href");
						continue;
					}
					_cateMap.put(key, new Category(key, vals[1].trim(), vals[2].trim(), vals[3].trim()));
					_mobileCateMap.put(key, new Category(key, vals[1].trim(), vals[2].trim(), vals[3].trim()));
				} else {
					Category cate = _cateMap.get(arg0);
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
					
					//since ZK 6.5
					//if item has the fifth val, ignore it on tablet
					if (vals.length == 4 || !"ignoreMobile".equals(vals[4].trim())) {
						_mobileCateMap.get(arg0).addItem(item);
					}
				}
			}			
			bufReader.close();
		} catch (IOException e) {
			log.error("Ingored: failed to load a properties file, \nCause: "
					+ e.getMessage());
		}
	}	
}