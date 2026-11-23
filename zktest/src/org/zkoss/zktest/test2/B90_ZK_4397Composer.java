/* B90_ZK_4397Composer.java

	Purpose:
		
	Description:
		
	History:
		Mon Mar 16 18:30:20 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.GoldenLayout;
import org.zkoss.zkmax.zul.GoldenPanel;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B90_ZK_4397Composer extends SelectorComposer<Component> {
	@Wire
	GoldenLayout gl;
	@Wire
	Component container;

	@Listen("onClick=#btn")
	public void doBtn() {
		if (gl.getParent() != null) {
			gl.getParent().removeChild(gl);
		} else {
			GoldenPanel child = new GoldenPanel();
			child.appendChild(new Label("test"));
			child.setArea("A");
			gl.appendChild(child);
			container.appendChild(gl);
		}
	}
}
