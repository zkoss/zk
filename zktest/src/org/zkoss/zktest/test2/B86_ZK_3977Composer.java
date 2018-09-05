/* B86_ZK_3977Composer.java

        Purpose:
                
        Description:
                
        History:
                Wed Sep 05 14:13:54 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Radiogroup;

public class B86_ZK_3977Composer extends SelectorComposer {
	@Wire
	private Div div;
	private ListModelList<String> model = new ListModelList<>(new String[]{"A", "B"});

	@Listen("onClick = #button")
	public void toggle() {
		if (div.getChildren().size() != 0) {
			div.getChildren().clear();
		} else {
			Radiogroup radiogroup = new Radiogroup();
			radiogroup.setModel(model);
			radiogroup.addEventListener(Events.ON_CHECK, new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					Clients.log("selectedItem: " + radiogroup.getSelectedItem().getLabel());
				}
			});
			div.appendChild(radiogroup);
		}
	}
}
