package org.zkoss.zephyr.test.mvvm.book.comp;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Listbox {
	private List<String> _itemList = new ArrayList<>();
	public Listbox() {
		String[] items = {"item01", "item02", "item03", "item04"};
		_itemList.addAll(Arrays.asList(items));
	}

	private String selected;
	private Integer index;
	private boolean open = false;

	public List<String> getItemList() {
		return _itemList;
	}

	public String getSelected() {
		return selected;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
}
