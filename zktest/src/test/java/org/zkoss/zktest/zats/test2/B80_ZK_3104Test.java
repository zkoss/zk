/* B80_ZK_3104Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 28 16:53:28 CST 2015, Created by Jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B80_ZK_3104Test extends WebDriverTestCase {

	@Test
	public void test() throws IOException {
		connect();
		JQuery btns = jq("@button");
		for (int i = 0; i < 6; i++) {
			click(btns.eq(i));
			waitResponse();
			assertEquals("true", getZKLog());
			closeZKLog();
			waitResponse();
		}
	}
}
