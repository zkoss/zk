/* B96_ZK_4890Composer.java

	Purpose:
		
	Description:
		
	History:
		Wed May 19 14:44:04 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

/**
 * @author rudyhuang
 */
public class B96_ZK_4890Composer extends SelectorComposer<Component> {
	@Wire
	private Listbox lb;
	@Wire
	private Grid gr;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		final ListModelList<String> listModel = createListModel();
		lb.setModel(listModel);
		gr.setModel(listModel);
		lb.scrollToIndex(500);
		gr.scrollToIndex(500);
	}

	private ListModelList<String> createListModel() {
		ListModelList<String> model = new ListModelList<>();
		for (int i = 0; i < 1000; i++) {
			model.add("foo" + i);
		}
		return model;
	}
}
