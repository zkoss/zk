/* WireComponentsVM.java

		Purpose:
		
		Description:
		
		History:
				Thu May 06 14:52:28 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;

public class WireComponentsVM {
	//UI component
	@Wire("#msgPopup")
	Popup popup;
	@Wire("#msg")
	Label msg;

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void log(@BindingParam("btn") Button btn) {
		Clients.log(popup.getWidgetClass());
		Clients.log(msg.getWidgetClass());
		Clients.log(btn.getWidgetClass());
	}
}
