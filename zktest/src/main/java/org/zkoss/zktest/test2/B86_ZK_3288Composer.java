/* B86_ZK_3288Composer.java

        Purpose:
                
        Description:
                
        History:
                Tue Sep 04 11:10:37 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Center;
import org.zkoss.zul.Div;

public class B86_ZK_3288Composer extends SelectorComposer {
	@Wire
	private Div container;
	@Wire
	private Center center;

	@Listen("onClick = #btn1")
	public void addContent() {
		container.getTemplate("content").create(container, null, null, null);
	}

	@Listen("onClick = #btn2")
	public void toggleAutoscroll() {
		center.setAutoscroll(!center.isAutoscroll());
	}
}
