/* F90_ZK_MVVM_1.java

	Purpose:
		
	Description:
		
	History:
		Mon Feb 24 14:26:36 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class F90_ZK_MVVM_1 {

	private List<String> data = new ArrayList<>();

	private Object selItem = "a1";
	private String text = "bbb1";
	private List<String> list;

	@Init
	public void init() {
		list = new ArrayList<>();
		list.add("a1");
		list.add("b1");
		list.add("c1");
		list.add("d1");
		list.add("e1");
		list.add("f1");
	}

	@Command
	public void doClick() {
		Clients.log("nested clicked!");
	}

	public Object getSelItem() {
		return selItem;
	}

	public void setSelItem(Object selItem) {
		this.selItem = selItem;
	}

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getList() {
		return list;
	}
}
