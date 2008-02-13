/* PageConfig.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 14 12:02:36     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
	/** Returns the content that will be generated as the attributes
	 * of the root element, or null if no special attribute is required.
	 * <p>For HTML, the root element is the HTML element.
	 * @see PageCtrl#getRootAttributes
	 */
	public String getRootAttributes();
	/** Returns the content type, or null to use the device default.
	 */
	public String getContentType();
	/** Returns the doc type (&lt;!DOCTYPE&gt;),
	 * or null to use the device default.
	 */
	public String getDocType();
	/** Returns the first line to be generated to the output,
	 * or null if nothing to generate.
	 *
	 * <p>For XML devices, it is usually the xml processing instruction:<br/>
	 * <code>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
	 */
	public String getFirstLine();
	/** Returns if the client can cache the rendered result, or null
	 * to use the device default.
	 */
	public Boolean getCacheable();
	/** Returns the implementation of the expression factory that
	 * is used by this page, or null if
	 * {@link org.zkoss.zk.ui.util.Configuration#getExpressionFactoryClass}
	 * is used.
	 *
	 * <p>The returned class must implement
	 * {@link org.zkoss.xel.ExpressionFactory}.
	 *
	 * @since 3.0.4
	 */
	public Class getExpressionFactoryClass();
}
