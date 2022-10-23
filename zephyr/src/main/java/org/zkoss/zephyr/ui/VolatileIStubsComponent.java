/* VolatileIStubsComponent.java

	Purpose:
		
	Description:
		
	History:
		2:46 PM 2022/8/17, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui;

import java.io.IOException;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.ContentRenderer;

/**
 * Internal use only.
 * @author jumperchen
 */
public class VolatileIStubsComponent extends IStubsComponent {
	private String _widgetClass;
	private IStubComponent _target;

	public void replace(Component comp, boolean bFellow, boolean bListener,
			boolean bChildren) {
		if(comp instanceof IStubComponent) {
			_target = (IStubComponent) comp;
			_widgetClass = _target.getWidgetClass();
		}
		super.replace(comp, bFellow, bListener, bChildren);
	}

	public String getWidgetClass() {
		return _widgetClass != null ? _widgetClass : super.getWidgetClass();
	}

	protected void renderProperties(ContentRenderer renderer)
			throws IOException {
		if (_target != null) {
			_target.renderProperties(renderer);
			_target = null; // free
		} else {
			super.renderProperties(renderer);
		}
	}
}
