package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class LoadSaveForm {

	FormItem value1;
	FormItem value2;
	FormItem value3;

	public LoadSaveForm() {
		value1 = new FormItem("A");
		value2 = new FormItem("B");
		value3 = new FormItem("C");
	}

	public FormItem getValue1() {
		return value1;
	}

	public void setValue1(FormItem value1) {
		this.value1 = value1;
	}

	public FormItem getValue2() {
		return value2;
	}

	public void setValue2(FormItem value2) {
		this.value2 = value2;
	}

	public FormItem getValue3() {
		return value3;
	}

	public void setValue3(FormItem value3) {
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
}
