package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;

public class B86_ZK_4016Composer extends SelectorComposer {
	@Listen("onClick = listitem")
	public void click() {
		System.out.println("click");
	}
}