/* Native.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 16 11:35:01     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.List;

import org.zkoss.idom.Namespace;
import org.zkoss.zk.ui.Component;

/**
 * Implemented with {@link Component} to represent
 * a native component.
 * The native component is used to implement the feature of the
 * Native namespace (http://www.zkoss.org/2005/zk/native).
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public interface Native extends NonFellow {
	/** Returns a readonly list of the declared namespaces
	 * ({@link Namespace}), or empty if no declared namespace.
	 */
	public List<Namespace> getDeclaredNamespaces();
	/** Adds a declared namespace.
	 * The added declared namespace will be generated to the output.
	 *
	 * @param ns the namespace (never null).
	 */
	public void addDeclaredNamespace(Namespace ns);
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
	 * The implementation usually depends on the client (i.e., {@link org.zkoss.zk.ui.Desktop#getDevice}.
	 * <p>Application developers shall not count on this interface. It is
	 * used only for implementing a native component.
	 */
	public interface Helper {
		/** Creates a native component with the specified content.
		 */
		public Component newNative(String text);

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
		 * @param namespaces a list of {@link org.zkoss.idom.Namespace}
		 * to be generated, or null if not.
		 * Note: EL expressions is not allowed
		 */
		public void getFirstHalf(StringBuffer sb, String tag,
		Map<String, Object> props, Collection namespaces);
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
		/** Appends the text.
		 *
		 * @param text the text content to append
		 */
		public void appendText(StringBuffer sb, String text);
	}
}
