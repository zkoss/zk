/* IfVersusVisibleTest.java

		Purpose:
		
		Description:
		
		History:
				Wed May 05 15:50:25 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Button;

public class IfVersusVisibleTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Button visible = desktop.query("#visible").as(Button.class);
		Button disabled = desktop.query("#disabled").as(Button.class);

		Assertions.assertTrue(desktop.query("#wrong") == null);
		Assertions.assertTrue(desktop.query("#el") != null);
		Assertions.assertEquals(true, visible.isVisible());
		Assertions.assertEquals(false, disabled.isDisabled());

		desktop.query("#checkbox").check(false);

		Assertions.assertTrue(desktop.query("#wrong") == null);
		Assertions.assertTrue(desktop.query("#el") != null);
		Assertions.assertEquals(false, visible.isVisible());
		Assertions.assertEquals(true, disabled.isDisabled());
	}
}
