/* URLEncoder.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 14 12:48:46     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.net;

/**
 * A plugin to allow developer to customize the encoding of URL.
 *
 * <p>By default, java.net.URLEncoder.encode(s, "UTF-8") is used.
 * If it is not what you want, you can customize it by implementing this
 * interface and specify it in the library property called
 * <code>org.zkoss.net.URLEncoder</code>.
 *
 * @author tomyeh
 * @since 3.6.3
 */
public interface URLEncoder {
	/** Translates a string into x-www-form-urlencoded  format.
	 *
	 * <p>Notice that Note: The  World Wide Web Consortium Recommendation
	 * states that UTF-8 should be used. Not doing so may introduce incompatibilites.
	 *
	 * @param s the string to be translated
	 */
	public String encode(String s);
}
