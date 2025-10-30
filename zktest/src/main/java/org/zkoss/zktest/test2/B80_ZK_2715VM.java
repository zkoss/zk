/* B80_ZK_3282VM.java

	Purpose:
		
	Description:
		
	History:
		Wed, Aug 17, 2016 12:21:26 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;
/**
 * 
 * @author Sefi
 */
public class B80_ZK_2715VM {
	private ListModelList<String> tabsModel = new ListModelList<String>();
	private int index = 1;

	@Init
	public void init(){
		add();
		add();
	}
	
	/**
	 * Remove @NotifyChange to avoid re-rendering the whole tabbox
	 */
	@Command
	public void add() {
		tabsModel.add(0, "tab"+index);
		index++;
	}

	@Command
	public void set() {
		tabsModel.set(1 ,"tab"+index);
		index++;
	}
	public ListModelList<String> getTabsModel() {
		return tabsModel;
	}
}
