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
import org.zkoss.zkmob.Listable;
import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.ZkComponent;

/**
 * An UiFactory that create a ListItem Ui component.
 * @author henrichen
 *
 */
public class ListItemFactory extends AbstractUiFactory {

	public ListItemFactory(String name) {
		super(name);
	}
	
	public ZkComponent create(ZkComponent parent, String tag, Attributes attrs, String hostURL) {
		final String id = attrs.getValue("id"); //id
		final String label = attrs.getValue("lb"); //label
		final String src = attrs.getValue("im");
		//TODO: font not supported yet
		final Listable owner = (Listable) parent;
		final ZkListItem component = new ZkListItem(owner, id, null, null);

		owner.append(component, label, null);

		//load the image and setup on a seperate thread
		final String imagesrc = UiManager.prefixURL(hostURL, src); //image
		UiManager.loadImageOnThread(component, imagesrc);
		
		return component;
	}
}
