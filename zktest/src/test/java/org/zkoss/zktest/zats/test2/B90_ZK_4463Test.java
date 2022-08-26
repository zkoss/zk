/* B90_ZK_4463Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Dec 19 17:46:06 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B90_ZK_4463Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery jqMark = jq(".z-multislider-mark").eq(2).find(".z-multislider-mark-label");
		click(jqMark);
		waitResponse();
		Assertions.assertTrue(isZKLogAvailable());
		closeZKLog();
		click(jqMark);
		waitResponse();
		Assertions.assertFalse(isZKLogAvailable());
	}
}
