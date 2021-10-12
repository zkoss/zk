package org.zkoss.zktest.test2;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

public class F70_ZK_1729 extends SelectorComposer<Component> {
	
	@Wire
	Listbox box;
	
	@Wire
	Listhead head;
	
	@Wire
	Label lbl;
	
	@Listen("onClick=#btn")
	public void onClick() {
		List<Listheader> headers = head.getChildren();
		
		StringBuffer headerinfo = new StringBuffer("");
		for(Listheader header: headers) {
			headerinfo.append(header.getLabel() + " ");
		}
		
		lbl.setValue(headerinfo.toString());
		
		box.getChildren().add(new Listitem("new item"));
	}
}