package org.zkoss.zktest.bind.issue.parser;

import org.zkoss.bind.annotation.Init;

public class F80_Parser_MVVMAutoApplyVM {
	
	private String label1 = "test1";
	private String label2 = "test2";
	private String label3 = "test3";
	
	public String getLabel1() {
		return label1;
	}

	public void setLabel1(String label1) {
		this.label1 = label1;
	}

	public String getLabel2() {
		return label2;
	}

	public void setLabel2(String label2) {
		this.label2 = label2;
	}

	public String getLabel3() {
		return label3;
	}

	public void setLabel3(String label3) {
		this.label3 = label3;
	}

	@Init
	public void init() {
	}
	
}
