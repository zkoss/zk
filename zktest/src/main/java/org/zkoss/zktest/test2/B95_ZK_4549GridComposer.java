/* B95_ZK_4549GridComposer.java

		Purpose:
		
		Description:
		
		History:
				Tue Aug 11 10:47:50 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;

public class B95_ZK_4549GridComposer extends GenericForwardComposer {
	@Wire
	private Grid grid;
	
	private ListModelList myModel;
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		myModel = new ListModelList();
		grid.setModel(myModel);
		myModel.add("test");
		myModel.clear();
		grid.setModel(myModel);
	}
}
