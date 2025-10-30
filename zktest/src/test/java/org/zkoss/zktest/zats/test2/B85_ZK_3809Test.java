/* B85_ZK_3809Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan 17 11:16:44 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B85_ZK_3809Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();

		assertThat(getZKLog(), CoreMatchers.endsWith("/test%23!%3f%24(%3c_%3e%2f%22')*+%2c%3a%3b%3d@%5b%5d.txt"));
	}
}
