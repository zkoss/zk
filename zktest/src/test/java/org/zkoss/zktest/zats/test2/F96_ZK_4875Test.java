/* F96_ZK_4875Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Mar 21 18:50:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F96_ZK_4875Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq("$btn"));
		waitResponse();
		sleep(8000);
		assertTrue(getZKLog().contains("3"));
	}
}
