/* ResourceInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 30 18:27:16     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.util.resource;

import java.io.File;
import java.net.URL;

/** Represents a resource.
 * Note: we would like to use path as the key while we need file for loading,
 * so we pack them as one object.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/05/29 04:27:44 $
 */
/*package*/ class ResourceInfo {
	/*package*/ final String path;
	/*package*/ final File file;
	/*package*/ final URL url;
	/*package*/ ResourceInfo(String path, File file) {
		if (file == null) throw new IllegalArgumentException("null");
		this.path = path;
		this.file = file;
		this.url = null;
	}
	/*package*/ ResourceInfo(String path, URL url) {
		if (url == null) throw new IllegalArgumentException("url");
		this.path = path;
		this.file = null;
		this.url = url;
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
