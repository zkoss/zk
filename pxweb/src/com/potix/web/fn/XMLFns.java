/* XMLFns.java

{{IS_NOTE
	$Id: XMLFns.java,v 1.4 2006/02/27 03:54:27 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Mar 31 14:16:40     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.fn;

/**
 * Utilities to manipulate XML/HTML for EL.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/02/27 03:54:27 $
 */
public class XMLFns {
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
