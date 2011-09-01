/* AuBookmark.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 29 18:50:13     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to bookmark the desktop.
 *
 * <p>data[0]: the name of the bookmark.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuBookmark extends AuResponse {
	/**
	 * @param name the bookmark name.
	 */
	public AuBookmark(String name) {
		this(name, false); //component-independent
	}
	/**
	 * @param name the bookmark name.
	 */
	public AuBookmark(String name, boolean replace) {
		super("bookmark", new Object[] {name, Boolean.valueOf(replace)}); //component-independent
	}

	/** Default: zk.bookmark (i.e., only one response of this class will
	 * be sent to the client in an execution)
	 * @since 5.0.2
	 */
	public final String getOverrideKey() {
		return "zk.bookmark";
	}
}
