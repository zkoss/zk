/* NavigationModelTest.java

		Purpose:
		
		Description:
		
		History:
				Mon May 10 17:37:45 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class NavigationModelTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Label l1 = desktop.query("#l1").as(Label.class);

		Assert.assertEquals("AAA", l1.getValue());
		Assert.assertEquals("none", desktop.query("#l2").as(Label.class).getValue());
		Assert.assertEquals(4, desktop.queryAll("a").size());
		Assert.assertEquals(1, desktop.queryAll("#lv2 a").size());

		desktop.query("a[label='BBB']").click();
		Assert.assertEquals("BBB", l1.getValue());
		Assert.assertEquals("none", desktop.query("#l2").as(Label.class).getValue());
		Assert.assertEquals(4, desktop.queryAll("a").size());
		Assert.assertEquals(1, desktop.queryAll("#lv2 a").size());

		desktop.query("a[label='BBB1']").click();
		Assert.assertEquals("BBB", l1.getValue());
		Assert.assertEquals("BBB1", desktop.query("#l2").as(Label.class).getValue());
		Assert.assertEquals(4, desktop.queryAll("a").size());
		Assert.assertEquals(1, desktop.queryAll("#lv2 a").size());

		desktop.query("a[label='CCC']").click();
		Assert.assertEquals("CCC", l1.getValue());
		Assert.assertEquals("none", desktop.query("#l2").as(Label.class).getValue());
		Assert.assertEquals(3, desktop.queryAll("a").size());
		Assert.assertEquals(0, desktop.queryAll("#lv2 a").size());
	}
}
