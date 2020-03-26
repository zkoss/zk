/* F91_ZK_4452ScrollVM.java

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
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class F91_ZK_4452ScrollVM {
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
		manager = BindUtils.getUIManager(this);
		manager.scrollIntoView(text);
	}

	private UIManager manager;

	public void doScrollToTop() {
		Clients.log("doScrollToTop!");
		manager.scrollIntoView(label);
	}

	public void doScroll() {
		Clients.log("doScroll!");
		manager.scrollIntoView(text);
	}

	public void doScrollInList() {
		Clients.log("doScrollInList!");
		manager.scrollIntoView(list.get(list.size() - 1));
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
