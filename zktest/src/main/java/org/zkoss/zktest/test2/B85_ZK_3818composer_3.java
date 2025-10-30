/* B85_ZK_3818.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 19 11:52:15 CST 2017, Created by bobpeng

Copyright (C) 2017 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;

import java.util.List;

/**
 * @author bobpeng
 */
public class B85_ZK_3818composer_3 extends SelectorComposer {

	@Wire
	Radiogroup rg3;
	@Wire
	Div rgdiv3;

	@Listen("onClick = #btnAdd3")
	public void onAdd() {
		Radio radio = new Radio();
		radio.addEventListener("onCheck", new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				Clients.log("selected index: " + rg3.getSelectedIndex());
			}
		});
		List<Component> list = rgdiv3.getChildren();
		if (list.isEmpty()) {
			rgdiv3.appendChild(radio);
		} else {
			rgdiv3.insertBefore(radio, list.get(0));
		}
		Clients.log("selected index: " + rg3.getSelectedIndex());
	}

	@Listen("onClick = #btnDelete3")
	public void onDelete() {
		rgdiv3.removeChild(rgdiv3.getChildren().get(0));
		Clients.log("selected index: " + rg3.getSelectedIndex());
	}
}
