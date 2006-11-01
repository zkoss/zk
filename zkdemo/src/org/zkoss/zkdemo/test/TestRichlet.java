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
		new Label("Hello World!").setParent(w);
		final Label l = new Label();
		l.setParent(w);

		final Button b = new Button("Change");
		b.addEventListener(Events.ON_CLICK,
			new EventListener() {
				int count;
				public boolean isAsap() {
					return true;
				}
				public void onEvent(Event evt) {
					l.setValue("" + ++count);
				}
			});
		b.setParent(w);

		w.setPage(page);
	}
}
