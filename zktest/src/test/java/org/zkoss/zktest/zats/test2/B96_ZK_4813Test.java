/* B96_ZK_4813Test.java

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
public class B96_ZK_4813Test extends WebDriverTestCase {

	@Test
	public void test() throws Exception {
		connect();
		waitResponse();
		JQuery jqSplitter = jq(".z-splitter");
		click(jqSplitter);
		waitResponse();
		Assertions.assertEquals("0", jqSplitter.attr("aria-valuenow"));
		jqSplitter = jq(".z-splitlayout-splitter");
		click(jqSplitter);
		waitResponse();
		Assertions.assertEquals("0", jqSplitter.attr("aria-valuenow"));
	}
}
