/* PageDefinitions.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 12:34:43     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import java.net.URL;
import javax.servlet.ServletContext;

import org.zkoss.lang.D;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.resource.ResourceCache;
import org.zkoss.idom.Document;
import org.zkoss.web.util.resource.ServletContextLocator;
import org.zkoss.web.util.resource.ResourceCaches;
import org.zkoss.web.util.resource.ResourceLoader;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.metainfo.Parser;

/**
 * Utilities to retrieve page definitions.
 *
 * @author tomyeh
 */
public class PageDefinitions {
	private static final String ATTR_PAGE_CACHE = "org.zkoss.zk.ui.PageCache";

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
	 * an language. In other words, if
	 * the content doesn't specify an language, {@link LanguageDefinition#getByExtension}
	 * is called.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
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
	 * @param extension the default extension if the content doesn't specify
	 * an language. In other words, if
	 * the content doesn't specify an language, {@link LanguageDefinition#getByExtension}
	 * is called.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
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
	 * @param extension the default extension if the content doesn't specify
	 * an language. In other words, if
	 * the content doesn't specify an language, {@link LanguageDefinition#getByExtension}
	 * is called.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
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
		wapp.getConfiguration().invokeURIInterceptors(path);
			//give the security a chance to reject

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
	 * If not specified, {@link org.zkoss.zk.ui.Desktop#getCurrentDirectory}
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
					cache = new ResourceCache(new MyLoader(wapp), 167);
					cache.setMaxSize(1024);
					cache.setLifetime(60*60000); //1hr
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
			return new Parser(_wapp, locator).parse(file, path);
		}
		protected Object parse(String path, URL url, Object extra)
		throws Exception {
			final Locator locator =
				extra != null ? (Locator)extra: getLocator(_wapp, path);
			return new Parser(_wapp, locator).parse(url, path);
		}
	}
}
