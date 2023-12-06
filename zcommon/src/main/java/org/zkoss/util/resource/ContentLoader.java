/* ContentLoader.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 12:40:06     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.resource;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.zkoss.io.Files;

/**
 * A {@link Loader} that loads the resource by use URL.getContent()
 * if the source is URL, or loads into a String if the source is a File
 * (and assuming UTF-8).
 *
 * @author tomyeh
 */
public class ContentLoader extends AbstractLoader<Object, String> {
	//-- Loader --//
	public String load(Object src) throws Exception {
		final InputStream is;
		if (src instanceof URL) {
			// prevent SSRF warning
			URL url = ((URL)src);
			url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
			is = url.openStream();
		} else if (src instanceof File) {
			is = new FileInputStream((File)src);
		} else if (src == null) {
			throw new NullPointerException();
		} else {
			throw new IllegalArgumentException("Unknown soruce: "+src+"\nOnly File and URL are supported");
		}
		try {
			return Files.readAll(new InputStreamReader(is, "UTF-8")).toString();
		} finally {
			Files.close(is);
		}
	}
}
