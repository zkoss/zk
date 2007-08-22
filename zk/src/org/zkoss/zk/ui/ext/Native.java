/* Native.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 16 11:35:01     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

import java.util.Iterator;
import java.util.Map;

/**
 * Implemented with {@link org.zkoss.zk.ui.Component} to represent
 * a native component.
 * The native component is used to implement the feature of the
 * Native namespace (http://www.zkoss.org/2005/zk/native).
 * 
 * @author tomyeh
 * @since 2.5.0
 */
public interface Native extends NonFellow {
	/** Returns the prolog content. It is the content generated
	 * before the child components, if any.
	 * <p>Default: empty ("").
	 */
	public String getPrologContent();
	/** Sets the prolog content. It is the content generated
	 * before the child components, if any.
	 */
	public void setPrologContent(String prolog);
	/** Returns the epilog content. It is the content generated
	 * before the child components, if any.
	 * <p>Default: empty ("").
	 */
	public String getEpilogContent();
	/** Sets the epilog content. It is the content generated
	 * before the child components, if any.
	 */
	public void setEpilogContent(String epilog);

	/** Returns the helper to generate the output of the native components.
	 */
	public Helper getHelper();

	/** The helper to generate the output for the native component.
	 * It usually depends on the client (i.e., {@link org.zkoss.zk.ui.Desktop#getDevice}.
	 */
	public interface Helper {
		/** Generates the first half of the device-dependent content
		 * for the specified tag and properties, and appends it to
		 * the specified string buffer.
		 *
		 * <p>For example, getFirstHalf(sb, "tr", null) appends "&lt;tr&gt;" to sb,
		 * and getFirstHalf(sb, "br", ) appends "&lt;br/&gt;".
		 *
		 * @param sb the string buffer to append the result (never null)
		 * @param tag the tag name (never null)
		 * @param props a map of name and value pairs or null
		 * if no properties at all.
		 * Note: the value doesn't contain any EL expression.
		 */
		public void getFirstHalf(StringBuffer sb, String tag, Map props);
		/** Appends the first half of the device-dependent content
		 * for the specified tag and properties, and appends it to
		 * the specified string buffer.
		 *
		 * <p>For example, appendSecpmdHalf(sb, "tr") appends "&lt;/tr&gt;" to sb,
		 * and getSecondHalf(sb, "br") appends "".
		 *
		 * @param sb the string buffer to append the result (never null)
		 * @param tag the tag name (never null)
		 */
		public void getSecondHalf(StringBuffer sb, String tag);
	}
}
