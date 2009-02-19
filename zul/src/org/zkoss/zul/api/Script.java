/* Script.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * A component to represent script codes running at the client. It is the same
 * as HTML SCRIPT tag.
 * 
 * <p>
 * Note: it is the scripting codes running at the client, not at the server.
 * Don't confuse it with the <code>zscript</code> element.
 * 
 * <p>
 * There are three formats when used in a ZUML page:
 * 
 * <p>
 * Method 1: Specify the URL of the JS file
 * 
 * <pre>
 * &lt;code&gt;&lt;script type=&quot;text/javascript&quot; src=&quot;my.js&quot;/&gt;
 * &lt;/code&gt;
 * </pre>
 * 
 * <p>
 * Method 2: Specify the JavaScript codes directly
 * 
 * <pre>
 * &lt;code&gt;&lt;script type=&quot;text/javascript&quot;&gt;
 * some_js_at_browser();
 * &lt;/script&gt;
 * &lt;/code&gt;
 * </pre>
 * 
 * <p>
 * Method 3: Specify the JavaScript codes by use of the content property (
 * {@link #setContent}).
 * 
 * <pre>
 * &lt;code&gt;&lt;script type=&quot;text/javascript&quot;&gt;
 * &lt;attribute name=&quot;content&quot;&gt;
 *  some_js_at_browser();
 * &lt;/attribute&gt;
 * &lt;/script&gt;
 * &lt;/code&gt;
 * </pre>
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Script {

	/** @deprecated As of release 5.0.0, it is meaningless since
	 * text/javascript is always assumed.
	 */
	public String getType();

	/** @deprecated As of release 5.0.0, it is meaningless since
	 * text/javascript is always assumed.
	 */
	public void setType(String type);

	/**
	 * Returns the character enconding of the source. It is used with
	 * {@link #getSrc}.
	 * 
	 * <p>
	 * Default: null.
	 */
	public String getCharset();

	/**
	 * Sets the character encoding of the source. It is used with
	 * {@link #setSrc}.
	 */
	public void setCharset(String charset);

	/**
	 * Returns the URI of the source that contains the script codes.
	 * <p>
	 * Default: null.
	 */
	public String getSrc();

	/**
	 * Sets the URI of the source that contains the script codes.
	 * 
	 * <p>
	 * You either add the script codes directly with the {@link Label} children,
	 * or set the URI to load the script codes with {@link #setSrc}. But, not
	 * both.
	 * 
	 * @param src
	 *            the URI of the source that contains the script codes
	 */
	public void setSrc(String src);

	/** @deprecated As of release 5.0.0, it is meaningless since it is always
	 * deferred
	 */
	public boolean isDefer();

	/** @deprecated As of release 5.0.0, it is meaningless since it is always
	 * deferred
	 */
	public void setDefer(boolean defer);

	/**
	 * Returns the content of the script element. By content we mean the
	 * JavaScript codes that will be enclosed by the HTML SCRIPT element.
	 * 
	 * <p>
	 * Default: null.
	 * 
	 */
	public String getContent();

	/**
	 * Sets the content of the script element. By content we mean the JavaScript
	 * codes that will be enclosed by the HTML SCRIPT element.
	 * 
	 */
	public void setContent(String content);

}
