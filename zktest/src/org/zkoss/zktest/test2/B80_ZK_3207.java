/* B80_ZK_3207.java

	Purpose:
		
	Description:
		
	History:
		Mon, May 16, 2016 12:28:57 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
/**
 * 
 * @author Sefi
 */
public class B80_ZK_3207 extends GenericForwardComposer{
	@Wire
	private Groupbox groupbox;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}

	public void onClick$toggle(Event e) throws InterruptedException{
		Clients.resize(groupbox);
	}
}
