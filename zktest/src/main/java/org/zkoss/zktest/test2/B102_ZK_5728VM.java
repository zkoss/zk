/* B102_ZK_5728VM.java

        Purpose:
                
        Description:
                
        History:
                Tue Mar 25 10:22:40 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;

public class B102_ZK_5728VM {
	List<String> valueList = List.of("a", "b");
	private String selectedItem = "a";

	@Command
	@NotifyChange("selectedItem")
	public void next() {
		int index = valueList.indexOf(selectedItem);
		int next = (index + 1) % valueList.size();
		selectedItem = valueList.get(next);
	}

	public String getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
	}
}
