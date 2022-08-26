/* F96_ZK_4866Test.java

	Purpose:
		
	Description:
		
	History:
		Fri May 28 10:46:50 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class F96_ZK_4866Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$btn1"));
		waitResponse();
		assertEquals(widget(jq("$headline")).uuid(), getEval("document.activeElement.id"));
	}
}
