/* ForEachVersusChildrenBindingTest.java

		Purpose:
		
		Description:
		
		History:
				Wed May 05 16:56:57 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class ForEachVersusChildrenBindingTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Assertions.assertEquals(1, desktop.queryAll("#wrong checkbox").size());
		Assertions.assertEquals(4, desktop.queryAll("#el checkbox").size());
		Assertions.assertEquals(4, desktop.queryAll("#children checkbox").size());

		desktop.query("#btn").click();

		Assertions.assertEquals(1, desktop.queryAll("#wrong checkbox").size());
		Assertions.assertEquals(4, desktop.queryAll("#el checkbox").size());
		Assertions.assertEquals(5, desktop.queryAll("#children checkbox").size());
	}
}
