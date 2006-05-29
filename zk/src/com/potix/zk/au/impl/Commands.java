/* Commands.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Oct  2 11:48:07     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.au.impl;

import java.util.Set;
import java.util.LinkedHashSet;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.UiException;
import com.potix.zk.au.AuRequest;

/**
 * Utilities to implement {@link com.potix.zk.au.AuRequest.Command}.
 * Used only internally.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
class Commands {
	/** Converts the data of the specified request to a set of Component.
	 * The data is assumed to contain a list of item ID in the
	 * comman-separated format
	 */
	static Set convertToItems(AuRequest request) {
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
}
