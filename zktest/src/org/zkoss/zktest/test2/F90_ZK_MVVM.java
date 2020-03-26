/* F90_ZK_MVVM.java

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
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class F90_ZK_MVVM {

	public F90_ZK_MVVM() {
		System.out.println();
	}

	private List<String> data = new ArrayList<>();

	private Object selItem = "a";
	private String text = "bbb";
	private List<String> list;

	private boolean scrollingAfterCompose = false;
	private boolean focusAfterCompose = false;

	@Init
	public void init(@BindingParam("type") String type) {
		list = new ArrayList<>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");
		list.add("e");
		list.add("f");
		manager = BindUtils.getUIManager(this);
		if ("scroll".equals(type))
			manager.scrollIntoView(text);
		else if ("focus".equals(type))
			manager.focus(text);
	}

	private UIManager manager;

	@Command
	public void doClick(int s, @BindingParam("bb") String bb, @BindingParam("ss") long ss) {
		Clients.log("clicked!");
	}

	@NotifyChange("selItem")
	@Command
	public void doTest_Origin(@BindingParam("aa") String aa, @BindingParam("bb") int bb, @BindingParam("cc") long cc) {
		selItem += "1";
		Clients.log("do test!!!" + aa + "," + bb + "," + cc);
	}

	@NotifyChange("selItem")
	public void doTest(String bb, int sa, long tt) {
		selItem += "1";
		Clients.log("do test!!!" + bb + "," + sa + "," + tt);
	}

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

	@Command
	public void doScrollToTop() {
		Clients.log("doScrollToTop!");
		manager.scrollIntoView(selItem);
	}

	@Command
	public void doScroll() {
		Clients.log("doScroll!");
		manager.scrollIntoView(text);
	}

	@Command
	public void doScrollInList() {
		Clients.log("doScrollInList!");
		manager.scrollIntoView(list.get(list.size() - 1));
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
