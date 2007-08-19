/* GaugeFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 15, 2007 3:21:03 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.factory;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import org.xml.sax.Attributes;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.ui.ZkDesktop;
import org.zkoss.zkmob.ui.ZkGauge;

/**
 * An UiFactory that create a Gauge Ui component.
 * @author henrichen
 *
 */
public class GaugeFactory extends AbstractUiFactory {
	public GaugeFactory(String name) {
		super(name);
	}
	
	public ZkComponent create(ZkComponent parent, String tag, Attributes attrs, String hostURL, String pathURL) {
		final String id = attrs.getValue("id"); //id
		final String label = attrs.getValue("lb"); //label
		final String maxValueStr = attrs.getValue("mv"); //maxValue
		final String initialValueStr = attrs.getValue("iv"); //initialValue
		final String interactiveStr = attrs.getValue("it"); //interactive
		final boolean interactive = "t".equalsIgnoreCase(interactiveStr);
		final int maxValue = Integer.parseInt(maxValueStr);
		final int initialValue = Integer.parseInt(initialValueStr);
		final ZkDesktop zk = ((ZkComponent)parent).getZkDesktop();
		
		final ZkGauge component = new ZkGauge(zk, id, label, interactive, maxValue, initialValue);

		UiManager.applyItemProperties(parent, component, attrs);
		
		return component;
	}

}
