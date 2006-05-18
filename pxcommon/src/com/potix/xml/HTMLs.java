/* HTMLs.java

{{IS_NOTE
	$Id: HTMLs.java,v 1.6 2006/03/20 09:36:43 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Sat Dec 31 12:46:27     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.xml;

import java.util.Set;
import java.util.HashSet;

/**
 * Utilities for HTML attributes and styles.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.6 $ $Date: 2006/03/20 09:36:43 $
 */
public class HTMLs {
	/** Appends an attribute to the string buffer for HTML/XML (name="val").
	 * If val is null or empty (if String), nothing is generated.
	 *
	 * <p>Note: {@link XMLs#encodeAttribute} is called automatically
	 * to encode val.
	 */
	public static final
	void appendAttribute(StringBuffer sb, String name, String val) {
		if (val != null && val.length() != 0)
			sb.append(' ').append(name).append("=\"")
				.append(XMLs.encodeAttribute(val)).append('"');
	}
	/** Appends an attribute to the string buffer for HTML/XML (name="val").
	 */
	public static final
	void appendAttribute(StringBuffer sb, String name, int val) {
		sb.append(' ').append(name).append("=\"").append(val).append('"');
	}
	/** Appends an attribute to the string buffer for HTML/XML (name="val").
	 */
	public static final
	void appendAttribute(StringBuffer sb, String name, boolean val) {
		sb.append(' ').append(name).append("=\"").append(val).append('"');
	}
	/** Appends a style value to the string buffer for HTML/XML (name:"val";).
	 * If val is null or empty (if String), nothing is generated.
	 */
	public static final
	void appendStyle(StringBuffer sb, String name, String val) {
		if (val != null && val.length() != 0)
			sb.append(name).append(':').append(val).append(';');
	}

	/** Retrieves text relevant CSS styles.
	 *
	 * <p>For example, if style is
	 * "border: 1px solid blue; font-size: 10px; padding: 3px; color: black;",
	 * then "font-size: 10px;color: black;" is returned.
	 *
	 * @return null if style is null. Otherwise, it never returns null.
	 */
	public static final String getTextRelevantStyle(String style) {
		if (style == null) return null;
		if (style.length() == 0) return "";

		final StringBuffer sb = new StringBuffer(64);
		for (int j = 0, l = 0; l >= 0; j = l + 1) {
			final int k = style.indexOf(':', j);
			l = k >= 0 ? style.indexOf(';', k + 1): -1;

			final String nm =
				(k >= 0 ? style.substring(j, k): style.substring(j)).trim();
			if (nm.startsWith("font")  || nm.startsWith("text")
			|| _txtstyles.contains(nm))
				sb.append(
					l >= 0 ? style.substring(j, l + 1): style.substring(j));
		}
		return sb.toString();
	}
	private final static Set _txtstyles;
	static {
		final String[] txts = {
			"color", "background-color", "background", "white-space"
		};
		_txtstyles = new HashSet();
		for (int j = txts.length; --j >=0;)
			_txtstyles.add(txts[j]);
	}
}
