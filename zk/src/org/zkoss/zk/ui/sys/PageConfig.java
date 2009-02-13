/* PageConfig.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 14 12:02:36     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

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
	/** Returns the content that will be generated inside the header element,
	 * or null if no special header is required.
	 * <p>For HTML, the header element is the HEAD element.
	 * @see PageCtrl#getHeaders
	 */
	public String getHeaders();
}
