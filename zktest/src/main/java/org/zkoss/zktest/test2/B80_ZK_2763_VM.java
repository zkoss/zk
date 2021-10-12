package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.SelectorComposer;

public class B80_ZK_2763_VM {
	Item item;

	public B80_ZK_2763_VM() {
		item = new Item("A");
	}
	
	public Item getItem() {
		return item;
	}

	@Command
	@NotifyChange("item")
	public void cmd(){
		item.setName("B");
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
