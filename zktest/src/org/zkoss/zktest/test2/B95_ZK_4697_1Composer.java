/** B95_ZK_4697_1Composer.java.

	Purpose:

	Description:

	History:
		Mon Oct 19 12:27:53 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

/**
 * @author jameschu
 */
public class B95_ZK_4697_1Composer extends SelectorComposer<Component> {
	@Wire
	Window modalDialog;

	@Listen("onClick = #cancel")
	public void showModal(Event e) {
		modalDialog.detach();
	}
}
