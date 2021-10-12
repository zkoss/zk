/* B95_ZK_4614Test.java

	Purpose:
		
	Description:
		
	History:
		Wed, Aug 19, 2020 03:42:38 PM, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B95_ZK_4614Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button:eq(0)"));
		waitResponse();
		assertNoJSError();

		click(jq("@button:eq(1)"));
		waitResponse();
		assertNoJSError();
	}
}
