/* F95_ZK_4418Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct 19 18:55:14 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.equalToIgnoringCase;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F95_ZK_4418Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("$forTest"));
		waitResponse();

		click(jq("@button:eq(0)"));
		waitResponse();
		MatcherAssert.assertThat(getZKLog(), equalToIgnoringCase("text/plain;charset=ISO-8859-15"));

		closeZKLog();
		click(jq("@button:eq(1)"));
		waitResponse();
		MatcherAssert.assertThat(getZKLog(), equalToIgnoringCase("image/jpeg"));
	}
}
