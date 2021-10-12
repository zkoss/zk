/* BasicTest.java

	Purpose:
		
	Description:
		
	History:
		Tue May 04 18:41:36 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.viewmodel.notification;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;

/**
 * @author rudyhuang
 */
public class BasicTest extends ZATSTestCase {
	@Test
	public void testBasic() {
		doTest(connect("/bind/viewmodel/notification/basic.zul"));
	}

	@Test
	public void testAuto() {
		doTest(connect("/bind/viewmodel/notification/basic-auto.zul"));
	}

	@Test
	public void testSmart() {
		doTest(connect("/bind/viewmodel/notification/basic-smart.zul"));
	}

	private void doTest(DesktopAgent desktop) {
		final Intbox id = desktop.query("#id").as(Intbox.class);
		final Textbox name = desktop.query("#name").as(Textbox.class);
		final Textbox city = desktop.query("#city").as(Textbox.class);
		final Integer oldId = id.getValue();
		final String oldName = name.getValue();
		final String oldCity = city.getValue();
		Assert.assertNotEquals(null, oldId);
		Assert.assertNotEquals("", oldName);
		Assert.assertNotEquals("", oldCity);

		desktop.query("#rnd").click();
		Assert.assertNotEquals(oldId, id.getValue());
		Assert.assertNotEquals(oldName, name.getValue());
		Assert.assertNotEquals(oldCity, city.getValue());

		desktop.query("#reset").click();
		Assert.assertEquals("", name.getValue());
		Assert.assertEquals("", city.getValue());
	}
}
