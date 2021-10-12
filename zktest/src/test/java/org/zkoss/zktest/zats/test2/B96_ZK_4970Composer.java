/* B96_ZK_4970Composer.java

	Purpose:
		
	Description:
		
	History:
		4:21 PM 2021/11/18, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.Collections;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListitemRenderer;

/**
 * @author jumperchen
 */
public class B96_ZK_4970Composer implements Composer<Div> {

	private Div div;

	@Override
	public void doAfterCompose(Div comp) throws Exception {

		Button button = new Button("create");
		comp.appendChild(button);

		button.addEventListener("onClick", event -> {
			div = (Div) Executions.createComponentsDirectly(
					"<div apply='${arg.innerComposer}'><listbox/></div>", "zul",
					null, Collections.singletonMap("innerComposer",
							(Composer<Div>) div -> {
								div.addEventListener("onInitModel", event2 -> {
									Listbox listbox = (Listbox) div.getFirstChild();
									// workaround detach/re-attach the listbox before setting model and item renderer
									//							listbox.detach();
									//							div.appendChild(listbox);
									listbox.setModel(new ListModelList<>(
											Arrays.asList("aaa", "bbb",
													"ccc")));
									listbox.setItemRenderer(
											(ListitemRenderer<String>) (item, data, index) -> item.setLabel(
													data));
								});
								Events.postEvent("onInitModel", div, null);
								// Events.sendEvent("onInitModel", div, null); using send event also works
							}));

		});
	}
}