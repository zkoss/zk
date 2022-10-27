/* InitErrorVMNameTest.java
	Purpose:

	Description:

	History:
		Thu May 06 16:43:47 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.databinding.initialization;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

/**
 * @author jameschu
 */
public class InitErrorVMNameTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		assertNotEquals("-1",
			getEval("document.body.innerHTML.indexOf('HTTP ERROR 500 org.zkoss.zk.ui.UiException: java.lang.ClassNotFoundException: InitVM')"));
	}
}
