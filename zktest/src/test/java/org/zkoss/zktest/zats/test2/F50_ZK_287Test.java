/* F50_ZK_287Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 12 15:57:01 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F50_ZK_287Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button:eq(0)"));
		click(jq("@button:eq(1)"));
		sleep(3000);
		waitResponse();

		assertThat(jq("$lb").text(), startsWith("A done: "));
		assertThat(jq("$lb2").text(), startsWith("B done: "));
	}
}
