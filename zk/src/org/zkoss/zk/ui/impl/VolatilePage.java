/* VolatilePage.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct  4 09:16:40 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.impl;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.PageDefinition;

/**
 * A page that does not really exist at the client.
 * Its existence is temporary. It is usually used by UiEngine to create
 * components from a ZUML page that eventually shall not be attached to the desktop.
 *
 * <p>Notice that all components attached to this page won't be output to the client.
 * @author tomyeh
 * @since 6.0.0
 */
public class VolatilePage extends PageImpl {
	/** Constructs a page by giving the page definition.
	 * @param pgdef the page definition (never null).
	 */
	public VolatilePage(PageDefinition pgdef) {
		super(pgdef);
	}
	public VolatilePage(Page ref) {
		super(ref);
	}
	public String toString() {
		return "vlatile" + super.toString();
	}
}
