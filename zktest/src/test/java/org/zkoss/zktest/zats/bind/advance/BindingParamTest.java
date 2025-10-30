/* BindingParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 10:33:48 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class BindingParamTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		final Label msg = desktop.query("#msg").as(Label.class);
		final ComponentAgent indexBtn = desktop.queryAll("row").get(2).queryAll("button").get(0);
		final ComponentAgent updateBtn = desktop.queryAll("row").get(0).queryAll("button").get(2);

		indexBtn.click();
		assertEquals("item index 2", msg.getValue());

		updateBtn.click();
		assertEquals("updated item name to: AA", msg.getValue());
	}

	@Test
	public void testOmit() {
		final DesktopAgent desktop = connect();
		final Label msg = desktop.query("#msg").as(Label.class);
		final ComponentAgent indexOmitBtn = desktop.queryAll("row").get(3).queryAll("button").get(1);
		final ComponentAgent updateOmitBtn = desktop.queryAll("row").get(1).queryAll("button").get(3);

		indexOmitBtn.click();
		assertEquals("item index 3", msg.getValue());

		updateOmitBtn.click();
		assertEquals("updated item name to: BB", msg.getValue());
	}
}
