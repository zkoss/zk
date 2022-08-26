/* F90_ZK_4414Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Jan 15 17:45:48 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class F90_ZK_4414Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		sleep(2000); // ensure pdf is loaded

		String pageBefore = widget("@pdfviewer").$n("toolbar-page-active").get("value");

		jq(widget("@pdfviewer").$n("container")).scrollTop(10000);
		eval(jq("@pdfviewer") + ".trigger('mousewheel', [-10])");
		waitResponse();

		String page = widget("@pdfviewer").$n("toolbar-page-active").get("value");
		Assertions.assertNotEquals("Scrolling down doesn't change page number.", pageBefore, page);
	}
}
