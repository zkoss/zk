/* F96_ZK_4894Test.java

	Purpose:
		
	Description:
		
	History:
		Thu May 20 14:31:34 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Element;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * @author rudyhuang
 */
public class F96_ZK_4894Test extends WebDriverTestCase {
	@Test
	public void testErrorbox() {
		connect();

		final JQuery textbox = jq("@textbox");
		click(textbox);
		blur(textbox);
		waitResponse();

		final JQuery errorbox = jq("@errorbox");
		final int oldTop = errorbox.offsetTop();

		jq("$content").scrollTop(200);
		waitResponse();
		Assertions.assertNotEquals(oldTop, errorbox.offsetTop());
	}

	@Test
	public void testPopup() {
		connect();

		final Widget combo = widget("@combobox");
		click(combo.$n("btn"));
		waitResponse(true);

		final Element comboPopup = combo.$n("pp");
		final int oldTop = jq(comboPopup).offsetTop();

		jq("$content").scrollTop(200);
		waitResponse();
		Assertions.assertNotEquals(oldTop, jq(comboPopup).offsetTop());
	}
}
