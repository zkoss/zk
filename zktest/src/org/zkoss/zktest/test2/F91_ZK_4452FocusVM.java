/* F91_ZK_4452FocusVM.java

	Purpose:
		
	Description:
		
	History:
		Mon Feb 24 14:26:36 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.UIManager;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class F91_ZK_4452FocusVM {
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
		manager = BindUtils.getUIManager(this);
		manager.focus(text);
	}

	private UIManager manager;

	@Command
	public void doFocus() {
		Clients.log("doFocus!");
		manager.focus(text);
	}

	@Command
	public void doFocusInList() {
		Clients.log("doFocusInList!");
		manager.focus(list.get(list.size() - 1));
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
