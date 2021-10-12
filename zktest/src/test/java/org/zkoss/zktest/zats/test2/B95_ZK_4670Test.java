/* B95_ZK_4670Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov 25 12:49:33 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4670Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		click(jq("@button"));
		waitResponse();
		MatcherAssert.assertThat(getZKLog(), not(containsString("true")));
	}
}
