/* F90_ZK_4379Composer.java

		Purpose:
		
		Description:
		
		History:
				Thu Oct 03 11:33:43 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.ui.util.Loadingbar;
import org.zkoss.zkmax.ui.util.LoadingbarControl;
import org.zkoss.zul.Intbox;

public class F90_ZK_4379Composer extends SelectorComposer<Component> {
	private LoadingbarControl loadingbarCtrl;
	
	@Wire
	Intbox index2;
	
	@Listen("onClick = #createCtrl")
	public void createCtrl() {
		loadingbarCtrl = Loadingbar.createLoadingbar("myId");
	}
	
	@Listen("onClick = #start")
	public void start() {
		loadingbarCtrl.start(5);
	}
	
	@Listen("onClick = #setValue")
	public void setValue() {
		int value = index2.getValue();
		loadingbarCtrl.update(value);
	}
	
	@Listen("onClick = #indeterminate")
	public void indeterminate() {
		loadingbarCtrl.update(true);
	}
	
	@Listen("onClick = #stopIndeterminate")
	public void stopIndeterminate() {
		loadingbarCtrl.update(false);
	}
	
	@Listen("onClick = #finish")
	public void finish() {
		loadingbarCtrl.finish();
	}
}
