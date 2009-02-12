/* PageSerializationListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  7 13:36:53     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Page;

/**
 * Used to notify an object stored in a page, when the page
 * is going to be serialized or has been deserialized.
 *
 * <p>When a page is going to be serialized, it checks every
 * attribute ({@link Page#setAttribute}),
 * listener ({@link Page#addEventListener}), and
 * variable resolver ({@link Page#addVariableResolver})
 * to see whether this interface is implemented.
 * If implemented, {@link #willSerialize} will be called.
 * Similarly, {@link #didDeserialize} is called if the page has
 * been deserialized.
 *
 * @author tomyeh
 * @since 2.4.0
 */
public interface PageSerializationListener {
	/** Called when a page is going to serialize this object.
	 */
	public void willSerialize(Page page);
	/** Called when a page has de-serialized this object back.
	 */
	public void didDeserialize(Page page);
}
