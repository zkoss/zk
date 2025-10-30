/* B50_ZK_867Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 16 14:23:21 CST 2020, Created by jameschu

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class B50_ZK_867Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery product0 = jq(".z-grid:eq(0) .z-row:contains(Kyocera)");
		click(product0.find("@detail").toWidget().$n("icon"));
		waitResponse();
		JQuery product1 = jq(".z-grid:eq(1) .z-row:contains(Kyocera)");
		click(product1.find("@detail").toWidget().$n("icon"));
		waitResponse();
		JQuery detail0 = product0.next();
		JQuery detail1 = product1.next();
		assertNotEquals("the style of the content of second detail should be different with first detail", detail0.css("background-color"), detail1.css("background-color"));
	}
}
