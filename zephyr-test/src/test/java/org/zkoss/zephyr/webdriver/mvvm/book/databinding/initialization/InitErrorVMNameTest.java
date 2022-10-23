/* InitErrorVMNameTest.java
	Purpose:

	Description:

	History:
		Thu May 06 16:43:47 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.initialization;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.zephyr.webdriver.TestStage;

/**
 * @author jameschu
 */
public class InitErrorVMNameTest extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		assertNotEquals("-1",
			getEval("document.body.innerHTML.indexOf('HTTP ERROR 500 org.zkoss.zk.ui.UiException: java.lang.ClassNotFoundException: InitVM')"));
	}
}
