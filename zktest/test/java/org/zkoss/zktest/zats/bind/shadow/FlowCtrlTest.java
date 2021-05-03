/* FlowCtrlTest.java

	Purpose:
		
	Description:
		
	History:
		Thu May 06 18:13:58 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.shadow;

import static org.hamcrest.Matchers.startsWith;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class FlowCtrlTest extends ZATSTestCase {
	@Test
	public void testChoose() {
		final DesktopAgent desktop = connect("/bind/shadow/flowctrl-choose.zul");
		final List<ComponentAgent> navitems = desktop.queryAll("#navbar > navitem");
		final List<ComponentAgent> navs = desktop.queryAll("#navbar > nav");
		Assert.assertEquals(4, navitems.size());
		Assert.assertEquals(2, navs.size());
	}

	@Test
	public void testIf() {
		final DesktopAgent desktop = connect("/bind/shadow/flowctrl-if.zul");
		final List<ComponentAgent> navitems = desktop.queryAll("#navbar > navitem");
		final List<ComponentAgent> navs = desktop.queryAll("#navbar > nav");
		Assert.assertEquals(4, navitems.size());
		Assert.assertEquals(2, navs.size());
	}

	@Test
	public void testOtherwiseWrongUsage() {
		Throwable t = Assert.assertThrows(ZatsException.class, () -> {
			connect("/bind/shadow/flowctrl-otherwise-wrong.zul");
		});
		MatcherAssert.assertThat(t.getMessage(), startsWith("Unsupported parent for otherwise"));
	}

	@Test
	public void testWhenWrongUsage() {
		Throwable t = Assert.assertThrows(ZatsException.class, () -> {
			connect("/bind/shadow/flowctrl-when-wrong.zul");
		});
		// FIXME ZK-4896 MatcherAssert.assertThat(t.getMessage(), startsWith("Unsupported parent for when"));
	}
}
