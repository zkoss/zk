/* NotifyChangeOnSetterTest.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 11:52:43 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.viewmodel.notification;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class NotifyChangeOnSetterTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect("/bind/viewmodel/notification/notifychange-onsetter.zul");
		final Label fullname = desktop.query("#fullname").as(Label.class);
		final Label fn = desktop.query("#firstname").as(Label.class);
		final Label ln = desktop.query("#lastname").as(Label.class);
		Assert.assertEquals("John Smith", fullname.getValue());
		Assert.assertEquals("John", fn.getValue());
		Assert.assertEquals("Smith", ln.getValue());

		desktop.query("#fn").type("Tom");
		desktop.query("#ln").type("Riddle");
		Assert.assertEquals("Tom Riddle", fullname.getValue());
		Assert.assertEquals("John", fn.getValue());
		Assert.assertEquals("Riddle", ln.getValue());
	}

	@Test
	public void testSmartCombination() {
		final DesktopAgent desktop = connect("/bind/viewmodel/notification/notifychange-onsetter2.zul");
		final Label fullname = desktop.query("#fullname").as(Label.class);
		final Label fn = desktop.query("#firstname").as(Label.class);
		final Label ln = desktop.query("#lastname").as(Label.class);
		Assert.assertEquals("John Smith", fullname.getValue());
		Assert.assertEquals("John", fn.getValue());
		Assert.assertEquals("Smith", ln.getValue());

		desktop.query("#fn").type("Tom");
		desktop.query("#ln").type("Riddle");
		Assert.assertEquals("Tom Riddle", fullname.getValue());
		Assert.assertEquals("Tom", fn.getValue());
		Assert.assertEquals("Riddle", ln.getValue());
	}
}
