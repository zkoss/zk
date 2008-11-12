/* Html.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * A comonent used to embed the browser native content (i.e., HTML tags) into
 * the output sent to the browser. The browser native content is specified by
 * {@link #setContent}.
 * 
 * <p>
 * Notice that {@link Html} generates HTML SPAN to enclose the embedded HTML
 * tags. Thus, you can specify the style ({@link #getStyle}), tooltip
 * {@link #getTooltip} and so on.
 * 
 * <pre>
 * <code><html style=&quot;border: 1px solid blue&quot;><![CDATA[
 * <ul>
 *  <li>It is in a SPAN tag.</li>
 * </ul>
 * ]></html></code>
 * </pre>
 * 
 * <p>
 * The generated HTML tags will look like:
 * 
 * <pre>
 * <code><SPAN id=&quot;xxx&quot; style=&quot;border: 1px solid blue&quot;>
 * <ul>
 *  <li>It is in a SPAN tag.</li>
 * </ul>
 * </SPAN></code>
 * </pre>
 * 
 * <p>
 * Since SPAN is used to enclosed the embedded HTML tags, so the following is
 * incorrect.
 * 
 * <pre>
 * <code><html><![CDATA[
 * <table>
 *  <tr>
 *   <td> <-- Incomplete since it is inside SPAN -->
 * ]></html>
 * <textbox/>
 * <html><![CDATA[
 *   </td>
 *  </tr>
 * </table>
 * ]></html></code>
 * </pre>
 * 
 * <p>
 * If you need to generate the HTML tags directly without enclosing with SPAN,
 * you can use the Native namespace, http://www.zkoss.org/2005/zk/native. Refer
 * to the Developer's Guide for more information.
 * 
 * <p>
 * A non-XUL extension.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Html extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the embedded content (i.e., HTML tags).
	 * <p>
	 * Default: empty ("").
	 */
	public String getContent();

	/**
	 * Sets the embedded content (i.e., HTML tags).
	 */
	public void setContent(String content);

}
