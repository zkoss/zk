/* ContentLoader.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 12:40:06     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.resource;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;

import org.zkoss.io.Files;

/**
 * A {@link Loader} that loads the resource by use URL.getContent()
 * if the source is URL, or loads into a String if the source is a File
 * (and assumging UTF-8).
 *
 * @author tomyeh
 */
public class ContentLoader extends AbstractLoader {
	//-- Loader --//
	public Object load(Object src) throws Exception {
		final InputStream is;
		if (src instanceof URL) {
			is = ((URL)src).openStream();
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
			try {is.close();} catch (Throwable ex) {}
		}
	}
}
