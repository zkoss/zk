/* GlobalCommandTest.java

	Purpose:
		
	Description:
		
	History:
		Tue May 04 16:38:59 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.viewmodel.command;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.startsWith;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class GlobalCommandTest extends ZATSTestCase {
	private DesktopAgent desktop;

	@Before
	public void setUp() {
		desktop = connect("/bind/viewmodel/command/global-command.zul");
	}

	@Test
	public void test() {
		desktop.query("#btnClean").click();

		final List<String> zkLog = desktop.getZkLog();
		Assert.assertEquals(2, zkLog.size());
		MatcherAssert.assertThat(zkLog, containsInAnyOrder("GlobalCommandVM clean!", "command1"));
	}

	@Test
	public void testUnknown() {
		desktop.query("#btnUnknown").click();

		final List<String> zkLog = desktop.getZkLog();
		Assert.assertEquals(2, zkLog.size());
		MatcherAssert.assertThat(zkLog, containsInAnyOrder(
				"[GlobalCommandVM] GlobalCommand [unknown] unknown!",
				"[LocalCommandVM] GlobalCommand [unknown] unknown!"
		));
	}

	@Test
	public void testDefaultDuplicated() {
		final DesktopAgent desktop = connect("/bind/viewmodel/command/global-command-duplicated-default.zul");
		Throwable t = Assert.assertThrows(ZatsException.class, () ->
				desktop.query("button").click());
		MatcherAssert.assertThat(t.getMessage(), startsWith("there are more than one DefaultGlobalCommand method in class"));
	}
}
