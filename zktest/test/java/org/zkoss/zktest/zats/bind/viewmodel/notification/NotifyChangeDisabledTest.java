/* NotifyChangeDisabledTest.java

	Purpose:
		
	Description:
		
	History:
		Wed May 05 17:06:42 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.viewmodel.notification;

import static org.hamcrest.Matchers.is;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.zkoss.zats.mimic.AgentException;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class NotifyChangeDisabledTest extends ZATSTestCase {
	private DesktopAgent desktop;

	@Before
	public void setUp() {
		desktop = connect("/bind/viewmodel/notification/notifychange-disabled.zul");
	}

	@Test
	public void testNormalUsage() {
		desktop.query("#inp0").type("ZK");
		Assert.assertEquals("ZK", desktop.query("#val0").as(Label.class).getValue());
		desktop.query("#inp1").type("ZK");
		Assert.assertNotEquals("ZK", desktop.query("#val1").as(Label.class).getValue());
	}

	@Test
	public void testIllegalUsage1() {
		Throwable t = Assert.assertThrows(AgentException.class, () -> {
			desktop.query("#inp2").type("ZK");
		});
		MatcherAssert.assertThat(t.getCause().getMessage(), is("don't use interface" +
				" org.zkoss.bind.annotation.NotifyChange with interface" +
				" org.zkoss.bind.annotation.NotifyChangeDisabled, choose only one"));
	}

	@Test
	public void testIllegalUsage2() {
		Throwable t = Assert.assertThrows(AgentException.class, () -> {
			desktop.query("#inp3").type("ZK");
		});
		MatcherAssert.assertThat(t.getCause().getMessage(), is("don't use interface" +
				" org.zkoss.bind.annotation.SmartNotifyChange with interface" +
				" org.zkoss.bind.annotation.NotifyChangeDisabled, choose only one"));
	}
}
