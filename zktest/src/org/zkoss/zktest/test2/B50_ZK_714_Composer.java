/* B50ZK714Controller.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Dec 27, 2011 3:48:09 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * 
 * @author simonpai
 */
public class B50_ZK_714_Composer extends GenericForwardComposer {
	
	Listbox box;
	
	public void onCreate$win(Event event) {
		onClick$reload();
		box.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem item, Object data, int index) throws Exception {
				Listcell cell = new Listcell();
				cell.setParent(item);
				cell.setLabel("wrong");
				item.setValue(data);
				
				//in my case, cell's value was defered loaded, so I simulate it.
				Events.postEvent(new Event("onLater", box, new Object[]{ cell, data }));
			}
		});
	}
	
	public void onLater$box(Event event) {
		event = ((ForwardEvent) event).getOrigin();
		Listcell cell = (Listcell) ((Object[]) event.getData())[0];
		String data = (String) ((Object[]) event.getData())[1];
		cell.setLabel(data);
	}
	
	public void onClick$reload() {
		ListModelList model = new ListModelList();
		for (int i = 0; i < 10; i++)
			model.add("Item" + i);
		box.setModel(model);
	}
	
}
