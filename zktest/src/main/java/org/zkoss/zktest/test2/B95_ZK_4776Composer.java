/* B95_ZK_4776Composer.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 22 09:50:20 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
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
public class B95_ZK_4776Composer extends SelectorComposer<Component> {
	private GoldenLayout gl;
	private Component comp;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.comp = comp;
	}

	@Listen("onClick=#btn")
	public void buttonClick() {
		gl = new GoldenLayout();
		gl.setAreas("A");
		GoldenPanel child1 = new GoldenPanel("A");
		child1.appendChild(new Label("foo"));
		gl.appendChild(child1);
		comp.appendChild(gl);
	}
}
