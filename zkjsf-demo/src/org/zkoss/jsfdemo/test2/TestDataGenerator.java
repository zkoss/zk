package org.zkoss.jsfdemo.test2;

public class TestDataGenerator {
	
	public static String[] generateNumerousStrings(int n){
		String[] data = new String[n];
		for(int j=0; j < data.length; ++j) {
			data[j] = "option "+j;
		}
		return data;
	}
}
