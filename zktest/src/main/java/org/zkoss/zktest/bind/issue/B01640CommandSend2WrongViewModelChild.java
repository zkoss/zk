package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class B01640CommandSend2WrongViewModelChild {
	String value;

	List<String> items = new ArrayList<String>();

	public B01640CommandSend2WrongViewModelChild() {
		items.add("A");
	}

	@Init
	public void init() {
		value = "initialized";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<String> getItems() {
		return items;
	}

	@Command
	@NotifyChange("value")
	public void outerGridCommand() {
		value = "do outerGridCommand";
	}

	@Command
	@NotifyChange("value")
	public void innerGridCommand(@BindingParam("str") String str) {
		value = "do innerGridCommand "+str;
	}
}
