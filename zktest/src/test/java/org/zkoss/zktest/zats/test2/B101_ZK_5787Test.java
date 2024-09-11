/* B101_ZK_5787Test.java

	Purpose:

	Description:

	History:
		5:18 PM 2024/9/11, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
@Tag("WcagTestOnly")
public class B101_ZK_5787Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertNoJSError();
		click(jq(".z-listcell:eq(0)"));
		waitResponse();
		assertNoJSError();
	}
}
