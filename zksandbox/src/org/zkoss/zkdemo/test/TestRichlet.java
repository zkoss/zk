/* TestRichlet.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct  5 12:29:35     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;

/**
 * Used to test richlet.
 *
 * @author tomyeh
 */
public class TestRichlet extends GenericRichlet {
	//Richlet//
	public void service(Page page) {
		page.setTitle("Richlet Test");

		final Window w = new Window("Richlet Test", "normal", false);
		final Vbox vb = new Vbox();
		vb.setParent(w);

		new Label("Hello World!").setParent(vb);
		new Label("Request: "+page.getRequestPath()).setParent(vb);
		final Label l = new Label();
		l.setId("l");
		l.setParent(vb);

		Button b = new Button("Change");
		b.addEventListener(Events.ON_CLICK,
			new EventListener() {
				int count;
				public void onEvent(Event evt) {
					l.setValue("" + ++count);
				}
			});
		b.setParent(vb);

		b = new Button("Create Directly");
		b.addEventListener(Events.ON_CLICK,
			new EventListener() {
				int count;
				public void onEvent(Event evt) {
					Executions.createComponentsDirectly(
						"<textbox value=\"${l.value}\"/>", null, vb, null);
				}
			});
		b.setParent(vb);

		w.setPage(page);
	}
}
