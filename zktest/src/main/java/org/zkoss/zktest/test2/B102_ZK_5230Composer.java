/* B102_ZK_5230Composer.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 14 11:31:22 CST 2025, Created by jameschu

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Barcodescanner;
import org.zkoss.zkmax.zul.event.DetectEvent;
import org.zkoss.zul.Window;

/**
 * @author jameschu
 */
public class B102_ZK_5230Composer extends SelectorComposer {
	@Wire
	Barcodescanner bs;
	@Wire
	Window win;


	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}


	@Listen("onClick=#deBtn")
	public void doDetach() {
		bs.detach();
	}

	@Listen("onClick=#atBtn")
	public void doReattach() {
		bs.setParent(win);
	}

	@Listen("onClick=#addBtn")
	public void addScanner() {
		Barcodescanner scanner = new Barcodescanner();
		scanner.setType("CODE128");
		scanner.setContinuous(true);
		scanner.setInterval(1000);
		scanner.setHeight("100px");
		scanner.addEventListener("onDetect", (EventListener<DetectEvent>) event -> Clients.log(event.getType() + " " + event.getResult()));
		win.appendChild(scanner);
	}
}
