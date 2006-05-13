/* ContentLoader.java

{{IS_NOTE
	$Id: ContentLoader.java,v 1.4 2006/02/27 03:42:06 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 12:40:06     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.resource;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;

import com.potix.io.Files;

/**
 * A {@link Loader} that loads the resource by use URL.getContent()
 * if the source is URL, or loads into a String if the source is a File
 * (and assumging UTF-8).
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/02/27 03:42:06 $
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
		return Files.readAll(new InputStreamReader(is, "UTF-8")).toString();
	}
}
