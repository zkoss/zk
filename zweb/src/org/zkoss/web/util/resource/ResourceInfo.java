/* ResourceInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 30 18:27:16     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.util.resource;

import java.io.File;
import java.net.URL;

/** Represents a resource.
 * Note: we would like to use path as the key while we need file for loading,
 * so we pack them as one object.
 * 
 * @author tomyeh
 */
/*package*/ class ResourceInfo {
	/*package*/ final String path;
	/*package*/ final File file;
	/*package*/ final URL url;
	/*package*/ final Object extra;
	/**
	 * @param extra the extra paramter passed from {@link ResourceCaches#get}.
	 */
	/*package*/ ResourceInfo(String path, File file, Object extra) {
		if (file == null) throw new IllegalArgumentException("null");
		this.path = path;
		this.file = file;
		this.url = null;
		this.extra = extra;
	}
	/*package*/ ResourceInfo(String path, URL url, Object extra) {
		if (url == null) throw new IllegalArgumentException("url");
		this.path = path;
		this.file = null;
		this.url = url;
		this.extra = extra;
	}

	//-- Object --//
	public boolean equals(Object o) {
		return (o instanceof ResourceInfo) && path.equals(((ResourceInfo)o).path);
	}
	public int hashCode() {
		return this.path.hashCode();
	}
	public String toString() {
		return this.path;
	}
}
