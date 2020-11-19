package org.zkoss.zktest.test2;

import java.util.Arrays;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;

public class B95_ZK_4726VM {
	private ListModelList items = new ListModelList();
	private String message;

	public B95_ZK_4726VM() {
		String[] names = {"A", "B", "C", "D"};
		items.addAll(Arrays.asList(names));
	}

	@NotifyChange("message")
	public void delete(@BindingParam("item") String item){
		items.remove(item);
		message = "remove item: " + item;
	}

	@NotifyChange("message")
	public void showIndex(int index){
		message = "item index: " + index;
	}

	public ListModelList getItems() {
		return items;
	}

	public String getMessage() {
		return message;
	}
}