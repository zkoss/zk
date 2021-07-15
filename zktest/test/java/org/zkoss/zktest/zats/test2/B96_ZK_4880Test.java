/* B96_ZK_4880Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 15 10:46:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_4880Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		final long start = System.currentTimeMillis();
		click(jq("@button"));
		waitResponse();
		final long actualMillis = System.currentTimeMillis() - start;
		MatcherAssert.assertThat(actualMillis, lessThanOrEqualTo(SECONDS.toMillis(3)));
	}

}
