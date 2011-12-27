package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class LoadSaveForm {

	Item value1;
	Item value2;
	Item value3;

	public LoadSaveForm() {
		value1 = new Item("A");
		value2 = new Item("B");
		value3 = new Item("C");
	}

	public Item getValue1() {
		return value1;
	}

	public void setValue1(Item value1) {
		this.value1 = value1;
	}

	public Item getValue2() {
		return value2;
	}

	public void setValue2(Item value2) {
		this.value2 = value2;
	}

	public Item getValue3() {
		return value3;
	}

	public void setValue3(Item value3) {
		this.value3 = value3;
	}
	@Command 
	public void cmd1() {

	}
	@Command 
	public void cmd2() {

	}
	@Command 
	public void cmd3(){
		
	}

	public class Item {

		String value;

		public Item(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@NotifyChange
		public void setValue(String value) {
			this.value = value;
		}

	}
}
