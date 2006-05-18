/* ResourceLoader.java

{{IS_NOTE
	$Id: ResourceLoader.java,v 1.4 2006/04/17 14:34:13 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Aug 30 18:31:26     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.util.resource;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;

import com.potix.lang.D;
import com.potix.util.resource.Loader;
import com.potix.util.logging.Log;

/**
 * A semi-implemented loader to used with {@link ResourceCaches#get}
 * to retrieve servlet resources.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/04/17 14:34:13 $
 */
abstract public class ResourceLoader implements Loader {
	private static final Log log = Log.lookup(ResourceLoader.class);

	protected ResourceLoader() {
	}

	/** Parses the specified file and returns the result which
	 * will be stored into the cache ({@link ResourceCaches#get}).
	 *
	 * <p>Deriving must override this method.
	 */
	abstract protected Object parse(String path, File file) throws Exception;
	/** Parses the specified URL and returns the result which
	 * will be stored into the cache ({@link ResourceCaches#get}).
	 *
	 * <p>Deriving must override this method.
	 */
	abstract protected Object parse(String path, URL url) throws Exception;

	public long getLastModified(Object src) {
		final ResourceInfo si =(ResourceInfo)src;
		if (si.url != null) {
			try {
				return si.url.openConnection().getLastModified();
			} catch (IOException ex) {
				return -1;
			}
		}

		return si.file.lastModified();
	}
	public Object load(Object src) throws Exception {
		final ResourceInfo si =(ResourceInfo)src;
		if (si.url != null)
			return parse(si.path, si.url);

		if (!si.file.exists()) {
			if (D.ON && log.debugable()) log.debug("Not found: "+si.file);
			return null; //File not found
		}
		if (D.ON && log.debugable()) log.debug("Loading "+si.file);
		try {
			return parse(si.path, si.file);
		} catch (FileNotFoundException ex) {
			return null;
		}
	}
}
