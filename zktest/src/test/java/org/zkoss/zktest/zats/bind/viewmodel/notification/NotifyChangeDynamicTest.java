/* NotifyChangeDynamicTest.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 15:52:57 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.viewmodel.notification;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class NotifyChangeDynamicTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect("/bind/viewmodel/notification/notifychange-dynamic.zul");
		final ComponentAgent add = desktop.query("#add");
		final ComponentAgent addNoNotify = desktop.query("#addNoNotify");
		final Label current = desktop.query("#current").as(Label.class);
		add.click();
		add.click();
		add.click();
		Assert.assertEquals("15", current.getValue());

		addNoNotify.click();
		addNoNotify.click();
		addNoNotify.click();
		Assert.assertEquals("15", current.getValue());

		add.click();
		Assert.assertEquals("35", current.getValue());
	}
}
