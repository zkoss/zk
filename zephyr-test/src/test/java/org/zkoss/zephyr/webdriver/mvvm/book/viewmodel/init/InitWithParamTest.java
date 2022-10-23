/* InitWithParamTest.java

	Purpose:
		
	Description:
		
	History:
		Mon May 03 15:18:04 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.viewmodel.init;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.zephyr.webdriver.TestStage;

/**
 * @author rudyhuang
 */
public class InitWithParamTest extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect("/mvvm/book/viewmodel/init/init-with-param.zul");
		click(jq("button"));
		waitResponse();
		assertEquals("test test2", getZKLog().split("\n")[0]);
	}
}
