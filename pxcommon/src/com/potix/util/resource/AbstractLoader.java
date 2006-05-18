/* AbstractLoader.java

{{IS_NOTE
	$Id: AbstractLoader.java,v 1.5 2006/02/27 03:42:06 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 09:45:42     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * A skeletal implementation that assumes the source is either URL or File.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/02/27 03:42:06 $
 */
abstract public class AbstractLoader implements Loader {
	//-- Loader --//
	public long getLastModified(Object src) {
		if (src instanceof URL) {
			try {
				return ((URL)src).openConnection().getLastModified();
			} catch (IOException ex) {
				return -1;
			}
		} else if (src instanceof File) {
			return ((File)src).lastModified();
		} else if (src == null) {
			throw new NullPointerException();
		} else {
			throw new IllegalArgumentException("Unknown soruce: "+src+"\nOnly File and URL are supported");
		}
	}
}
