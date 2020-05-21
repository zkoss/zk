/* B91_ZK_4537VM.java

		Purpose:
		
		Description:
		
		History:
				Tue May 19 09:45:57 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zul.ListModelList;

public class B91_ZK_4537VM {
	private ListModelList<String> testChoose = new ListModelList<>();
	
	public ListModelList<String> getTestChoose() {
		return testChoose;
	}
	
	public void setTestChoose(ListModelList<String> testChoose) {
		this.testChoose = testChoose;
	}
	
	@Command
	public void addC1() {
		testChoose.clear();
		for (int i = 0; i < 10; i++) {
			testChoose.add("C1-Test" + i);
			testChoose.addToSelection(testChoose.get(i));
		}
		BindUtils.postNotifyChange(null, null, this, "testChoose");
		
	}
	
	@Command
	public void addC2() {
		testChoose.clear();
		for (int i = 0; i < 5; i++) {
			testChoose.add("C2-Test" + i);
			testChoose.addToSelection(testChoose.get(i));
		}
		BindUtils.postNotifyChange(null, null, this, "testChoose");
	}
}
