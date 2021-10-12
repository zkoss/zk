/* GridTest.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 27 12:09:39 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.comp;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.operation.OpenAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class GridTest extends ZATSTestCase {
	@Test
	public void testDetail() {
		final DesktopAgent desktop = connect();
		final OpenAgent detail = desktop.query("detail").as(OpenAgent.class);
		final Label detailOpen = desktop.query("#detailOpen").as(Label.class);
		detail.open(true);
		Assert.assertEquals("true", detailOpen.getValue());
		detail.open(false);
		Assert.assertEquals("false", detailOpen.getValue());
	}

	@Test
	public void testGroup() {
		final DesktopAgent desktop = connect();
		final OpenAgent group = desktop.query("group").as(OpenAgent.class);
		final Label groupOpen = desktop.query("#groupOpen").as(Label.class);
		group.open(true);
		Assert.assertEquals("true", groupOpen.getValue());
		group.open(false);
		Assert.assertEquals("false", groupOpen.getValue());
	}
}
