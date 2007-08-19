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
package org.zkoss.zkmob.factory;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;

import org.xml.sax.Attributes;
import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.ui.ZkDesktop;
import org.zkoss.zkmob.ui.ZkImageItem;

/**
 * An UiFactory that create a ImageItem Ui component.
 * @author henrichen
 *
 */
public class ImageItemFactory extends AbstractUiFactory {

	public ImageItemFactory(String name) {
		super(name);
	}
	
	public ZkComponent create(ZkComponent parent, String tag, Attributes attrs, String hostURL, String pathURL) {
		final String id = attrs.getValue("id"); //id
		final String label = attrs.getValue("lb"); //label
		final String altText = attrs.getValue("tx"); //altText
		final String apperenceModeStr = attrs.getValue("md"); //apperenceMode
		final String src = attrs.getValue("im");
		final int apperenceMode = apperenceModeStr != null ?
				Integer.parseInt(apperenceModeStr) : Item.PLAIN;
		final ZkDesktop zk = ((ZkComponent)parent).getZkDesktop();
		
		final ZkImageItem component = 
			new ZkImageItem(zk, id, label, src, ZkImageItem.LAYOUT_DEFAULT, altText, apperenceMode);
		UiManager.applyItemProperties(parent, component, attrs);

		return component;
	}
}
