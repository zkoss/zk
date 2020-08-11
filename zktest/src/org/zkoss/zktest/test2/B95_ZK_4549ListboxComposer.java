/* B95_ZK_4549ListboxComposer.java

		Purpose:
		
		Description:
		
		History:
				Tue Aug 11 15:36:39 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

public class B95_ZK_4549ListboxComposer extends GenericForwardComposer {
	@Wire
	private Listbox listbox;
	
	private ListModelList myModel;
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		myModel = new ListModelList();
		listbox.setModel(myModel);
		myModel.add("test");
		myModel.clear();
		listbox.setModel(myModel);
	}
}
