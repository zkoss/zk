/* ListItemFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 22, 2007 9:11:34 AM, Created by henrichen
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
import org.zkoss.zkmob.Listable;
import org.zkoss.zkmob.UiManager;

/**
 * An UiFactory that create a ListItem Ui component.
 * @author henrichen
 *
 */
public class ListItemFactory extends AbstractUiFactory {

	public ListItemFactory(String name) {
		super(name);
	}
	
	public Object create(Object parent, String tag, Attributes attrs, Context ctx) {
		final String id = attrs.getValue("id"); //label
		final String label = attrs.getValue("lb"); //label
		//TODO: font not supported yet
		final Listable owner = (Listable) parent;
		owner.append(id, label, null);
		final ZkListItem component = new ZkListItem(id, owner);

		//load the image and setup on a seperate thread
		String imagesrc = ctx.prefixURL(attrs.getValue("im")); //image
		UiManager.loadImageOnThread(component, imagesrc);
		
		return component;
	}
}
