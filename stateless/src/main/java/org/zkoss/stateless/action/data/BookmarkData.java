/* BookmarkData.java

	Purpose:
		
	Description:
		
	History:
		6:25 PM 2022/8/11, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

/**The bookmark update action used with <code>onBookmarkChange</code>
 * to notify that user pressed BACK, FORWARD or others
 * that causes the bookmark changed (but still in the same desktop).
 *
 * <p>All root components of all pages of the desktop will
 * receive this action.
 * @author jumperchen
 */
public class BookmarkData implements ActionData {
	private final String _bookmark;

	@JsonCreator
	protected BookmarkData(Map data) {
		_bookmark = (String) data.get("");
	}

	/** Returns the bookmark name (never null).
	 */
	public String getBookmark() {
		return _bookmark;
	}
}
