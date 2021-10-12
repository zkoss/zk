/* B96_ZK_4897Test.java

	Purpose:
		
	Description:
		
	History:
		Wed May 12 11:10:24 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class B96_ZK_4897Test extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect();
		final List<ComponentAgent> list = desktop.query("#content").getChildren();
		Assert.assertEquals(5, list.size());
		Assert.assertEquals("1", list.get(0).as(Label.class).getValue());
		Assert.assertEquals("2", list.get(1).as(Label.class).getValue());
		Assert.assertEquals("3", list.get(2).as(Label.class).getValue());
		Assert.assertEquals("4", list.get(3).as(Label.class).getValue());
		Assert.assertEquals("5", list.get(4).as(Label.class).getValue());
	}
}
