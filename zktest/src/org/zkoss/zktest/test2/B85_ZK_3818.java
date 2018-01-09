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
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
/**
 * @author bobpeng
 */
public class B85_ZK_3818 extends SelectorComposer {

	@Wire
	Radiogroup rg;
	@Wire
	Window win;

	@Listen("onClick = #btnAdd")
	public void onAdd() {
		Radio radio = new Radio();
		radio.addEventListener("onCheck", new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				Clients.log("selected index: " + rg.getSelectedIndex());
			}
		});
		List<Component> list = rg.getChildren();
		if (list.isEmpty()) {
			Div div = new Div();
			div.appendChild(radio);
			rg.appendChild(div);
		} else {
			Div div = new Div();
			div.appendChild(radio);
			rg.insertBefore(div, list.get(0));
		}
		Clients.log("selected index: " + rg.getSelectedIndex());
	}

	@Listen("onClick = #btnDelete")
	public void onDelete() {
		rg.removeChild(rg.getChildren().get(0));
		Clients.log("selected index: " + rg.getSelectedIndex());
	}
	@Listen("onClick = #serial")
	public void serial() {
		doSerialize(win);
	}

	byte[] _bytes;

	public void doSerialize(Window win) {
		try {
			doSerialize0(win);
			doDeserialize0(win);
		} catch (Exception x) {
			x.printStackTrace();
			Clients.log("error :" + x.getClass() + "," + x.getMessage());
		}
	}

	public void doSerialize0(Window win) throws Exception {
		Page pg = win.getPage();
		((ComponentCtrl) win).sessionWillPassivate(pg);//simulate
		ByteArrayOutputStream oaos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(oaos);
		oos.writeObject(win);
		oos.close();
		oaos.close();
		_bytes = oaos.toByteArray();
	}

	public void doDeserialize0(Window win) throws Exception {
		ByteArrayInputStream oaos = new ByteArrayInputStream(_bytes);
		ObjectInputStream oos = new ObjectInputStream(oaos);

		Window newwin = (Window) oos.readObject();
		Page pg = win.getPage();
		Component parent = win.getParent();
		Component ref = win.getNextSibling();
		win.detach();
		oos.close();
		oaos.close();
		parent.insertBefore(newwin, ref);
		//for load component back.
		((ComponentCtrl) newwin).sessionDidActivate(newwin.getPage());//simulate
		Clients.log("done deserialize: " + _bytes.length);
	}
}
