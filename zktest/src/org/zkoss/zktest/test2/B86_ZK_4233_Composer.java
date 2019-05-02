/* B86_ZK_4233_Composer.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 30 17:42:17 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Include;

public class B86_ZK_4233_Composer extends SelectorComposer {
	private String src = "B86-ZK-4233-include.zul";
	private int i = 0;
	
	@Wire
	private Include inc;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		inc.setSrc(src);
		
		EventQueue<Event> lookup = EventQueues.lookup("testq", true);
		lookup.subscribe(new EventListener<Event>() {
			
			public void onEvent(Event event) throws Exception {
				updateIncludeSrc();
			}
		});
	}
	
	@Listen("onClick = #btnInc")
	public void doIncBtn(){
		updateIncludeSrc();
	}
	
	private void updateIncludeSrc() {
		String src2 = src+"?test="+i++;
		inc.setSrc(src2);
		Clients.log(src2);
	}
}
