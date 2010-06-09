/* Html.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
 * &lt;code&gt;&lt;html style=&quot;border: 1px solid blue&quot;&gt;&lt;![CDATA[
 * &lt;ul&gt;
 *  &lt;li&gt;It is in a SPAN tag.&lt;/li&gt;
 * &lt;/ul&gt;
 * ]&gt;&lt;/html&gt;&lt;/code&gt;
 * </pre>
 * 
 * <p>
 * The generated HTML tags will look like:
 * 
 * <pre>
 * &lt;code&gt;&lt;SPAN id=&quot;xxx&quot; style=&quot;border: 1px solid blue&quot;&gt;
 * &lt;ul&gt;
 *  &lt;li&gt;It is in a SPAN tag.&lt;/li&gt;
 * &lt;/ul&gt;
 * &lt;/SPAN&gt;&lt;/code&gt;
 * </pre>
 * 
 * <p>
 * Since SPAN is used to enclosed the embedded HTML tags, so the following is
 * incorrect.
 * 
 * <pre>
 * &lt;code&gt;&lt;html&gt;&lt;![CDATA[
 * &lt;table&gt;
 *  &lt;tr&gt;
 *   &lt;td&gt; &lt;-- Incomplete since it is inside SPAN --&gt;
 * ]&gt;&lt;/html&gt;
 * &lt;textbox/&gt;
 * &lt;html&gt;&lt;![CDATA[
 *   &lt;/td&gt;
 *  &lt;/tr&gt;
 * &lt;/table&gt;
 * ]&gt;&lt;/html&gt;&lt;/code&gt;
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
