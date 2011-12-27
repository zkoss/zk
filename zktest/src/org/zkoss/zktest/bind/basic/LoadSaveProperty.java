package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class LoadSaveProperty {

	String value1;
	String value2;
	String value3;
	
	public LoadSaveProperty(){
		value1 = "A";
		value2 = "B";
		value3 = "C";
	}

	public String getValue1() {
		return value1;
	}

	@NotifyChange
	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	@NotifyChange
	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getValue3() {
		return value3;
	}

	@NotifyChange
	public void setValue3(String value3) {
		this.value3 = value3;
	}
	@Command 
	public void cmd1(){
		
	}
	@Command 
	public void cmd2(){
		
	}
	@Command 
	public void cmd3(){
		
	}
}
