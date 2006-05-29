/* PageDefinitions.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Apr 24 13:21:29     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.http;

import java.io.File;
import java.net.URL;
import javax.servlet.ServletContext;

import com.potix.util.prefs.Apps;
import com.potix.util.resource.Locator;
import com.potix.util.resource.ResourceCache;

import com.potix.web.util.resource.ServletContextLocator;
import com.potix.web.util.resource.ResourceCaches;
import com.potix.web.util.resource.ResourceLoader;

import com.potix.zk.ui.Execution;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.metainfo.Parser;
import com.potix.zk.ui.metainfo.PageDefinition;

/**
 * Utilities to retrieve metainfos.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class PageDefinitions extends com.potix.zk.ui.metainfo.PageDefinitions {
	private static final String ATTR_PAGE_CACHE = "com.potix.zk.ui.PageCache";

	/** Returns the page definition of the specified path, or null if not
	 * found or failed to parse.
	 */
	public static final
	PageDefinition getPageDefinition(ServletContext ctx, String path) {
		return (PageDefinition)ResourceCaches.get(
			getCache(ctx), ctx, path);
	}
	/** Returns the locator for the specified context.
	 * @param path the original path, or null if not available.
	 * The original path is used to resolve a relative path.
	 * If not specified, {@link com.potix.zk.ui.Desktop#getCurrentDirectory}
	 * is used.
	 */
	public static final Locator getLocator(ServletContext ctx, String path) {
		if (ctx == null) throw new IllegalArgumentException("null");
	
		if (path != null && path.length() > 0 && path.charAt(0) == '/') {
			final int j = path.lastIndexOf('/');
			path = j > 0 ? path.substring(0, j + 1): "/";
		} else {
			final Execution exec = Executions.getCurrent();
			if (exec != null) path = exec.getDesktop().getCurrentDirectory();
		}
		return new ServletContextLocator(ctx, path);
	}

	private static final ResourceCache getCache(ServletContext ctx) {
		ResourceCache cache = (ResourceCache)ctx.getAttribute(ATTR_PAGE_CACHE);
		if (cache == null) {
			synchronized (PageDefinitions.class) {
				cache = (ResourceCache)ctx.getAttribute(ATTR_PAGE_CACHE);
				if (cache == null) {
					cache = new ResourceCache(new MyLoader(ctx), 29);
					cache.setMaxSize(1000).setLifetime(60*60000); //1hr
					cache.setCheckPeriod(
						Apps.getInteger("com.potix.web.file.checkPeriod", 5) * 1000);
					ctx.setAttribute(ATTR_PAGE_CACHE, cache);
				}
			}
		}
		return cache;
	}

	private static class MyLoader extends ResourceLoader {
		private final ServletContext _ctx;
		private MyLoader(ServletContext ctx) {
			_ctx = ctx;
		}
		//-- super --//
		protected Object parse(String path, File file) throws Exception {
			return new Parser(getLocator(_ctx, path)).parse(file);
		}
		protected Object parse(String path, URL url) throws Exception {
			return new Parser(getLocator(_ctx, path)).parse(url);
		}
	}
}
