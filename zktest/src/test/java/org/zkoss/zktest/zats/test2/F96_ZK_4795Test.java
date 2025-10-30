/* F96_ZK_4795Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 16 10:50:45 CST 2021, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class F96_ZK_4795Test extends WebDriverTestCase {
	@Test
	public void testScroll() {
		connect();
		waitResponse();
		//test grid sticky
		eval("window.scrollTo(0, " + jq("@row").last().offsetTop() + ")");
		waitResponse();
		assertEquals(parseInt(getEval("window.scrollY")), jq(".z-grid-header").offsetTop());

		//test listbox sticky
		eval("window.scrollTo(0, " + jq("@listitem").last().offsetTop() + ")");
		waitResponse();
		assertEquals(parseInt(getEval("window.scrollY")), jq(".z-listbox-header").offsetTop());

		//test tree sticky
		eval("window.scrollTo(0, document.body.scrollHeight)");
		waitResponse();
		assertEquals(parseInt(getEval("window.scrollY")), jq(".z-tree-header").offsetTop());
	}
}
