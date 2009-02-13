/* ForEach.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar  8 11:12:53     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

/**
 * Used to denote a collection of elements.
 * Currently, only {@link org.zkoss.zk.ui.metainfo.ComponentInfo}
 * uses it to represent the forEach attribute.
 *
 * @author tomyeh
 */
public interface ForEach {
	/** Advanced to the next element.
	 *
	 * @return false if there is no more element.
	 */
	public boolean next();
}
