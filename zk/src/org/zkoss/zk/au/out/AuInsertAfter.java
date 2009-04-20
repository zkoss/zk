/* AuInsertAfter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:32:06     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to insert an unparsed HTML after the specified component
 * at the client.
 * <p>data[0]: the uuid of the component after which the HTML will insert<br>
 * data[1]: the unparsed HTML (aka., content)
 * data[2]: the page UUID
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuInsertAfter extends AuResponse {
	/**
	 * @param anchor the reference where the component will be added after.
	 */
	public AuInsertAfter(Component anchor, String content) {
		super("addAft", anchor,
			new String[] {anchor.getUuid(), content, getRefId(anchor)});
	}
	private static String getRefId(Component anchor) {
		//Bug 1939059: This is a dirty fix. We only handle roots
		//and assume it must be the last one
		if (anchor.getParent() != null) {
			if (anchor instanceof Native)
				throw new UiException("Adding a component after a native one not allowed: "+anchor);

			//Bug 2686585: if ZK JSP used, we have no info so no exception
			return "";
		}

		final Page page = anchor.getPage();
		return page != null ? /*just in case*/page.getUuid(): "";
	}
}
