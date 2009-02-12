/* Commands.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 11:48:07     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.in;

import java.util.Set;
import java.util.LinkedHashSet;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.au.AuRequest;

/**
 * Utilities to implement {@link org.zkoss.zk.au.Command}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class Commands {
	/** Converts the data of the specified request to a set of Component.
	 * The data is assumed to contain a list of item ID in the
	 * comman-separated format
	 *
	 * @return a set of components.
	 */
	public static Set convertToItems(AuRequest request) {
		final Set items = new LinkedHashSet();
		final String[] data = request.getData();
		String s = data != null && data.length > 0 ? data[0]: null;
		if (s != null) {
			s = s.trim();
			if (s.length() > 0) {
				final Desktop desktop = request.getDesktop();
				for (int j = 0, k = 0; k >=0; j = k + 1) {
					k = s.indexOf(',', j);
					final String uuid =
						k >= 0 ? s.substring(j, k): s.substring(j);
					final Component item =
						desktop.getComponentByUuid(uuid.trim());
					items.add(item);
				}
			}
		}
		return items;
	}

	/** Parses the key flags of a mouse event.
	 * @return a combination of {@link MouseEvent#ALT_KEY},
	 * {@link MouseEvent#SHIFT_KEY} and {@link MouseEvent#CTRL_KEY},
	 */
	public static int parseKeys(String flags) {
		int keys = 0;
		if (flags != null) {
			if (flags.indexOf("a") >= 0) keys |= MouseEvent.ALT_KEY;
			if (flags.indexOf("c") >= 0) keys |= MouseEvent.CTRL_KEY;
			if (flags.indexOf("s") >= 0) keys |= MouseEvent.SHIFT_KEY;
		}
		return keys;
	}
}
