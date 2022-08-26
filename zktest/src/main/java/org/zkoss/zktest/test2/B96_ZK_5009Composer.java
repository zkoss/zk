/* B96_ZK_5009Composer.java

	Purpose:
		
	Description:
		
	History:
		4:26 PM 2021/11/16, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;

/**
 * @author jumperchen
 */
public class B96_ZK_5009Composer extends SelectorComposer {

	@Wire Tabbox tabbox;

	@Listen("onClick = #btn")
	public void onClick$btn(Event e) throws InterruptedException {
		Tabs tabs = (Tabs) tabbox.getFirstChild();
		Tabpanels tabpanels = (Tabpanels) tabbox.getLastChild();

		Tab tab = new Tab();
		Caption caption = buildCaption();

		tab.appendChild(caption);
		Tabpanel panel = new Tabpanel();
		panel.appendChild(new Label("added panel"));
		tabs.appendChild(tab);
		tabpanels.appendChild(panel);
	}

	private Caption buildCaption() {
		Caption caption = new Caption("caption title");
		caption.appendChild(new Button("a btn"));

		//any of the following line cause js error
		caption.appendChild(new Radio());
		//        caption.appendChild(new Checkbox("test"));
		//        caption.appendChild(new Textbox());
		//        caption.appendChild(new Intbox());
		return caption;
	}
}