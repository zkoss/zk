/* Richlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct  5 11:56:22     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;

/**
 * Defines methods that all richlets must implement.
 * A richlet is a small Java program that
 * creates all necessary components for a given page in response to user's request.
 *
 * <p>To activate it, it must be added to {@link org.zkoss.zk.ui.util.Configuration}
 * by use of {@link org.zkoss.zk.ui.util.Configuration#addRichlet}, or specify
 * &lt;richlet&gt; in zk.xml.
 *
 * @author tomyeh
 */
public interface Richlet {
	/** Called by the richlet container to indicate to a richlet that
	 * the richlet is being placed into service.
	 */
	public void init(RichletConfig config);
	/** Called by the richlet container to indicate to a richlet that
	 * the richlet is being taken out of service.
	 */
	public void destroy();

	/** Called by the richlet container to create components when
	 * the specified page is visited and created.
	 */
	public void service(Page page);

	/** Returns the default language defintion that this richlet belongs to.
	 * Don't return null.
	 *
	 * <p>The default language definition is used to initialize
	 * a page when the page is created to be served by this richlet.
	 * Note: you can use components from different languages as long as
	 * they belong to the same device type ({@link LanguageDefinition#getDeviceType}.
	 *
	 * <p>The default language definition controls the URIs used to
	 * render a desktop and a page, and the initial zscript codes used
	 * to initialize the interpreter (of a page).
	 *
	 * <p>For example, you can return
	 * <pre><code>LanguageDefinition.lookup("xul/html");</code></pre>
	 */
	public LanguageDefinition getLanguageDefinition();
}
