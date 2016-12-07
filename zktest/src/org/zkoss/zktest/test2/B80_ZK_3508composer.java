/* B80_ZK_3508composer.java

	Purpose:
		
	Description:
		
	History:
		Mon Dec  5 14:39:57 CST 2016, Created by wenninghsu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 * 
 * @author wenninghsu
 */
public class B80_ZK_3508composer extends SelectorComposer<Component> {
	private static final long serialVersionUID = -3976124305456564325L;

	@Wire
	Div bigDiv;

	@Wire
	Button button;

	@Wire
	Button button1;

	@Wire
	Button button2;

	@Override
	public void doAfterCompose(final Component comp) throws Exception {
		super.doAfterCompose(comp);

		button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				bigDiv.setVisible(false);
				Window window2 = new Window();
				window2.setVflex("1");
				window2.setParent(comp);

				Label label = new Label();
				label.setMultiline(true);
				label.setValue("aaa");
				label.setParent(window2);

				button.setVisible(false);
			}
		});

		button1.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				Window window2 = new Window();
				window2.setVflex("1");
				window2.setParent(comp);

				Label label = new Label();
				label.setMultiline(true);
				label.setValue("bbb");
				label.setParent(window2);
			}
		});

		button2.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				bigDiv.detach();
			}
		});
	}

}