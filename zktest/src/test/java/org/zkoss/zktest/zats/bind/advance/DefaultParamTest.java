/* DefaultParamTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 04 12:47:43 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class DefaultParamTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		List<ComponentAgent> buttons = desktop.queryAll("button");
		buttons.get(0).click();
		buttons.get(1).click();
		Assertions.assertArrayEquals(
			new String[] {
				"test param: -1",
				"test param: 2"
			},
			desktop.getZkLog().toArray()
		);
	}

	@Test
	public void omitTest() {
		DesktopAgent desktop = connect();

		List<ComponentAgent> buttons = desktop.queryAll("button");
		buttons.get(2).click();
		// TODO: bug?
		// buttons.get(3).click();
		Assertions.assertArrayEquals(
			new String[] {
				"test param: -1"
				// TODO: bug?
				//, "test param: 2"
			},
			desktop.getZkLog().toArray()
		);
	}
}
