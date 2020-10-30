/* B95_ZK_4644Composer.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 28 11:10:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.DefaultStepModel;
import org.zkoss.zkmax.zul.StepModel;
import org.zkoss.zkmax.zul.Stepbar;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class B95_ZK_4644Composer extends SelectorComposer<Component> {
	@Wire
	Component container;

	@Listen("onClick=#btn")
	public void doBtn() {
		Stepbar stepbar = new Stepbar();
		container.appendChild(stepbar);
		StepModel<String> stepbarModel = new DefaultStepModel<>();
		stepbarModel.add("1");
		stepbarModel.add("2");
		stepbarModel.add("3");
		stepbar.setModel(stepbarModel);
		container.appendChild(new Label("test"));
	}
}