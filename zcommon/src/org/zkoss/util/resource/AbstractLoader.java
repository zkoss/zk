/* AbstractLoader.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 09:45:42     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * A skeletal implementation that assumes the source is either URL or File.
 *
 * @author tomyeh
 */
abstract public class AbstractLoader implements Loader {
	//-- Loader --//
	public boolean shallCheck(Object src, long expiredMillis) {
		return expiredMillis > 0;
		//FUTURE: prolong if src.url's protocol is http, https or ftp
	}
	public long getLastModified(Object src) {
		if (src instanceof URL) {
		//Due to round-trip, we don't retrieve last-modified
			final URL url = (URL)src;
			final String protocol = url.getProtocol().toLowerCase();
			if (!"http".equals(protocol) && !"https".equals(protocol)
			&& !"ftp".equals(protocol)) {
				try {
					return url.openConnection().getLastModified();
				} catch (IOException ex) {
					return -1; //reload
				}
			}
			return -1; //reload
		} else if (src instanceof File) {
			return ((File)src).lastModified();
		} else if (src == null) {
			throw new NullPointerException();
		} else {
			throw new IllegalArgumentException("Unknown soruce: "+src+"\nOnly File and URL are supported");
		}
	}
}
