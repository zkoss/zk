/** TabboxEngine.java.

	Purpose:
		
	Description:
		
	History:
		10:23:44 AM Nov 14, 2013, Created by jumperchen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zul.impl;

import org.zkoss.zul.Tabbox;
import org.zkoss.zul.event.ListDataEvent;

/**
 * Tabbox engine is an engine that do the model rendering for {@link ListModel}
 * @author jumperchen
 * @since 7.0.0
 */
public interface TabboxEngine {
	public static final String ATTR_ON_INIT_RENDER_POSTED = "org.zkoss.zul.onInitLaterPosted";
	public static final String ATTR_CHANGING_SELECTION = "org.zkoss.zul.tabbox.changingSelection";
	public void doInitRenderer(Tabbox tabbox);
	public void doDataChange(Tabbox tabbox, ListDataEvent event);
}
