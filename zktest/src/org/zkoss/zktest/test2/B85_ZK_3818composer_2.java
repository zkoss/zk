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
public class B85_ZK_3818composer_2 extends SelectorComposer {

	@Wire
	Radiogroup rg2;

	@Listen("onClick = #btnAdd2")
	public void onAdd() {
		Radio radio = new Radio();
		radio.addEventListener("onCheck", new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				Clients.log("selected index: " + rg2.getSelectedIndex());
			}
		});
		List<Component> list = rg2.getChildren();
		if (list.isEmpty()) {
			Div div = new Div();
			div.appendChild(radio);
			rg2.appendChild(div);
		} else {
			Div div = new Div();
			div.appendChild(radio);
			rg2.insertBefore(div, list.get(0));
		}
		Clients.log("selected index: " + rg2.getSelectedIndex());
	}

	@Listen("onClick = #btnDelete2")
	public void onDelete() {
		rg2.removeChild(rg2.getChildren().get(0));
		Clients.log("selected index: " + rg2.getSelectedIndex());
	}
}
