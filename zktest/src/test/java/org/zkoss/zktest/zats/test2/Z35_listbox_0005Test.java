/* Z35_listbox_0005Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jun 19 15:21:24 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class Z35_listbox_0005Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		JQuery listbody = jq(".z-listbox-body");
		listbody.scrollTop(listbody.scrollHeight());
		waitResponse();
		listbody.scrollTop(0);
		waitResponse();
		Assertions.assertEquals(51, jq(".z-listitem").length());
		for (int i = 0; i < jq("@listitem").length(); i++) {
			Assertions.assertTrue(jq("@listitem").eq(i).text().contains(String.valueOf(i)));
		}
	}
}
