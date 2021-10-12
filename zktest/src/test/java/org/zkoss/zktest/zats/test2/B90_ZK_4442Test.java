/* B90_ZK_4442Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Dec 04 17:42:24 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class B90_ZK_4442Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button").eq(0));
		waitResponse();
		// testing the text color and header-hover background-color contrast radio >= 1.5
		Assert.assertThat(Double.valueOf(getZKLog()), greaterThanOrEqualTo(1.5));
		closeZKLog();
		click(jq("@button").eq(1));
		waitResponse();
		Assert.assertThat(Double.valueOf(getZKLog()), greaterThanOrEqualTo(1.5));
	}
}
