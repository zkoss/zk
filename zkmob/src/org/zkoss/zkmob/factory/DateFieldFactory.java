/* DateFieldFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 15, 2007 3:19:17 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob.factory;

import java.util.Date;
import java.util.TimeZone;

import org.xml.sax.Attributes;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.UiManager;
import org.zkoss.zkmob.ui.ZkDesktop;
import org.zkoss.zkmob.ui.ZkDateField;

/**
 * An UiFactory that create a DateField Ui component.
 * @author henrichen
 *
 */
public class DateFieldFactory extends AbstractUiFactory {
	public DateFieldFactory(String name) {
		super(name);
	}

	public ZkComponent create(ZkComponent parent, String tag, Attributes attrs, String hostURL) {
		final String id = attrs.getValue("id"); //id
		final String label = attrs.getValue("lb"); //label
		final String modeStr = attrs.getValue("md"); //mode
		final String dateStr = attrs.getValue("dt"); //date
//		final String tzid = attrs.getValue("tz"); //timezone
		//The JavaME TimeZone support is poor. 
		//Always use GMT timezone and calculate the correct date in ZK server
		final TimeZone tz = TimeZone.getTimeZone("GMT"); 
		final int mode = Integer.parseInt(modeStr);
		final String onChange = attrs.getValue("on");
		final ZkDesktop zk = ((ZkComponent)parent).getZkDesktop();

		final ZkDateField component = new ZkDateField(zk, id, label, mode, tz
				, onChange == null ? null : new Boolean("t".equals(onChange)));
		if (dateStr != null) {
			final long time = Long.parseLong(dateStr);
			component.setDate(new Date(time));
		}

		UiManager.applyItemProperties(parent, component, attrs);
		
		return component;
	}
}
