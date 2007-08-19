/* SpacerFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 15, 2007 3:31:58 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.factory;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Spacer;

import org.xml.sax.Attributes;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.ui.ZkDesktop;
import org.zkoss.zkmob.ui.ZkSpacer;

/**
 * @author henrichen
 *
 */
public class SpacerFactory extends AbstractUiFactory {

	public SpacerFactory(String name) {
		super(name);
	}
	
	public ZkComponent create(ZkComponent parent, String tag, Attributes attrs, String hostURL, String pathURL) {
		final String id = attrs.getValue("id"); //id
		final String minWidthStr = attrs.getValue("mw"); //minWidth
		final String minHeightStr = attrs.getValue("mh"); //minHeight
		final int minWidth = Integer.parseInt(minWidthStr);
		final int minHeight = Integer.parseInt(minHeightStr);
		final ZkDesktop zk = ((ZkComponent)parent).getZkDesktop();
		
		final ZkSpacer component = new ZkSpacer(zk, id, minWidth, minHeight);

		UiManager.applyItemProperties(parent, component, attrs);
		
		return component;
	}

}
