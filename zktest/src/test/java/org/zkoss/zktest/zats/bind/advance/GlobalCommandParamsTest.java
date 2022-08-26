/* GlobalCommandParamsTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 11:59:03 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class GlobalCommandParamsTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		List<ComponentAgent> buttons = desktop.queryAll("button");
		buttons.get(0).click();
		buttons.get(1).click();
		buttons.get(2).click();
		Assertions.assertArrayEquals(
			new String[] {
				"GlobalCommand global1 executed: global1",
				"Local Command local2 executed: local2",
				"GlobalCommand global2 executed: global2",
				"Local Command local2 executed: omit",
				"GlobalCommand global2 executed: omit"
			},
			desktop.getZkLog().toArray()
		);
	}
}
