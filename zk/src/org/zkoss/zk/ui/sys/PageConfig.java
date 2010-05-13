/* PageConfig.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 14 12:02:36     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.Collection;

/**
 * Represents the info used to initialize a page.
 *
 * @see PageCtrl#init
 * @author tomyeh
 * @since 3.0.0
 */
public interface PageConfig {
	/** Returns the page identifier, or null if auto-generation is required.
	 * It is the identifier set by the appplication developer.
	 * If null, the implementation of {@link PageCtrl#init} shall generate
	 * one automatically.
	 *
	 * <p>Note: if {@link org.zkoss.zk.ui.Page#setId} was called with
	 * a non-empty value before, this method is ignored.
	 */
	public String getId();
	/** Returns the page's UUID. 
	 * This method is used only in the recovering mode
	 * ({@link ExecutionCtrl#isRecovering}).
	 * In other words, it is null if not in the recovering mode.
	 */
	public String getUuid();
	/** Returns the page title, or null if not available (no title at all).
	 *
	 * <p>Note: if {@link org.zkoss.zk.ui.Page#setTitle} was called with
	 * a non-empty value before, this method is ignored.
	 */
	public String getTitle();
	/** Returns the page's CSS style, or null if no special CSS style to
	 * assign.
	 *
	 * <p>Note: if {@link org.zkoss.zk.ui.Page#setStyle} was called with
	 * a non-empty value before, this method is ignored.
	 */
	public String getStyle();
	/** Returns the content of the specified condition
	 * that shall be generated inside the header element
	 * (HTML HEAD), or null if no special header is required.
	 *
	 * <p>For HTML, the header element is the HEAD element.
	 *
	 * @param before whether to return the headers that shall be shown
	 * before ZK's CSS/JS headers.
	 * If true, only the headers that shall be shown before (such as meta)
	 * are returned.
	 * If true, only the headers that shall be shown after (such as link)
	 * are returned.
	 * @see #getHeaders()
	 * @since 3.6.1
	 */
	public String getHeaders(boolean before);
	/** Returns all content that will be generated inside the header element
	 * (HTML HEAD), or null if no special header is required.
	 * <p>For HTML, the header element is the HEAD element.
	 * <p>It returns all header no matter it shall be shown before or
	 * after ZK's CSS/JS headers. To have more control, use
	 * {@link #getHeaders(boolean)} instead.
	 *
	 * @see PageCtrl#getHeaders
	 * @see #getHeaders(boolean)
	 */
	public String getHeaders();

	/** Returns a readonly collection of response headers (never null).
	 * The entry is a three-element object array.
	 * The first element is the header name.
	 * The second element of the array is the value which is an instance of
	 * {@link java.util.Date} or {@link String} (and never null).
	 * The third element indicates whether to add (rather than set)
	 * theader. It is an instance of Boolean (and never null).
	 * @since 5.0.2
	 */
	public Collection getResponseHeaders();
}
