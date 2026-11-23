/* F90_ZK_4334Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct 28 15:27:18 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F90_ZK_4334Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		sleep(300);
		waitResponse();

		Assert.assertThat(jq("$tz").text(), containsString("GMT"));
		Assert.assertThat(jq("$zid").text(), not(containsString("GMT")));
	}
}
