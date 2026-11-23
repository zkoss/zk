/* B96_ZK_5148Test.java

	Purpose:
		
	Description:
		
	History:
		11:35 AM 2022/10/4, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5148Test extends WebDriverTestCase {
	@Test
	public void testNoJSError() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertNoJSError();
	}
}
