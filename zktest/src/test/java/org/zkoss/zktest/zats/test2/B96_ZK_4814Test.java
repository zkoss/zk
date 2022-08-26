/* B96_ZK_4814Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 09 12:50:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */

@Tag("WcagTestOnly")
public class B96_ZK_4814Test extends WebDriverTestCase {

	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		JQuery jqSplitter = jq(".z-splitter");
		String valuenow = jqSplitter.attr("aria-valuenow");
		dragdropTo(jqSplitter, 3, 0, 20, 0);
		waitResponse();
		Assertions.assertNotEquals(jqSplitter.attr("aria-valuenow"), valuenow);
	}
}
