/* ImageItemFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 15, 2007 3:22:57 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.impl;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;

import org.xml.sax.Attributes;
import org.zkoss.zkmob.AbstractUiFactory;
import org.zkoss.zkmob.Context;
import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.ZkComponents;

/**
 * An UiFactory that create a ImageItem Ui component.
 * @author henrichen
 *
 */
public class ImageItemFactory extends AbstractUiFactory {

	public ImageItemFactory(String name) {
		super(name);
	}
	
	public Object create(Object parent, String tag, Attributes attrs, Context ctx) {
		final String id = attrs.getValue("id"); //id
		final String label = attrs.getValue("lb"); //label
		final String altText = attrs.getValue("tx"); //altText
		final String apperenceModeStr = attrs.getValue("md"); //apperenceMode
		final int apperenceMode = apperenceModeStr != null ?
				Integer.parseInt(apperenceModeStr) : Item.PLAIN;
		final Zk zk = ((ZkComponent)parent).getZk();
		
		final ZkImageItem component = new ZkImageItem(zk, id, label, ZkImageItem.LAYOUT_DEFAULT, altText, apperenceMode);

		ZkComponents.applyItemProperties((Form)parent, component, attrs);

		//load the image and setup on a seperate thread
		String imagesrc = ctx.prefixURL(attrs.getValue("im"));
		UiManager.loadImageOnThread(component, imagesrc);
		
		return component;
	}
}
