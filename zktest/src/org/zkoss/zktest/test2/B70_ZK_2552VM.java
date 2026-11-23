/* B70_ZK_2552VM.java

	Purpose:
		
	Description:
		
	History:
		Thu, Jan 29, 2015  3:35:55 PM, Created by hanhsu

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

/**
 * 
 * @author hanhsu
 */

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

public class B70_ZK_2552VM {

	private ListModelList<String> tabsModel = new ListModelList<String>();

	@Init
	public void init() {
		tabsModel.add("aaa");
		tabsModel.add("bbb");
		tabsModel.add("ccc");
		tabsModel.add("ddd");
		tabsModel.add("eee");
	}
	
	public ListModelList<String> getTabsModel() {
		return tabsModel;
	}
	
	@Command("removeTab")
	@NotifyChange("tabsModel")
	public void removeTab(@BindingParam("tab") String tab) {
		tabsModel.remove(tab);
	}
}