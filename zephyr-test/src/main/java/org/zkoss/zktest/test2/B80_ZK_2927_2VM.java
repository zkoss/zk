package org.zkoss.zktest.test2;

import org.zkoss.zul.ListModelList;
public class B80_ZK_2927_2VM {
	private ListModelList model = new ListModelList();
	public String c1 = "another";

	public ListModelList getFilteredView() {
		model.add("a");
		model.remove(0);
		return model;
	}

	public String getC1() {
		return c1;
	}
}
