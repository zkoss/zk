/* NavigationModelTest.java

		Purpose:
		
		Description:
		
		History:
				Mon May 10 17:37:45 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class NavigationModelTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Label l1 = desktop.query("#l1").as(Label.class);

		Assertions.assertEquals("AAA", l1.getValue());
		Assertions.assertEquals("none", desktop.query("#l2").as(Label.class).getValue());
		Assertions.assertEquals(4, desktop.queryAll("a").size());
		Assertions.assertEquals(1, desktop.queryAll("#lv2 a").size());

		desktop.query("a[label='BBB']").click();
		Assertions.assertEquals("BBB", l1.getValue());
		Assertions.assertEquals("none", desktop.query("#l2").as(Label.class).getValue());
		Assertions.assertEquals(4, desktop.queryAll("a").size());
		Assertions.assertEquals(1, desktop.queryAll("#lv2 a").size());

		desktop.query("a[label='BBB1']").click();
		Assertions.assertEquals("BBB", l1.getValue());
		Assertions.assertEquals("BBB1", desktop.query("#l2").as(Label.class).getValue());
		Assertions.assertEquals(4, desktop.queryAll("a").size());
		Assertions.assertEquals(1, desktop.queryAll("#lv2 a").size());

		desktop.query("a[label='CCC']").click();
		Assertions.assertEquals("CCC", l1.getValue());
		Assertions.assertEquals("none", desktop.query("#l2").as(Label.class).getValue());
		Assertions.assertEquals(3, desktop.queryAll("a").size());
		Assertions.assertEquals(0, desktop.queryAll("#lv2 a").size());
	}
}
