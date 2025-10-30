/* B86_ZK_4323VM.java

		Purpose:
		
		Description:
		
		History:
				Tue Aug 27 16:55:56 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class B86_ZK_4323VM {
	private ListModelList model;
	private List<String> selectedItems = new LinkedList<>();
	
	@Init
	public void init() {
		List Items = new ArrayList();
		for (int i = 0; i < 20; i++) {
			Items.add("data " + i);
		}
		model = new ListModelList(Items);
		model.setMultiple(true);
		
		selectedItems.add(model.get(model.size()-1).toString());
		selectedItems.add(model.get(model.size()-10).toString());
	}
	
	public ListModelList getModel() {
		return model;
	}
	
	public List<String> getSelectedItems() {
		return selectedItems;
	}
	
	public void setSelectedItems(List<String> selectedItems) {
		this.selectedItems = selectedItems;
	}
}
