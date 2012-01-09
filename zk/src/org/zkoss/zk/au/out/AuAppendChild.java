/* AuAppendChild.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:33:16     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import org.zkoss.json.JavaScriptValue;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to insert an unparsed HTML as the last child of
 * the specified component at the client.
 *
 * <p>data[0]: the uuid of the component/page as the parent<br>
 * data[1]: the unparsed HTML (aka., content)
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuAppendChild extends AuResponse {
	/**
	 * @param contents a collection of contents (in String objects).
	 * Each content is the output of a component.
	 * @since 5.0.7
	 */
	public AuAppendChild(Component comp, Collection<String> contents) {
		super("addChd", comp, toArray(comp.getUuid(), contents));
	}
	/**
	 * @param contents a collection of contents (in String objects).
	 * Each content is the output of a component or a page.
	 * @since 5.0.7
	 */
	public AuAppendChild(Page page, Collection<String> contents) {
		super("addChd", page, toArray(page.getUuid(), contents));
	}
	/** Converts the contents (a collection of strings) to an arry of JavaScriptValue. */
	/*package*/ static Object[] toArray(String uuid, Collection<String> contents) {
		final List<Object> list = new LinkedList<Object>();
		list.add(uuid);
		stringToJS(contents, list);
		return list.toArray(new Object[list.size()]);
	}
	/** Converts the contents (a collection of strings) to an arry of JavaScriptValue. */
	private static void stringToJS(Collection<String> contents, Collection<Object> result) {
		for (String content: contents)
			result.add(new JavaScriptValue(content));
	}
}
