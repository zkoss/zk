/* B90_ZK_4442Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Dec 04 17:42:24 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B90_ZK_4442Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").eq(0));
		waitResponse();
		// testing the text color and header-hover background-color contrast radio >= 1.5
		assertThat(Double.valueOf(getZKLog()), greaterThanOrEqualTo(1.5));
		closeZKLog();
		click(jq("@button").eq(1));
		waitResponse();
		assertThat(Double.valueOf(getZKLog()), greaterThanOrEqualTo(1.5));
	}
}
