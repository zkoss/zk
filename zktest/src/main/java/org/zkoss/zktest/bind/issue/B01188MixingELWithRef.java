package org.zkoss.zktest.bind.issue;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

public class B01188MixingELWithRef {

	ListModelList lm0 = new ListModelList();

	public class Item {
		String s_;
		public Item(String s) {
			s_ = s;
		}

		public String toString(){
			return s_;
		}
	}

	public B01188MixingELWithRef() {
		ListModelList list = new ListModelList();
		list.add(new Item("Item 1"));
		list.add(new Item("Item 2"));

		lm0.add(new Object[] { "Today", list });
		
		list = new ListModelList();
		list.add(new Item("Item 3"));
		list.add(new Item("Item 4"));
		
		lm0.add(new Object[] { "Tomorrow", list });
	}

	public ListModel getLm0() {
		return lm0;
	}

}
