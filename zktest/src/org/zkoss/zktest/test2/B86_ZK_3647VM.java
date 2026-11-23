/* B86_ZK_3647VM.java

        Purpose:
                
        Description:
                
        History:
                Fri Aug 31 09:43:31 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.SimpleListModel;

public class B86_ZK_3647VM {
	private SimpleListModel<Integer> model;

	@Init
	public void init() {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < 15000; ++i) {
			list.add(i);
		}
		model = new SimpleListModel<>(list);
	}

	public SimpleListModel getModel() {
		return model;
	}
}
