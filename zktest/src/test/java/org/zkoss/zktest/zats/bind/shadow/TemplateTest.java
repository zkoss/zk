/* TemplateTest.java

	Purpose:
		
	Description:
		
	History:
		Fri May 07 16:20:23 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.shadow;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class TemplateTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect("/bind/shadow/template.zul");
		final List<ComponentAgent> navitems = desktop.queryAll("#navbar > navitem");
		final List<ComponentAgent> navs = desktop.queryAll("#navbar > nav");
		Assert.assertEquals(4, navitems.size());
		Assert.assertEquals(2, navs.size());
		Assert.assertEquals(4, navs.get(0).getChildren().size());
		Assert.assertEquals(3, navs.get(1).getChildren().size());
	}

	@Test
	public void testExternal() {
		final DesktopAgent desktop = connect("/bind/shadow/template-external.zul");
		final List<ComponentAgent> navitems = desktop.queryAll("#navbar > navitem");
		final List<ComponentAgent> navs = desktop.queryAll("#navbar > nav");
		Assert.assertEquals(4, navitems.size());
		Assert.assertEquals(2, navs.size());
		Assert.assertEquals(4, navs.get(0).getChildren().size());
		Assert.assertEquals(3, navs.get(1).getChildren().size());
	}

	@Test
	public void testSrc() {
		final DesktopAgent desktop = connect("/bind/shadow/template-src.zul");
		final List<ComponentAgent> navitems = desktop.queryAll("#navbar > navitem");
		final List<ComponentAgent> navs = desktop.queryAll("#navbar > nav");
		Assert.assertEquals(4, navitems.size());
		Assert.assertEquals(2, navs.size());
		Assert.assertEquals(4, navs.get(0).getChildren().size());
		Assert.assertEquals(3, navs.get(1).getChildren().size());
	}
}
