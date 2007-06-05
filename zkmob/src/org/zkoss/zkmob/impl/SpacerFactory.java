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
package org.zkoss.zkmob.impl;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Spacer;

import org.xml.sax.Attributes;
import org.zkoss.zkmob.AbstractUiFactory;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.ZkComponents;

/**
 * @author henrichen
 *
 */
public class SpacerFactory extends AbstractUiFactory {

	public SpacerFactory(String name) {
		super(name);
	}
	
	public ZkComponent create(ZkComponent parent, String tag, Attributes attrs, String hostURL) {
		final String id = attrs.getValue("id"); //id
		final String minWidthStr = attrs.getValue("mw"); //minWidth
		final String minHeightStr = attrs.getValue("mh"); //minHeight
		final int minWidth = Integer.parseInt(minWidthStr);
		final int minHeight = Integer.parseInt(minHeightStr);
		final Zk zk = ((ZkComponent)parent).getZk();
		
		final ZkSpacer component = new ZkSpacer(zk, id, minWidth, minHeight);

		ZkComponents.applyItemProperties(parent, component, attrs);
		
		return component;
	}

}
