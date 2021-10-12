/* B90_ZK_4491Composer.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 18 14:30:20 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;

/**
 * @author jameschu
 */
public class B90_ZK_4491Composer extends GenericForwardComposer {
	private Grid inboxGrid;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		inboxGrid.setModel(new ListModelList(getData()));
	}

	/* simply return a small model here , you could read data from database for your own implementation.*/
	private List<String> getData() {
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < 200; i++) {
			list.add("" + i);
		}
		list.add("Too long text");
		return list;
	}
}