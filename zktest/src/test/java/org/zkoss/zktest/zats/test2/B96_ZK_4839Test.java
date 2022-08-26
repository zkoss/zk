/* B96_ZK_4839Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Mar 26 15:21:38 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

@Tag("WcagTestOnly")
public class B96_ZK_4839Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		mouseOver(jq(".z-icon-car"));
		waitResponse();
		assertNoJSError();
	}
}
