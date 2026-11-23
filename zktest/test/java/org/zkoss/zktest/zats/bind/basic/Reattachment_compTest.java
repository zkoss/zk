/* Reattachment_compTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 28 15:05:51 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * @author rudyhuang
 */
public class Reattachment_compTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect("/bind/basic/reattachment_comp.zul");

		final List<ComponentAgent> buttons = desktop.queryAll("#myWin > button");

		buttons.get(0).click();
		buttons.get(1).click();

		// Same as ArgsTest
		final Label l1 = desktop.query("#subWin #l1").as(Label.class);
		final Label l2 = desktop.query("#subWin #l2").as(Label.class);
		final Label l3 = desktop.query("#subWin #l3").as(Label.class);
		final Label l4 = desktop.query("#subWin #l4").as(Label.class);
		final Textbox t1 = desktop.query("#subWin #t1").as(Textbox.class);
		final Textbox t2 = desktop.query("#subWin #t2").as(Textbox.class);
		final Textbox t3 = desktop.query("#subWin #t3").as(Textbox.class);
		final Textbox t4 = desktop.query("#subWin #t4").as(Textbox.class);
		Assert.assertEquals("A-Arg1", l1.getValue());
		Assert.assertEquals("B-myarg1", l2.getValue());
		Assert.assertEquals("", l3.getValue());
		Assert.assertEquals("", l4.getValue());
		Assert.assertEquals("A-Arg1", t1.getValue());
		Assert.assertEquals("B-myarg1", t2.getValue());
		Assert.assertEquals("A", t3.getValue());
		Assert.assertEquals("A", t4.getValue());

		desktop.query("#subWin #btn1").click();
		Assert.assertEquals("ADennis-Arg1", l1.getValue());
		Assert.assertEquals("BChen-myarg1", l2.getValue());
		Assert.assertEquals("", l3.getValue());
		Assert.assertEquals("", l4.getValue());
		Assert.assertEquals("ADennis-Arg1", t1.getValue());
		Assert.assertEquals("BChen-myarg1", t2.getValue());
		Assert.assertEquals("ADennis", t3.getValue());
		Assert.assertEquals("A", t4.getValue());
	}
}
