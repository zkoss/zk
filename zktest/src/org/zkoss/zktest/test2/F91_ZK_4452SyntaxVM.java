/* F91_ZK_4452SyntaxVM.java

	Purpose:
		
	Description:
		
	History:
		Mon Feb 24 14:26:36 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class F91_ZK_4452SyntaxVM {
	private List<String> data = new ArrayList<>();
	private Object label = "a";
	private String text = "bbb";
	private List<String> list;

	@Init
	public void init() {
		list = new ArrayList<>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");
		list.add("e");
		list.add("f");
	}

	public void doClick(int s, @BindingParam("bb") String bb, @BindingParam("ss") long ss) {
		Clients.log("do click!!" + s + "," + bb + "," + ss);
	}

	@NotifyChange("selItem")
	public void doTest(String bb, int sa, long tt) {
		label += "1";
		Clients.log("do test!!" + bb + "," + sa + "," + tt);
	}

	public Object getLabel() {
		return label;
	}

	public void setLabel(Object label) {
		this.label = label;
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
