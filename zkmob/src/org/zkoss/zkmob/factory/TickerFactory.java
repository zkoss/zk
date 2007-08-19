/* TickerFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 16, 2007 4:21:13 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.factory;

import javax.microedition.lcdui.Ticker;

import org.xml.sax.Attributes;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.ui.ZkTicker;

/**
 * An UiFactory to create a Ticker.
 * @author henrichen
 *
 */
public class TickerFactory extends AbstractUiFactory {
	public TickerFactory(String name) {
		super(name);
	}

	public ZkComponent create(ZkComponent parent, String tag, Attributes attrs, String hostURL, String pathURL) {
		final String id = attrs.getValue("id"); //id
		final String string = attrs.getValue("tx"); //String
		
		final ZkTicker component = new ZkTicker(((ZkComponent)parent).getZkDesktop(), id, string);
		
		return component;
	}		
}
