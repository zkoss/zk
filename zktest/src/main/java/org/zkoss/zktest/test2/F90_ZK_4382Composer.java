/* F90_ZK_4382Composer.java

		Purpose:
		
		Description:
		
		History:
				Fri Sep 20 15:06:21 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Coachmark;

public class F90_ZK_4382Composer extends SelectorComposer<Component> {
	@Wire
	private Coachmark coachmark3;
	
	@Listen("onClick = #button1")
	public void target1() {
		Clients.log("task1");
	}
	
	@Listen("onClick = #button2")
	public void target2() {
		Clients.log("task2");
	}
	
	@Listen("onClick = #buttonP")
	public void position() {
		if (!"end_center".equals(coachmark3.getPosition()))
			coachmark3.setPosition("end_center");
		else
			coachmark3.setPosition("before_end");
	}
	
	@Listen("onClick = #button3")
	public void target3() {
		Clients.log("task3");
	}
}
