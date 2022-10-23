/* F70_ZK_2483Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 16:59:25 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesRegex;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ZephyrClientMVVMTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F70_ZK_2483Test extends ZephyrClientMVVMTestCase {
	@Test
	public void test() {
		connect();
		sleep(2000);
		JQuery lbls = jq("$init > @label");
		int index = 0;
		for (JQuery lbl : lbls) {
			if (index % 2 == 0) {
				assertThat(lbl.text(), matchesRegex("value \\d+"));
			} else {
				assertThat(lbl.text(), matchesRegex("key \\d+"));
			}
			index++;
		}
	}
}
