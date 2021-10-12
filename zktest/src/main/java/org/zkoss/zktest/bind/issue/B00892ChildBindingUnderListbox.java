/* CollectionInCollection.java

	Purpose:
		
	Description:
		
	History:
		Feb 24, 2012 5:55:02 PM, Created by henrichen

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.zktest.bind.issue.B00762Listbox1.Item;

/**
 * @author henrichen
 *
 */
public class B00892ChildBindingUnderListbox {
	private List<Item> items;

	public B00892ChildBindingUnderListbox() {
		items = new ArrayList<Item>();
		items.add(new Item("A"));
		items.add(new Item("B"));
		items.add(new Item("C"));
		items.add(new Item("D"));
	}
	
	public List<?> dummyList(int size) {
		return Arrays.asList(new Object[size]);
	}
	
	public List<Item> getItems() {
		return items;
	}
	
	static public class Item {
		String name;
		
		public Item(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
