/* B96_ZK_5111Test.java

	Purpose:
		
	Description:
		
	History:
		5:41 PM 2022/10/3, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5111Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertNoJSError();
	}
}
