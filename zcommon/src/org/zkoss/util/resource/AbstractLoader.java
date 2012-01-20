/* AbstractLoader.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 09:45:42     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.resource;

import java.io.File;
import java.net.URL;

/**
 * A skeletal implementation that assumes the source is either URL or File.
 *
 * @author tomyeh
 */
abstract public class AbstractLoader<K, V> implements Loader<K, V> {
	//-- Loader --//
	public boolean shallCheck(K src, long expiredMillis) {
		return expiredMillis > 0;
		//FUTURE: prolong if src.url's protocol is http, https or ftp
	}
	public long getLastModified(K src) {
		if (src instanceof URL) {
			try {
				final long v = ((URL)src).openConnection().getLastModified();
				return v != -1 ? v: 0; //not to reload if unknown (5.0.6 for better performance)
			} catch (Throwable ex) {
				return -1; //reload (might be removed)
			}
		} else if (src instanceof File) {
			final long v = ((File)src).lastModified();
			return v == -1 ? 0: //not to reload if unknown (5.0.6 for better performance)
				v == 0 ? -1: v; //0 means nonexistent so reload
		} else if (src == null) {
			throw new NullPointerException();
		} else {
			throw new IllegalArgumentException("Unknown soruce: "+src+"\nOnly File and URL are supported");
		}
	}
}
