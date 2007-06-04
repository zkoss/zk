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
package org.zkoss.zkmob.impl;

import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Form;
import org.xml.sax.Attributes;
import org.zkoss.zkmob.AbstractUiFactory;
import org.zkoss.zkmob.Context;
import org.zkoss.zkmob.ZkComponent;
import org.zkoss.zkmob.ZkComponents;

/**
 * An UiFactory that create a DateField Ui component.
 * @author henrichen
 *
 */
public class DateFieldFactory extends AbstractUiFactory {
	public DateFieldFactory(String name) {
		super(name);
	}

	public Object create(Object parent, String tag, Attributes attrs, Context ctx) {
		final String id = attrs.getValue("id"); //id
		final String label = attrs.getValue("lb"); //label
		final String modeStr = attrs.getValue("md"); //mode
		final int mode = Integer.parseInt(modeStr);
		final Zk zk = ((ZkComponent)parent).getZk();

		final DateField component = new ZkDateField(zk, id, label, mode);

		ZkComponents.applyItemProperties((Form) parent, component, attrs);
		
		return component;
	}
}
