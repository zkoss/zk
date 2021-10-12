/* B95_ZK_4645Test.java

	Purpose:

	Description:

	History:
		Wed Oct 28 11:10:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4645Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();
		sleep(1000);
		click(jq("$render"));
		sleep(1000);
		assertFalse(hasError());
	}
}