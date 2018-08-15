/* F86_ZK_4028_2Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 22 12:46:13 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.CoreMatchers.endsWith;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F86_ZK_4028_2Test extends WebDriverTestCase {
	@Test
	public void test() {
		// Test the History state support
		connect(getTestURL("F86-ZK-4028.zul"));

		click(jq("@button:contains(Switch History Support)"));
		waitResponse();

		click(jq("@button:contains(Next)"));
		waitResponse();
		click(jq("@button:contains(Next)"));
		waitResponse();
		Assert.assertThat(getWebDriver().getCurrentUrl(), endsWith("#personal%20details"));

		getWebDriver().navigate().back();
		waitResponse();
		Assert.assertThat(getZKLog(), endsWith("#accommodation: {\"item\":\"Accommodation\",\"index\":1}"));

		click(jq("@button:contains(Switch History Support)"));
		waitResponse();
		click(jq("@button:contains(Next)"));
		waitResponse();
		Assert.assertThat("The hash shouldn't be changed", getWebDriver().getCurrentUrl(), endsWith("#accommodation"));
	}
}
