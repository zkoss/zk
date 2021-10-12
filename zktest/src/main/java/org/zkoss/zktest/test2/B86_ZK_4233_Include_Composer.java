/* B86_ZK_4233_Include_Composer.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 30 17:42:55 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Window;

public class B86_ZK_4233_Include_Composer extends SelectorComposer<Window> {
	private Window win;
	
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		win = comp;
	}
	
	@Listen("onClick=#inBtn")
	public void inBtnClick(){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Messagebox.show("test","test",Messagebox.OK,Messagebox.INFORMATION);
		EventQueue<Event> lookup = EventQueues.lookup("testq", true);
		lookup.publish(new Event("test"));
	}
}
