/* AuInsertAfter.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:32:06     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.StubComponent;
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
	 * @param contents a collection of contents (in String objects).
	 * Each content is the output of a component.
	 * @since 5.0.7
	 */
	public AuInsertAfter(Component anchor, Collection contents) {
		super("addAft", anchor, toArray(anchor, contents));
	}
	private static Object[] toArray(Component anchor, Collection contents) {
		if (anchor instanceof Native || anchor instanceof StubComponent)
			throw new UiException("Adding a component before a native one not allowed: "+anchor);

		final List list = new LinkedList();
		list.add(anchor.getUuid());
		list.add(getRefId(anchor));
		AuAppendChild.stringToJS(contents, list);
		return list.toArray(new Object[list.size()]);
	}
	private static String getRefId(Component anchor) {
		//Bug 1939059: This is a dirty fix. We only handle roots
		//and assume it must be the last one
		if (anchor.getParent() != null) {
			if (anchor instanceof Native || anchor instanceof StubComponent)
				throw new UiException("Adding a component after a native one not allowed: "+anchor);

			//Bug 2686585: if ZK JSP used, we have no info so no exception
			return "";
		}

		final Page page = anchor.getPage();
		return page != null ? /*just in case*/page.getUuid(): "";
	}
}
