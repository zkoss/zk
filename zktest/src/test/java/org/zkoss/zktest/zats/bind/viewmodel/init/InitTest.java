/* InitTest.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 14:19:49 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.viewmodel.init;

import static org.hamcrest.Matchers.startsWith;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.ZatsException;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class InitTest extends ZATSTestCase {
	@Test
	public void testInit() {
		final DesktopAgent desktop = connect("/bind/viewmodel/init/init.zul");

		desktop.query("button").click();
		final List<String> zkLog = desktop.getZkLog();
		Assertions.assertEquals("InitVM.init was called\n", zkLog.get(0));
	}

	@Test
	public void testInitChildOverrideSuper() {
		final DesktopAgent desktop = connect("/bind/viewmodel/init/init-child-override-super.zul");

		desktop.query("button").click();
		final List<String> zkLog = desktop.getZkLog();
		Assertions.assertEquals("ChildInitOverrideVM.init was called twice\n" +
				"ChildInitOverrideVM.init was called twice\n", zkLog.get(0));
	}

	@Test
	public void testInitChildWithoutSuper() {
		final DesktopAgent desktop = connect("/bind/viewmodel/init/init-child-without-super.zul");

		desktop.query("button").click();
		final List<String> zkLog = desktop.getZkLog();
		Assertions.assertEquals("ChildInitNoSuperVM.childInit was called\n", zkLog.get(0));
	}

	@Test
	public void testInitChildWithSuper() {
		final DesktopAgent desktop = connect("/bind/viewmodel/init/init-child-with-super.zul");

		desktop.query("button").click();
		final List<String> zkLog = desktop.getZkLog();
		Assertions.assertEquals("InitVM.init was called\n" +
				"ChildInitSuperVM.childInit was called\n", zkLog.get(0));
	}

	@Test
	public void testInitChildWithSuperClass() {
		final DesktopAgent desktop = connect("/bind/viewmodel/init/init-child-with-super-class.zul");

		desktop.query("button").click();
		final List<String> zkLog = desktop.getZkLog();
		Assertions.assertEquals("InitVM.init was called\n", zkLog.get(0));
	}

	@Test
	public void testInitChildWithSuperNotExist() {
		final DesktopAgent desktop = connect("/bind/viewmodel/init/init-child-with-super-notexist.zul");

		desktop.query("button").click();
		final List<String> zkLog = desktop.getZkLog();
		Assertions.assertEquals("ChildInitSuperNotExistVM.childInit was called\n", zkLog.get(0));
	}

	@Test
	public void testMultipleInit() {
		Throwable t = Assertions.assertThrows(ZatsException.class, () ->
				connect("/bind/viewmodel/init/multiple-init.zul"));
		MatcherAssert.assertThat(t.getMessage(), startsWith("more than one [@Init] in the class"));
	}
}
