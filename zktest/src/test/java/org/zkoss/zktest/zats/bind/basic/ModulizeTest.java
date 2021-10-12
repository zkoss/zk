/* ModulizeTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 28 18:00:32 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.CloseAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class ModulizeTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect("/bind/basic/modulize.zul");
		final ComponentAgent addModule1 = desktop.query("window #addModule1");
		final ComponentAgent addModule2 = desktop.query("window #addModule2");
		final Label moduleCount = desktop.query("window #moduleCount").as(Label.class);
		final Label moduleAmount = desktop.query("window #moduleAmount").as(Label.class);

		addModule1.click();
		Assert.assertEquals("2", moduleCount.getValue());
		Assert.assertEquals(2, desktop.queryAll("window tab").size());

		addModule2.click();
		Assert.assertEquals("3", moduleCount.getValue());
		Assert.assertEquals(3, desktop.queryAll("window tab").size());

		desktop.queryAll("window tab").get(2).as(CloseAgent.class).close();
		Assert.assertEquals("2", moduleCount.getValue());
		Assert.assertEquals(2, desktop.queryAll("window tab").size());

		// Try to decrease the amount by 100
		final ComponentAgent defaultModule1Amount = desktop.query("window tabpanel:first-child window intbox");
		final int moduleAmountValue = Integer.parseInt(moduleAmount.getValue());
		int value = defaultModule1Amount.as(Intbox.class).getValue();
		defaultModule1Amount.type(String.valueOf(value - 100));
		Assert.assertEquals(String.valueOf(moduleAmountValue - 100), moduleAmount.getValue());
	}
}
