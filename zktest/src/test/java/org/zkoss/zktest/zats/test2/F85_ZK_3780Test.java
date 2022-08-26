/* F85_ZK_3780.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 24 17:48:05 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F85_ZK_3780Test extends WebDriverTestCase {
	@Test
	public void testNoParsingError() throws Exception {
		connect();
		JQuery errMsg = jq(".z-error .messages");
		boolean hasError = errMsg.exists();
		assertFalse(hasError && errMsg.text().contains("Lexical error"),
				"Might be a parsing error.");
	}
}
