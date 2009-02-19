/* BookmarkEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 29 22:44:05     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;

/** The bookmark udpate event used with <code>onBookmarkChange</code>
 * to notify that user pressed BACK, FORWARD or others
 * that causes the bookmark changed (but still in the same desktop).
 *
 * <p>All root components of all pages of the desktop will
 * recieves this event.
 * 
 * @author tomyeh
 * @see URIEvent
 */
public class BookmarkEvent extends Event {
	/** The bookmark name. */
	private final String _bookmark;

	/** Converts an AU request to a bookmark event.
	 * @since 5.0.0
	 */
	public static final BookmarkEvent getBookmarkEvent(AuRequest request) {
		final String[] data = request.getData();
		if (data == null || data.length != 1)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), request});
		final String nm = data[0];
		((DesktopCtrl)request.getDesktop()).setBookmarkByClient(nm);
		return new BookmarkEvent(request.getName(), nm);
	}

	public BookmarkEvent(String name, String bookmark) {
		super(name, null);
		_bookmark = bookmark != null ? bookmark: "";
	}

	/** Returns the bookmark name (never null).
	 */
	public String getBookmark() {
		return _bookmark;
	}
}
