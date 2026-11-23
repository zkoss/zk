/* B96_ZK_4872VM.java

	Purpose:
		
	Description:
		
	History:
		Tue May 11 11:22:58 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

/**
 * @author rudyhuang
 */
public class B96_ZK_4872VM {
	private final ListModelList<String> model = new ListModelList<>();
	private String selectedItem;

	public B96_ZK_4872VM() {
		model.add("group 1");
		model.add("item 1-1");
		model.add("item 1-2");
		model.add("group 2");
		model.add("item 2-1");
		model.add("item 2-2");
		selectedItem = model.get(1);
	}

	@Command
	@NotifyChange("selectedItem")
	public void init(){
		System.out.println("select");
	}

	@Command
	@NotifyChange("model")
	public void refresh(){
		System.out.println("refresh");
	}

	@Command
	public void update(){
		model.add("group 3");
		model.add("item 3-1");
		model.add("item 3-2");
	}

	public boolean isGroup(String item){
		return item.startsWith("group");
	}

	public ListModel<?> getModel() {
		return model;
	}

	public String getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
	}
}
