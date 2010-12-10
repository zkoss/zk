/* TestRichlet2.java

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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

/**
 * Used to test richlet.
 *
 * @author tomyeh
 */
public class TestRichlet2 extends GenericRichlet {

	//Richlet//
	public void service(Page page) {
		page.setTitle("Richlet Test");
		
		final Window w = new Window("Richlet Test", "normal", false);
		final Vbox vb = new Vbox();
		vb.setParent(w);
		
		new Label("This is test2!").setParent(vb);
		new Label("Request: "+page.getRequestPath()).setParent(vb);
		final Label l = new Label();
		l.setId("l");
		l.setParent(vb);

		Button b = new Button("Throw null pointer exception");
		b.addEventListener(Events.ON_CLICK,
			new EventListener() {
				int count;
				public void onEvent(Event evt) {
					throw new NullPointerException();
				}
			});
		b.setParent(vb);
		w.setPage(page);
	
	}
}
