/* Style.java

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
 * The style component used to specify CSS styles for the owner desktop.
 * 
 * <p>
 * Note: a style component can appear anywhere in a ZUML page, but it affects
 * all components in the same desktop.
 * 
 * <p>
 * Note: If {@link #isDynamic} is false, the HTML STYLE or LINK tag is generated
 * to represent this component. Due to IE's limitation, there is no effect if
 * the style component is added or removed dynamically and if {@link #isDynamic}
 * is false.
 * 
 * <p>
 * If {@link #isDynamic} is true, this component can be added and removed
 * dynamically and the rules will be attached and detached accordingly. Note: in
 * this case, the link is generated when this component is initialized at the
 * client, so the style will be loaded to the client after all components are
 * initialized.
 * 
 * <p>
 * There are three formats when used in a ZUML page:
 * 
 * <p>
 * Method 1: Specify the URL of the CSS file
 * 
 * <pre>
 * &lt;code&gt;&lt;style src=&quot;my.css&quot;/&gt;
 * &lt;/code&gt;
 * </pre>
 * 
 * <p>
 * Method 2: Specify the CSS directly
 * 
 * <pre>
 * &lt;code&gt;&lt;style&gt;
 * .mycls {
 *  border: 1px outset #777;
 * }
 * &lt;/style&gt;
 * &lt;/code&gt;
 * </pre>
 * 
 * <p>
 * Method 3: Specify the CSS by use of the content property ({@link #setContent}
 * ).
 * 
 * <pre>
 * &lt;code&gt;&lt;style&gt;
 * &lt;attribute name=&quot;content&quot;&gt;
 * .mycls {
 *  border: 1px outset #777;
 * }
 * &lt;/attribute&gt;
 * &lt;/style&gt;
 * &lt;/code&gt;
 * </pre>
 * 
 * <p>
 * Note: if the src and content properties are both set, the content property is
 * ignored.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Style {

	/** @deprecated As of release 5.0.0, it is decided automatically.
	 */
	public void setDynamic(boolean dynamic);
	/** @deprecated As of release 5.0.0, it is decided automatically.
	 */
	public boolean isDynamic();

	/**
	 * Returns the URI of an external style sheet.
	 * 
	 * <p>
	 * Default: null.
	 */
	public String getSrc();

	/**
	 * Sets the URI of an external style sheet.
	 * 
	 * <p>
	 * Calling this method implies setContent(null). In other words, the last
	 * invocation of {@link #setSrc} overrides the previous {@link #setContent},
	 * if any.
	 * 
	 * @param src
	 *            the URI of an external style sheet
	 * @see #setContent
	 */
	public void setSrc(String src);

	/**
	 * Returns the content of the style element. By content we mean the CSS
	 * rules that will be sent to the client.
	 * 
	 * <p>
	 * Default: null.
	 * 
	 */
	public String getContent();

	/**
	 * Sets the content of the style element. By content we mean the CSS rules
	 * that will be sent to the client.
	 * 
	 * <p>
	 * Calling this method implies setSrc(null). In other words, the last
	 * invocation of {@link #setContent} overrides the previous {@link #setSrc},
	 * if any.
	 * 
	 * @see #setSrc
	 */
	public void setContent(String content);

	/** Returns the media dependencies for this style sheet.
	 *
	 * <p>Default: null.
	 * <p>Refer to <a href="http://www.w3.org/TR/CSS2/media.html">media-depedent style sheet</a> for details.
	 * @since 5.0.3
	 */
	public String getMedia();
	/** Sets the media dependencies for this style sheet.
	 * <p>Refer to <a href="http://www.w3.org/TR/CSS2/media.html">media-depedent style sheet</a> for details.
	 *
	 * @param media the media dependencies for this style sheet
	 * @since 5.0.3
	 */
	public void setMedia(String media);
}
