/* BookmarkEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 29 22:44:05     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.event;

/** The onBookmarkChanged event used
 * to notify that user pressed BACK, FORWARD or specified URL directly
 * that causes the bookmark is changed (but still in the same desktop).
 *
 * <p>All root components of all pages of the desktop will
 * recieves this event.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class BookmarkEvent extends Event {
	/** The bookmark name. */
	private final String _bookmark;

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
