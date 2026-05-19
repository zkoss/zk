/* F110_ZK_6096Test.java

		Purpose:

		Description:

		History:
				Tue May 19 16:10:10 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F110_ZK_6096Test extends WebDriverTestCase {
	@Test
	public void testStubonlyDescendants() {
		connect("/test2/F110-ZK-6096.zul");
		click(jq("$probe"));
		waitResponse();
		assertEquals("g1div g1lbl g2divStub g3div g3lbl g4lbl g5div g5lbl", jq("$result").text());

		click(jq("$append"));
		waitResponse();
		click(jq("$probe"));
		waitResponse();
		assertEquals("g1div g1lbl g2divStub g3div g3lbl g4lbl g4newLbl g5div g5lbl g5newLbl", jq("$result").text());
	}

	@Test
	public void testPaging() {
		connect("/test2/F110-ZK-6096.zul");
		click(jq("$g6grid").find(".z-paging-next"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("r3", jq("$g6grid").find("@row").eq(0).text().trim());
		click(jq("$g7grid").find(".z-paging-next"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("s3", jq("$g7grid").find("@row").eq(0).text().trim());
	}

	@Test
	public void testShadowHost() {
		connect("/test2/F110-ZK-6096.zul");
		click(jq("$g8add"));
		waitResponse();
		click(jq("$g8remove"));
		waitResponse();
		assertFalse(hasError());
		JQuery textboxes = jq("$g8").find("@textbox");
		assertEquals(2, textboxes.length());
		assertEquals("b", textboxes.eq(0).val());
		assertEquals("c", textboxes.eq(1).val());
	}
}
