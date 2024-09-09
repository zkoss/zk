/* ExtendletLoader.java

	Purpose:

	Description:

	History:
		Wed May 28 17:01:32     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.io.Files;
import org.zkoss.lang.Library;
import org.zkoss.util.URLs;
import org.zkoss.util.resource.Loader;

/**
 * A skeletal implementation of the loader used to implement an extendlet.
 * All you have to do is to implement {@link #parse}
 * and {@link #getExtendletContext}.
 *
 * <p>If the real path is not the same as the path specified in URL,
 * you can override {@link #getRealPath}.
 *
 * @author tomyeh
 * @see Extendlet
 * @since 3.0.6
 */
public abstract class ExtendletLoader<V> implements Loader<String, V> {
	private static final Logger log = LoggerFactory.getLogger(ExtendletLoader.class);

	private int _checkPeriod;

	protected ExtendletLoader() {
		_checkPeriod = getInitCheckPeriod();
	}

	/** Returns the real path for the specified path.
	 *
	 * <p>Default: return path, i.e., the path specified in URL is
	 * the real path.
	 *
	 * <p>Notice that {@link #parse} will receive the original path
	 * (rather than the returned path).
	 *
	 * @param path the path specified in URL.
	 * Notice that it does NOT start with "~./". Rather it starts with
	 * "/". For example, "/zul/css/zk.wcs".
	 * @since 5.0.0
	 */
	protected String getRealPath(String path) {
		return path;
	}

	//Loader//
	public boolean shallCheck(String src, long expiredMillis) {
		return expiredMillis > 0;
	}

	/** Returns the last modified time.
	 */
	public long getLastModified(String src) {
		if (getCheckPeriod() < 0)
			return 1; //any value (because it is not dynamic)

		URLConnection conn = null;
		try {
			URL url = getExtendletContext().getResource(src);
			if (url != null) {
				// prevent SSRF warning
				url = URLs.sanitizeURL(url);
				conn = url.openConnection();
				final long v = conn.getLastModified();
				return v != -1 ? v : 0; //not to reload (5.0.6 for better performance)
			}
		} catch (Throwable ex) {
			// expected
		} finally {
			if (conn != null) {
				try {
					conn.getInputStream().close();
				} catch (Throwable e) {
					log.warn("The connection cannot be closed", e);
				}
			}
		}
		return -1; //reload (might be removed)
	}

	public V load(String src) throws Exception {
		if (log.isDebugEnabled())
			log.debug("Parse {}", src);
		final String path = getRealPath(src);
		InputStream is = null;
		if (getCheckPeriod() >= 0) {
			//Due to Web server might cache the result, we use URL if possible
			try {
				URL real = getExtendletContext().getResource(path);
				if (real != null) {
					// prevent SSRF warning
					real = URLs.sanitizeURL(real);
					is = real.openStream();
				}
			} catch (Throwable ex) {
				log.warn("Unable to read from URL: {}", path, ex);
			}
		}

		if (is == null) {
			is = getExtendletContext().getResourceAsStream(path);
			if (is == null)
				return null;
		}

		try {
			return parse(is, path, src);
		} catch (Throwable ex) {
			log.error("Failed to parse " + src, ex);
			return null; //as non-existent
		} finally {
			Files.close(is);
		}
	}

	//Derive to override//
	/** It is called to parse the resource into an intermediate format
	 * depending on {@link Extendlet}.
	 *
	 * <p>The object is returned directly by {@link #load}, so
	 * you can return an instance of org.zkoss.util.resource.Loader.Resource
	 * to have more control on {@link org.zkoss.util.resource.ResourceCache}.
	 *
	 * @param is the content of the resource
	 * @param path the path of the resource.
	 * It is the value returned by {@link #getRealPath}, so called
	 * the real path
	 * @param orgpath the original path.
	 * It is the path passed to the <code>path</code> argument
	 * of {@link #getRealPath}. It is useful if you want to retrieve
	 * the additional information encoded into the URI.
	 * @since 5.0.0
	 */
	protected abstract V parse(InputStream is, String path, String orgpath) throws Exception;

	/** Returns the extendlet context.
	 */
	protected abstract ExtendletContext getExtendletContext();

	/** Returns the check period, or -1 if the content is never changed.
	 * Unit: milliseconds.
	 *
	 * <p>Default: It checks if an integer (unit: second) is assigned
	 * to a system property called org.zkoss.util.resource.extendlet.checkPeriod.
	 * If no such system property, -1 is assumed (never change).
	 * For the runtime environment the content is never changed,
	 * since all extendlet resources are packed in JAR files.
	 */
	public int getCheckPeriod() {
		return _checkPeriod;
	}

	private static int getInitCheckPeriod() {
		final int v = Library.getIntProperty("org.zkoss.util.resource.extendlet.checkPeriod", -1);
		return v > 0 ? v * 1000 : v;
	}
}
