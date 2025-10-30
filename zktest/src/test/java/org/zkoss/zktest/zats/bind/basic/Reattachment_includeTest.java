/* Reattachment_includeTest.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 28 14:27:59 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.basic;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * @author rudyhuang
 */
public class Reattachment_includeTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect("/bind/basic/reattachment_include.zul");
		final List<ComponentAgent> buttons = desktop.queryAll("#myWin > button");

		buttons.get(0).click();
		buttons.get(1).click();

		// Same as ArgsTest
		final Label l1 = desktop.query("#inc window #l1").as(Label.class);
		final Label l2 = desktop.query("#inc window #l2").as(Label.class);
		final Label l3 = desktop.query("#inc window #l3").as(Label.class);
		final Label l4 = desktop.query("#inc window #l4").as(Label.class);
		final Textbox t1 = desktop.query("#inc window #t1").as(Textbox.class);
		final Textbox t2 = desktop.query("#inc window #t2").as(Textbox.class);
		final Textbox t3 = desktop.query("#inc window #t3").as(Textbox.class);
		final Textbox t4 = desktop.query("#inc window #t4").as(Textbox.class);
		Assertions.assertEquals("A-Arg1", l1.getValue());
		Assertions.assertEquals("B-myarg1", l2.getValue());
		Assertions.assertEquals("", l3.getValue());
		Assertions.assertEquals("", l4.getValue());
		Assertions.assertEquals("A-Arg1", t1.getValue());
		Assertions.assertEquals("B-myarg1", t2.getValue());
		Assertions.assertEquals("A", t3.getValue());
		Assertions.assertEquals("A", t4.getValue());

		desktop.query("#inc window #btn1").click();
		Assertions.assertEquals("ADennis-Arg1", l1.getValue());
		Assertions.assertEquals("BChen-myarg1", l2.getValue());
		Assertions.assertEquals("", l3.getValue());
		Assertions.assertEquals("", l4.getValue());
		Assertions.assertEquals("ADennis-Arg1", t1.getValue());
		Assertions.assertEquals("BChen-myarg1", t2.getValue());
		Assertions.assertEquals("ADennis", t3.getValue());
		Assertions.assertEquals("A", t4.getValue());
	}
}
