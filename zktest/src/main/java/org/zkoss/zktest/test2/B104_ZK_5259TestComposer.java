/* B104_ZK_5259TestComposer.java

	Purpose:
		
	Description:
		
	History:
		Tue Feb 03 17:45:55 CST 2026, Created by josephlo

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Radiogroup;

public class B104_ZK_5259TestComposer extends SelectorComposer<Component> {
    @Listen(Events.ON_CLICK + "=#btn")
	public void doBtnClick() {
		Component rootComponent = Executions.getCurrent().createComponents("B104-ZK-5259-1.zul", null)[0];
		rootComponent.setPage(getSelf().getPage());
		Radiogroup testrg =  (Radiogroup) rootComponent.getFellow("test");
		Clients.log("items of test= " + testrg.getItems().toString());
	}
}
