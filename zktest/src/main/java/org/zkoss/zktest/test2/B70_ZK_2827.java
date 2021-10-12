/* B70_ZK_2827.java

	Purpose:
		
	Description:
		
	History:
		11:47 AM 8/3/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;

/**
 * @author jumperchen
 */
public class B70_ZK_2827  extends SelectorComposer<Component> {

	private Desktop desktop;
	private Component root;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.root = comp;

		desktop = Executions.getCurrent().getDesktop();
		desktop.enableServerPush(true);
	}

	@Listen("onClick = #addContainer")
	public void addContainer() {
		final Div container = new Div();
		container.setStyle("border: 1px solid red");

		Button removeButton = new Button("click me to invalidate asynch (if you see an error message dialog, it's a bug)");
		removeButton.addEventListener("onClick", new EventListener<Event>() {
					public void onEvent(Event event) throws Exception {
						new Thread(new Runnable() {
							public void run() {
								try {
									Executions.activate(desktop);
									//an asynch operation leading to invalidate
									container.invalidate();
									Executions.deactivate(desktop);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
						container.addEventListener("onEchoClose",
								new EventListener<Event>() {
									public void onEvent(Event event)
											throws Exception {
										container.detach();
									}
								});
					//echo event to increase the chance "onEchoClose" and "dummy" arrive with the same request
					//simulates a user event arriving at the server simultaneously with the "dummy" request
					Events.echoEvent("onEchoClose", container, (Object)null);
				}});

		container.appendChild(removeButton);

		root.appendChild(container);
	}
}