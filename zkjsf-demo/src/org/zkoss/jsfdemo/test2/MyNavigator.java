package org.zkoss.jsfdemo.test2;

public class MyNavigator {
	
	private String text;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String doSuccess(){
		return "success";
	}
	
	public String doFailed(){
		return "failed";
	}
}
