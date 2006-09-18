/* PageDefinitions.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 12:34:43     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.metainfo;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import java.net.URL;
import javax.servlet.ServletContext;

import com.potix.lang.D;
import com.potix.util.resource.Locator;
import com.potix.util.resource.ResourceCache;
import com.potix.idom.Document;
import com.potix.web.util.resource.ServletContextLocator;
import com.potix.web.util.resource.ResourceCaches;
import com.potix.web.util.resource.ResourceLoader;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.metainfo.Parser;

/**
 * Utilities to retrieve page definitions.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class PageDefinitions {
	private static final String ATTR_PAGE_CACHE = "com.potix.zk.ui.PageCache";

	/** Returns the page definition of the specified raw content; never null.
	 *
	 * <p>This is the lowest method that other getPageDefinitionDirectly depends.
	 *
	 * <p>Dependency: Execution.createComponentsDirectly -&amp; Execution.getPageDefinitionDirectly
	 * -&amp; UiFactory.getPageDefiitionDirectly -&amp; PageDefintions.getPageDefinitionDirectly
	 *
	 * @param locator the locator used to locate taglib and other resources.
	 * If null, wapp is assumed ({@link WebApp} is also assumed).
	 * @param extension the default extension if the content doesn't specify
	 * an language. Ignored if null.
	 * If the content doesn't specify an language, {@link LanguageDefinition#lookupByExtension}
	 * is called.
	 * @exception UiException if failed to parse
	 */
	public static final
	PageDefinition getPageDefinitionDirectly(WebApp wapp, Locator locator,
	String content, String extension) {
		try {
			return getPageDefinitionDirectly(
				wapp, locator, new StringReader(content), extension);
		} catch (IOException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	/** Returns the page definition of the raw content from the specified
	 * reader; never null.
	 *
	 * @param locator the locator used to locate taglib and other resources.
	 * If null, wapp is assumed ({@link WebApp} is also assumed).
	 * @param extension the default extension if the content (of reader) doesn't specify
	 * an language. Ignored if null.
	 * If the content doesn't specify an language, {@link LanguageDefinition#lookupByExtension}
	 * is called.
	 * @exception UiException if failed to parse
	 */
	public static final
	PageDefinition getPageDefinitionDirectly(WebApp wapp, Locator locator,
	Reader reader, String extension) throws IOException {
		try {
			return new Parser(wapp, locator).parse(reader, extension);
		} catch (IOException ex) {
			throw (IOException)ex;
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	/** Returns the page definition of the specified raw content in DOM;
	 * never null.
	 *
	 * @param locator the locator used to locate taglib and other resources.
	 * If null, wapp is assumed ({@link WebApp} is also assumed).
	 * @param extension the default extension if doc doesn't specify
	 * an language. Ignored if null.
	 * If doc doesn't specify an language, {@link LanguageDefinition#lookupByExtension}
	 * is called.
	 * @exception UiException if failed to parse
	 */
	public static final
	PageDefinition getPageDefinitionDirectly(WebApp wapp, Locator locator,
	Document doc, String extension) {
		try {
			return new Parser(wapp, locator).parse(doc, extension);
		} catch (Exception ex) {
			throw UiException.Aide.wrap(ex);
		}
	}

	/** Returns the page definition of the specified path, or null if not
	 * found or failed to parse.
	 *
	 * <p>This is the lowest method that other getPageDefinition depends.
	 *
	 * <p>Dependency: Execution.createComponents -&amp; Execution.getPageDefinition
	 * -&amp; UiFactory.getPageDefiition -&amp; PageDefintions.getPageDefinition
	 *
	 * @param locator the locator used to locate taglib and other resources.
	 * If null, wapp is assumed ({@link WebApp} is also assumed).
	 */
	public static final
	PageDefinition getPageDefinition(WebApp wapp, Locator locator, String path) {
		final Object ctx = wapp.getNativeContext();
		if (ctx instanceof ServletContext)
			return (PageDefinition)ResourceCaches.get(
				getCache(wapp), (ServletContext)ctx, path, locator);
		throw new UnsupportedOperationException("Unknown context: "+ctx);
	}
	/** Returns the locator for the specified context.
	 *
	 * @param path the original path, or null if not available.
	 * The original path is used to resolve a relative path.
	 * If not specified, {@link com.potix.zk.ui.Desktop#getCurrentDirectory}
	 * is used.
	 */
	public static final Locator getLocator(WebApp wapp, String path) {
		if (wapp == null) throw new IllegalArgumentException("null");
	
		if (path != null && path.length() > 0 && path.charAt(0) == '/') {
			final int j = path.lastIndexOf('/');
			path = j > 0 ? path.substring(0, j + 1): "/";
		} else {
			final Execution exec = Executions.getCurrent();
			if (exec != null) path = exec.getDesktop().getCurrentDirectory();
		}
		final Object ctx = wapp.getNativeContext();
		if (ctx instanceof ServletContext)
			return new ServletContextLocator((ServletContext)ctx, path);
		throw new UnsupportedOperationException("Unknown context: "+ctx);
	}

	private static final ResourceCache getCache(WebApp wapp) {
		ResourceCache cache = (ResourceCache)wapp.getAttribute(ATTR_PAGE_CACHE);
		if (cache == null) {
			synchronized (PageDefinitions.class) {
				cache = (ResourceCache)wapp.getAttribute(ATTR_PAGE_CACHE);
				if (cache == null) {
					cache = new ResourceCache(new MyLoader(wapp), 29);
					cache.setMaxSize(1000).setLifetime(60*60000); //1hr
					wapp.setAttribute(ATTR_PAGE_CACHE, cache);
				}
			}
		}
		return cache;
	}

	private static class MyLoader extends ResourceLoader {
		private final WebApp _wapp;
		private MyLoader(WebApp wapp) {
			_wapp = wapp;
		}

		//-- super --//
		protected Object parse(String path, File file, Object extra)
		throws Exception {
			final Locator locator =
				extra != null ? (Locator)extra: getLocator(_wapp, path);
			return new Parser(_wapp, locator).parse(file);
		}
		protected Object parse(String path, URL url, Object extra)
		throws Exception {
			final Locator locator =
				extra != null ? (Locator)extra: getLocator(_wapp, path);
			return new Parser(_wapp, locator).parse(url);
		}
	}
}
