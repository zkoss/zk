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
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		ListModelList<Object> infos = new ListModelList<Object>(new String[] {"Apple", "Orange", "Mango"});
		combo.setModel(infos);
		gd.setModel(infos);
		listb.setModel(infos);
		
		infos.remove(0);
	}
}
