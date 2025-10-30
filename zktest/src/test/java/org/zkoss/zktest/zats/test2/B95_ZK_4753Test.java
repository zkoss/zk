/* B95_ZK_4753Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Dec 23 09:57:57 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
@Tag("WcagTestOnly")
public class B95_ZK_4753Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("$btnF1"));
		waitResponse();
		Assertions.assertEquals(
				widget(jq("$tb")).uuid(),
				getEval("document.activeElement.id"));

		click(jq("$btnF2"));
		waitResponse();
		Assertions.assertEquals(
				widget(jq("$ni1")).$n("a").get("id"),
				getEval("document.activeElement.id"));

		click(jq("$btnF3"));
		waitResponse();
		Assertions.assertEquals(
				widget(jq("$ni2")).$n("cnt").get("id"),
				getEval("document.activeElement.id"));

		click(jq("$btnF4"));
		waitResponse();
		Assertions.assertEquals(
				widget(jq("$ni1")).$n("a").get("id"),
				getEval("document.activeElement.id"));

		click(jq("$btnF5"));
		waitResponse();
		Assertions.assertEquals(
				widget(jq("$btn")).uuid(),
				getEval("document.activeElement.id"));
	}
}
