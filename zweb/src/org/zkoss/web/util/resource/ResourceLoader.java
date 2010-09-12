/* ResourceLoader.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 30 18:31:26     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;

import org.zkoss.lang.D;
import org.zkoss.util.resource.Loader;
import org.zkoss.util.logging.Log;

/**
 * A semi-implemented loader to used with {@link ResourceCaches#get}
 * to retrieve servlet resources.
 *
 * @author tomyeh
 */
abstract public class ResourceLoader<V> implements Loader<ResourceInfo, V> {
	private static final Log log = Log.lookup(ResourceLoader.class);

	protected ResourceLoader() {
	}

	/** Parses the specified file and returns the result which
	 * will be stored into the cache ({@link ResourceCaches#get}).
	 *
	 * <p>Deriving must override this method.
	 *
	 * @param extra the extra paramter passed from {@link ResourceCaches#get}.
	 */
	abstract protected V parse(String path, File file, Object extra)
	throws Exception;
	/** Parses the specified URL and returns the result which
	 * will be stored into the cache ({@link ResourceCaches#get}).
	 *
	 * <p>Deriving must override this method.
	 *
	 * @param extra the extra paramter passed from {@link ResourceCaches#get}.
	 */
	abstract protected V parse(String path, URL url, Object extra)
	throws Exception;

	public boolean shallCheck(ResourceInfo src, long expiredMillis) {
		return expiredMillis > 0;
		//FUTURE: prolong if src.url's protocol is http, https or ftp
	}
	public long getLastModified(ResourceInfo src) {
		if (src.url != null) {
		//Due to round-trip, we don't retrieve last-modified
			final String protocol = src.url.getProtocol().toLowerCase();
			if (!"http".equals(protocol) && !"https".equals(protocol)
			&& !"ftp".equals(protocol)) {
				try {
					return src.url.openConnection().getLastModified();
				} catch (Throwable ex) {
					return -1; //reload
				}
			}
			return -1; //reload
		}

		return src.file.lastModified();
	}
	public V load(ResourceInfo src) throws Exception {
		if (src.url != null)
			return parse(src.path, src.url, src.extra);

		if (!src.file.exists()) {
			if (D.ON && log.debugable()) log.debug("Not found: "+src.file);
			return null; //File not found
		}
		if (D.ON && log.debugable()) log.debug("Loading "+src.file);
		try {
			return parse(src.path, src.file, src.extra);
		} catch (FileNotFoundException ex) {
			return null;
		}
	}
}
