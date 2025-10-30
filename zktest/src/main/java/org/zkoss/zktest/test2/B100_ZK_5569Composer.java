package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Radio;

public class B100_ZK_5569Composer extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Listen("onCheck = #categorySelector")
	public void selectCategory(CheckEvent event) {
		Clients.log(event.getName() + ": " + ((Radio) event.getTarget()).getLabel());
	}
}