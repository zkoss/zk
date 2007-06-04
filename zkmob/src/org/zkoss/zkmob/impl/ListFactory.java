/* ListFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 15, 2007 3:12:00 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.impl;

import org.xml.sax.Attributes;
import org.zkoss.zkmob.AbstractUiFactory;
import org.zkoss.zkmob.Context;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.ZkComponents;

/**
 * An UiFactory used to create a List Ui component.
 * 
 * @author henrichen
 *
 */
public class ListFactory extends AbstractUiFactory {
	public ListFactory(String name) {
		super(name);
	}

	public Object create(Object parent, String tag, Attributes attrs, Context ctx) {
		final String id = attrs.getValue("id"); //id
		final String title = attrs.getValue("tt"); //title
		final String listTypeStr = attrs.getValue("tp"); //listType
		final int listType = Integer.parseInt(listTypeStr);
		
		ZkList list = new ZkList(((ZkComponent)parent).getZk(), id, title, listType);
		return list;
	}

}
