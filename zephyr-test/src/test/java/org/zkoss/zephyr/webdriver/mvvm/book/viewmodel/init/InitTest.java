/* InitTest.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 14:19:49 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.viewmodel.init;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.opentest4j.AssertionFailedError;
import org.zkoss.zephyr.webdriver.TestStage;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author rudyhuang
 */
public class InitTest extends ZephyrClientMVVMTestCase {
	@Test
	public void testInit() {
		connect("/mvvm/book/viewmodel/init/init.zul");

		click(jq("button"));
		waitResponse();
		assertEquals("InitVM.init was called", getZKLog());
	}

	@Test
	public void testInitChildOverrideSuper() {
		connect("/mvvm/book/viewmodel/init/init-child-override-super.zul");

		click(jq("button"));
		waitResponse();
		assertEquals("ChildInitOverrideVM.init was called twice\nChildInitOverrideVM.init was called twice",
				getZKLog());
	}

	@Test
	public void testInitChildWithoutSuper() {
		connect("/mvvm/book/viewmodel/init/init-child-without-super.zul");

		click(jq("button"));
		waitResponse();
		assertEquals("ChildInitNoSuperVM.childInit was called", getZKLog());
	}

	@Test
	public void testInitChildWithSuper() {
		connect("/mvvm/book/viewmodel/init/init-child-with-super.zul");

		click(jq("button"));
		waitResponse();
		assertEquals("InitVM.init was called\n" + "ChildInitSuperVM.childInit was called", getZKLog());
	}

	@Test
	public void testInitChildWithSuperClass() {
		connect("/mvvm/book/viewmodel/init/init-child-with-super-class.zul");

		click(jq("button"));
		waitResponse();
		assertEquals("InitVM.init was called", getZKLog());
	}

	@Test
	public void testInitChildWithSuperNotExist() {
		connect("/mvvm/book/viewmodel/init/init-child-with-super-notexist.zul");

		click(jq("button"));
		waitResponse();
		assertEquals("ChildInitSuperNotExistVM.childInit was called", getZKLog());
	}

	@Test
	public void testMultipleInit() {
		Throwable t = assertThrows(AssertionFailedError.class, () -> connect("/mvvm/book/viewmodel/init/multiple-init.zul"));
		MatcherAssert.assertThat(t.getMessage(), startsWith("Error Code: 500"));
	}
}
