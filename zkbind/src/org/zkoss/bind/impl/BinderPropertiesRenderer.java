/** BinderPropertiesRenderer.java.

	Purpose:
		
	Description:
		
	History:
		11:27:39 AM Jan 7, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import org.zkoss.bind.Binder;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zk.ui.sys.PropertiesRenderer;

/**
 * A BinderPropertiesRenderer is used to add extra properties for client widget.
 * @author jumperchen
 * @since 8.0.0
 */
public class BinderPropertiesRenderer implements PropertiesRenderer {

	/* (non-Javadoc)
	 * @see org.zkoss.zk.ui.sys.PropertiesRenderer#renderProperties(org.zkoss.zk.ui.Component, org.zkoss.zk.ui.sys.ContentRenderer)
	 */
	public void renderProperties(Component comp, ContentRenderer render) throws IOException {
		Binder binder = BinderUtil.getBinder(comp);
		if (binder != null && binder.getView() == comp) {
			render.render("$ZKBINDER$", true);
			//ZK-3133
			Map<String, Method> mmv = binder.getMatchMediaValue();
			if (!mmv.isEmpty()) {
				render.render("$ZKMATCHMEDIA$", mmv.keySet().toArray());
			}
		}
	}

}
