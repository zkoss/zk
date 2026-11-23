package org.zkoss.zktest.bind.basic;


public class ELTagVM {

	String value;
	
	
	
	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}



	public static String cat(String str1,String str2){
		return str1+":"+str2;
	}
	public static String cat2(String str1,String str2){
		return str1+"-"+str2;
	}
	
}
