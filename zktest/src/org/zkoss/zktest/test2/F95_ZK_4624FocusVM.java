/* F91_ZK_4452FocusVM.java

	Purpose:
		
	Description:
		
	History:
		Mon Feb 24 14:26:36 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jameschu
 */
public class F95_ZK_4624FocusVM {
	private List<String> data = new ArrayList<>();
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

	@AfterCompose
	public void afterCompose() {
		Clients.focus("textbox");
	}

	@Command
	public void doFocus() {
		Clients.log("doFocus!");
		Clients.focus("textbox");
	}

	@Command
	public void doFocusInList() {
		Clients.log("doFocusInList!");
		Clients.focus("#list textbox:last-child");
	}

	@Command
	public void doFocusInner() {
		Clients.log("doFocusInner");
		Clients.focus("#win2 #tb_inner");
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
