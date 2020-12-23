/* B95_ZK_4753Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Dec 23 09:57:57 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4753Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		if (!Boolean.parseBoolean(getEval("!!window.za11y"))) return;

		click(jq("$btnF1"));
		waitResponse();
		Assert.assertEquals(
				widget(jq("$tb")).uuid(),
				getEval("document.activeElement.id"));

		click(jq("$btnF2"));
		waitResponse();
		Assert.assertEquals(
				widget(jq("$ni1")).$n("a").get("id"),
				getEval("document.activeElement.id"));

		click(jq("$btnF3"));
		waitResponse();
		Assert.assertEquals(
				widget(jq("$ni2")).$n("cnt").get("id"),
				getEval("document.activeElement.id"));

		click(jq("$btnF4"));
		waitResponse();
		Assert.assertEquals(
				widget(jq("$ni1")).$n("a").get("id"),
				getEval("document.activeElement.id"));

		click(jq("$btnF5"));
		waitResponse();
		Assert.assertEquals(
				widget(jq("$btn")).uuid(),
				getEval("document.activeElement.id"));
	}
}
