package org.zkoss.zephyr.test.mvvm.book.comp;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Selectbox {
	private List _itemList = new ArrayList();

	public Selectbox() {
		String[] items = {"item01", "item02", "item03", "item04"};
		_itemList.addAll(Arrays.asList(items));
	}

	private int selectedIndex;

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public List getItemList() {
		return _itemList;
	}
}
