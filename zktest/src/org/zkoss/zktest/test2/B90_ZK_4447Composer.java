/* B90_ZK_4447Composer.java

	Purpose:
		
	Description:
		
	History:
		Mon Feb 17 15:40:22 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Locale;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

/**
 * @author rudyhuang
 */
public class B90_ZK_4447Composer extends GenericForwardComposer<Component> {
	private ListModelList<Object> myModel;
	@Wire
	private Grid grid;
	@Wire
	private Listbox listbox;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		myModel = new ListModelList<>(Locale.getAvailableLocales());
		grid.setModel(myModel);
		listbox.setModel(myModel);

		myModel.clear();
		myModel.add("test");
	}
}
