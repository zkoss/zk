/** B70_ZK_2682_Composer.java.

	Purpose:
		
	Description:
		
	History:
		9:30:57 AM Jun 18, 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

/**
 * @author jameschu
 *
 */
@SuppressWarnings("serial")
public class B70_ZK_2682_Composer extends SelectorComposer<Component> {

	@Wire
	private Combobox combo;
	@Wire
	private Grid gd;
	@Wire
	private Listbox listb;
	@Wire
	private Grid gdp;
	@Wire
	private Listbox listbp;
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		ListModelList<Object> infos = new ListModelList<Object>(new String[] {"Banana", "Kiwi", "Pineapple"});
		combo.setModel(infos);
		gd.setModel(infos);
		listb.setModel(infos);
		
		gdp.setMold("paging");
		gdp.setPageSize(1);
		gdp.setModel(infos);
		listbp.setMold("paging");
		listbp.setPageSize(1);
		listbp.setModel(infos);
		
		infos.clear();
		infos.add("Apple");
		infos.add("Orange");
	}
}
