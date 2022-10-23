/* B80_ZK_3104Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 28 16:53:28 CST 2015, Created by Jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;

/**
 * @author jameschu
 */
public class B80_ZK_3104_1Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() throws IOException {
		connect();
		JQuery btn = jq("@button");
		click(btn);
		waitResponse();
		assertTrue(hasError());
	}
}
