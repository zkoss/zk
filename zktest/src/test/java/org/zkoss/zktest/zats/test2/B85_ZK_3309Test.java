/* B85_ZK_3309Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 27 17:06:12 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class B85_ZK_3309Test extends WebDriverTestCase {
	@Test
	public void test() throws Exception {
		connect();

		final String inputText = "Hello";
		JQuery cbox = jq("@combobox");
		Element inp = cbox.toWidget().$n("real");
		inp.set("value", inputText);
		click(cbox.toWidget().$n("btn"));
		waitResponse();

		Assertions.assertEquals(inputText, inp.get("value"));
	}
}
