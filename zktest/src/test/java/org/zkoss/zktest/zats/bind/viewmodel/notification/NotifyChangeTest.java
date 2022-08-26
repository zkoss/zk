/* NotifyChangeTest.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 14:22:21 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.viewmodel.notification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class NotifyChangeTest extends ZATSTestCase {
	@Test
	public void testNotifySelf() {
		final DesktopAgent desktop = connect("/bind/viewmodel/notification/notifychange-self.zul");
		desktop.query("#date1").type("2021/05/01");
		desktop.query("#date2").type("2021/05/08");
		Assertions.assertEquals("7", desktop.query("#dur").as(Label.class).getValue());
	}

	@Test
	public void testNotifyAsterisk() {
		final DesktopAgent desktop = connect("/bind/viewmodel/notification/notifychange-asterisk.zul");
		desktop.query("#date1").type("2021/05/01");
		desktop.query("#date2").type("2021/05/08");
		Assertions.assertNotEquals("7", desktop.query("#dur").as(Label.class).getValue());
	}
}
