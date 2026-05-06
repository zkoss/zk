/* B104_ZK_6073Composer.java

        Purpose:
                
        Description:
                
        History:
                Wed May 06 20:22:10 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

/**
 * Composer for B104-ZK-6073: tracks every onChanging fired by the combobox and
 * exposes counts to the DOM so a WebDriver test can assert that arrow-key
 * navigation only ever fires onChanging with isChangingBySelectBack=true.
 */
public class B104_ZK_6073Composer extends SelectorComposer<Component> {

	@Wire
	private Label totalCount;
	@Wire
	private Label nonSelectBackCount;
	@Wire
	private Label lastValue;

	private int total;
	private int nonSelectBack;

	@Listen("onChanging=#cb")
	public void doCbOnChanging(InputEvent evt) {
		total++;
		totalCount.setValue(String.valueOf(total));
		lastValue.setValue(evt.getValue() + "|sb=" + evt.isChangingBySelectBack());
		if (!evt.isChangingBySelectBack()) {
			nonSelectBack++;
			nonSelectBackCount.setValue(String.valueOf(nonSelectBack));
		}
	}
}
