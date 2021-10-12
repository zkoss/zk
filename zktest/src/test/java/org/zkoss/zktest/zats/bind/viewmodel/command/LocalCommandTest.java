/* LocalCommandTest.java

	Purpose:
		
	Description:
		
	History:
		Tue May 04 16:03:52 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.viewmodel.command;

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
public class LocalCommandTest extends ZATSTestCase {
	@Test
	public void test() {
		final DesktopAgent desktop = connect("/bind/viewmodel/command/local-command.zul");

		desktop.queryAll("button").forEach(ComponentAgent::click);
		final List<String> zkLog = desktop.getZkLog();
		Assert.assertEquals(6, zkLog.size());
		Assert.assertEquals("command1", zkLog.get(0));
		Assert.assertEquals("commandTwo", zkLog.get(1));
		Assert.assertEquals("command3And4", zkLog.get(2));
		Assert.assertEquals("command3And4", zkLog.get(3));
		MatcherAssert.assertThat(zkLog.get(4), startsWith("command5: [MouseEvent onClick <Button"));
		Assert.assertEquals("Command [command999] unknown!", zkLog.get(5));
	}

	@Test
	public void testDuplicated() {
		final DesktopAgent desktop = connect("/bind/viewmodel/command/local-command-duplicated.zul");
		Throwable t = Assert.assertThrows(ZatsException.class, () ->
				desktop.query("button").click());
		MatcherAssert.assertThat(t.getMessage(), startsWith("there are more than one Command method command1 in class"));
	}

	@Test
	public void testDefaultDuplicated() {
		final DesktopAgent desktop = connect("/bind/viewmodel/command/local-command-duplicated-default.zul");
		Throwable t = Assert.assertThrows(ZatsException.class, () ->
				desktop.query("button").click());
		MatcherAssert.assertThat(t.getMessage(), startsWith("there are more than one DefaultCommand method in class"));
	}
}
