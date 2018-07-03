/* F86_ZK_3658VM.java

        Purpose:
                
        Description:
                
        History:
                Tue Jul 03 15:29:53 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

public class F86_ZK_3658VM {
	private ListModelList<String> model = new ListModelList<>();
	private int data = 0;
	
	@Init
	public void init() {
		while (data < 10) {
			model.add("data : " + data++);
		}
	}
	
	public ListModelList<String> getModel() {
		return model;
	}
	
	@Command
	@NotifyChange("model")
	public void changeData(@BindingParam("command") String command) {
		if ("add".equals(command)) {
			model.add("data : " + data++);
		} else if ("remove".equals(command)) {
			model.remove(--data);
		}
	}
}
