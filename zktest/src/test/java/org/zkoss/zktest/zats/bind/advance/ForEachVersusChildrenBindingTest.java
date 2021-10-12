/* ForEachVersusChildrenBindingTest.java

		Purpose:
		
		Description:
		
		History:
				Wed May 05 16:56:57 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class ForEachVersusChildrenBindingTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Assert.assertEquals(1, desktop.queryAll("#wrong checkbox").size());
		Assert.assertEquals(4, desktop.queryAll("#el checkbox").size());
		Assert.assertEquals(4, desktop.queryAll("#children checkbox").size());

		desktop.query("#btn").click();

		Assert.assertEquals(1, desktop.queryAll("#wrong checkbox").size());
		Assert.assertEquals(4, desktop.queryAll("#el checkbox").size());
		Assert.assertEquals(5, desktop.queryAll("#children checkbox").size());
	}
}
