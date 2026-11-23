/* F90_ZK_4377_3VM.java

		Purpose:
		
		Description:
		
		History:
				Thu Oct 31 19:05:31 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

public class F90_ZK_4377_3VM {
	private ListModelList<String> data;
	
	@Init
	public void init() {
		data = new ListModelList<>(new String[] {
				"2019-Q1",
				"2019-Q2",
				"2019-Q3",
				"2019-Q4"
		});
	}
	
	public ListModel<String> getData() {
		return data;
	}
	
	public void setData(ListModelList<String> dataList) {
		this.data = dataList;
	}
	
	@Command("add")
	public void addItem() {
		data.add("add new");
	}
	
	@Command("remove")
	public void removeItem() {
		data.remove(data.size() - 1);
	}
	
	@Command("change")
	public void changeItem() {
		data.set(0, "data changed!");
	}
	
	@Command("clear")
	public void clearItems() {
		data.clear();
	}
}
