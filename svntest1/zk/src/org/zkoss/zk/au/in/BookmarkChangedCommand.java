/* BookmarkChangedCommand.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 29 19:34:36     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.au.in;

import org.zkoss.lang.Objects;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.BookmarkEvent;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.Command;

/**
 * Used by {@link AuRequest} to implement a command to broadcast
 * an {@link BookmarkEvent} event to all root components.
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class BookmarkChangedCommand extends Command {
	/** Contruct an event to denote that the bookmark is changed.
	 *
	 * <p>Note: {@link org.zkoss.zk.ui.event.Event#getTarget} will return null. It means it is a broadcast
	 * event.
	 */
	public BookmarkChangedCommand(String evtnm, int flags) {
		super(evtnm, flags);
	}

	//-- super --//
	protected void process(AuRequest request) {
		final String[] data = request.getData();
		if (data == null || data.length != 1)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {Objects.toString(data), this});
		final String nm = data[0];
		((DesktopCtrl)request.getDesktop()).setBookmarkByClient(nm);
		Events.postEvent(new BookmarkEvent(getId(), nm));
		Events.postEvent(new BookmarkEvent("onBookmarkChanged", nm)); //backward compatibility
	}
}
