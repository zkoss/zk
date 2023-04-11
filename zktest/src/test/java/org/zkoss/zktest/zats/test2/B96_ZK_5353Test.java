/* B96_ZK_5353Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 07 16:52:12 CST 2023, Created by jameschu

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */

public class B96_ZK_5353Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		type(jq("@textbox"), "1");
		waitResponse();
		assertFalse(hasError());
	}

	@Test
	public void test1() throws Exception {
		connect("/test2/B96-ZK-5353-1.zul");
		waitResponse();
		JQuery jqBtn = jq("@button");
		click(jqBtn.eq(0));
		click(jqBtn.eq(1));
		waitResponse();
		assertFalse(hasError());
	}
}
