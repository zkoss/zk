/* XmlFns.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Mar 31 14:16:40     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.fn;

/**
 * Functions to manipulate XML/HTML for EL.
 *
 * @author tomyeh
 * @since 3.5.0
 */
public class XmlFns {
	/** Generates an attribute for HTML/XML.
	 * If val is null or empty (if String), nothing is generated.
	 */
	public static final String attr(String name, Object val) {
		if (val == null
		|| (val instanceof String && ((String)val).length() == 0))
			return "";

		return " "+ name + "=\"" + val + '"';
	}
}
