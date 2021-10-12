/* B86_ZK_4174Composer.java

        Purpose:
                
        Description:
                
        History:
                Fri Dec 28 15:05:18 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Label;

public class B86_ZK_4174Composer extends SelectorComposer {

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		B86_ZK_4174Component component = new B86_ZK_4174Component();
		component.appendChild(new Label("div"));
		comp.appendChild(component);
	}
}
