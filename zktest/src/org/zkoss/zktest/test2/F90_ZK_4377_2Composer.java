/* F90_ZK_4377_2Composer.java

		Purpose:
		
		Description:
		
		History:
				Mon Oct 28 14:45:17 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.Linelayout;
import org.zkoss.zul.ListModelList;

import java.util.Arrays;
import java.util.ListIterator;

public class F90_ZK_4377_2Composer extends SelectorComposer {
	@Wire
	private Linelayout lineLayout;
	
	private ListModelList<String> model;
	
	@Override
	public void doAfterCompose(Component component) throws Exception {
		super.doAfterCompose(component);
		model = new ListModelList<>();
		for (int count = 1; count <= 4; count++) {
			model.add(count + "");
		}
		lineLayout.setModel(model);
	}
	
	@Listen("onClick=#addItem")
	public void handleAddItem() {
		model.add(model.getSize(), model.getSize() + 1 + "");
	}
	
	@Listen("onClick=#removeItem")
	public void handleRemoveItem() {
		model.remove(model.getSize() - 1);
	}
	
	@Listen("onClick=#changeItem")
	public void handleChangeItem() {
		final ListIterator<String> listIterator = model.listIterator();
		if (listIterator.hasNext()) {
			listIterator.next();
			listIterator.set("changed!");
		}
	}
	
	@Listen("onClick=#setModelABC")
	public void setModelABC() {
		model = new ListModelList<>(Arrays.asList("A", "B", "C"));
		lineLayout.setModel(model);
	}
	
	@Listen("onClick=#setModelNull")
	public void setModelNull() {
		lineLayout.setModel(null);
	}
}
