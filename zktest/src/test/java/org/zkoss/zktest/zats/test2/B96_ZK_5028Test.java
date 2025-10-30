/* B96_ZK_5028Test.java

	Purpose:
		
	Description:
		
	History:
		3:09 PM 2021/11/12, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5028Test  extends WebDriverTestCase {
	@Test
	public void testTree() {
		connect();
		assertNoJSError();
		click(jq("@button:eq(0)"));
		waitResponse();
		assertNoJSError();
	}

	@Test
	public void testListbox() {
		connect();
		assertNoJSError();
		click(jq("@button:eq(1)"));
		waitResponse();
		assertNoJSError();
	}

	@Test
	public void testGrid() {
		connect();
		assertNoJSError();
		click(jq("@button:eq(2)"));
		waitResponse();
		assertNoJSError();
	}

	@Test
	public void testRepeated() {
		connect();
		assertNoJSError();
		for (int i = 0; i < 10; i++) {
			click(jq("@button:eq(0)"));
			click(jq("@button:eq(1)"));
			click(jq("@button:eq(2)"));
			waitResponse();
			assertNoJSError();
		}
	}

}