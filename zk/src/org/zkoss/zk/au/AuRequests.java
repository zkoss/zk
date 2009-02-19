/* AuRequests.java

	Purpose:
		
	Description:
		
	History:
		Sat Nov 29 21:34:32     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.au;

import java.util.Set;
import java.util.LinkedHashSet;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.MouseEvent;

/**
 * Utilities to convert {@link AuRequest} to {@link org.zkoss.zk.ui.event.Event}
 * or its deriving classes.
 * 
 * @author tomyeh
 * @since 5.0.0
 */
public class AuRequests {
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

	/** Returns the inner width of an AU request representing the update
	 * of inner width.
	 */
	public static String getInnerWidth(AuRequest request)
	throws UiException {
		final String[] data = request.getData();
		if (data == null || data.length != 1)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), request});
		return data[0];
	}

	/** Returns the result of an AU request representing the update result.
	 */
	public static Object getUpdateResult(AuRequest request)
	throws UiException {
		final String[] data = request.getData();
		if (data == null || data.length != 1)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), request});

		final Object result = request.getDesktop().removeAttribute(data[0]);
		if (result == null)
			throw new UiException("Content not found: "+data[0]);
		return result;
	}
}
