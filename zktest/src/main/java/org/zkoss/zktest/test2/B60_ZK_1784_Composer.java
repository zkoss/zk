package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;

public class B60_ZK_1784_Composer extends SelectorComposer<Component> {

	@Wire
	Tab tab2;
	
	@Wire
	Tabbox tabbox;
	
	@Listen("onClick=button")
	public void dbclick() {
		tabbox.invalidate();
	}
}
