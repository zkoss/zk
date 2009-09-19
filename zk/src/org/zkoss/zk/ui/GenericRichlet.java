/* GenericRichlet.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct  6 09:56:39     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import org.zkoss.zk.ui.metainfo.LanguageDefinition;

/**
 * Defines a genric richlet. Developers can use it as a skeleton to implement
 * an application-specific richlet.
 *
 * @author tomyeh
 */
abstract public class GenericRichlet implements Richlet {
	/** Called by the richlet container to indicate to a richlet that
	 * the richlet is being placed into service.
	 *
	 * <p>Default: does nothing.
	 */
	public void init(RichletConfig config) {
	}
	/** Called by the richlet container to indicate to a richlet that
	 * the richlet is being taken out of service.
	 *
	 * <p>Default: does nothing.
	 */
	public void destroy() {
	}
	/** Returns the language defintion that this richlet belongs to.
	 * Don't return null.
	 *
	 * <p> It is called when creating a new page for this richlet to serve.
	 *
	 * <p>Default: return the language definition called "xul/html".
	 */
	public LanguageDefinition getLanguageDefinition() {
		return LanguageDefinition.lookup("xul/html");
	}
}
