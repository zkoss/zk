/* B50_2918527Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 20 12:42:30 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.greaterThan;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B50_2918527Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		int left = jq("@textbox").offsetLeft();

		click(jq("@button"));
		waitResponse();

		int leftAfter = jq("@textbox").offsetLeft();

		Assert.assertThat("should move to center", leftAfter, greaterThan(left));
	}
}
