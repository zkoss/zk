/* B96_ZK_5009Test.java

	Purpose:
		
	Description:
		
	History:
		4:29 PM 2021/11/16, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5009Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		assertNoJSError();
		click(jq("@button"));
		waitResponse();
		assertNoJSError();
	}
}
