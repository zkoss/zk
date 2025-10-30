package org.zkoss.zktest.bind.issue;

import java.util.HashMap;
import java.util.Map;

public class B01059DifferentType {

int value1 =0;
	
	int value2 =0;
	
	int value3 =0;
	
	public double getValue1(){
		return value1;
	}
	
	public void setValue1(int value){
		this.value1 = value;
	}

	public int getValue2() {
		return value2;
	}

	public void setValue2(int value2) {
		this.value2 = value2;
	}

	public int getValue3() {
		return value3;
	}

	public void setValue3(double value3) {
		this.value3 = (int)value3;
	}
}
