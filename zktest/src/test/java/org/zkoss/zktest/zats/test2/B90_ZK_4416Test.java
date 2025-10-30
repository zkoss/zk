/* B90_ZK_4416Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 19 19:10:28 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B90_ZK_4416Test extends WebDriverTestCase {
	@Test
	public void tets() {
		connect();

		click(jq("@button:eq(0)"));
		waitResponse();
		sleep(1000); // Wait for pdf.js
		Assertions.assertNotEquals("", jq(widget("@pdfviewer").$n("toolbar-page-total")).text());

		click(jq("@button:eq(1)"));
		waitResponse();
		sleep(1000); // Wait for pdf.js
		assertThat(jq(".z-error .messages").text(), not(containsString("~./")));
	}
}
