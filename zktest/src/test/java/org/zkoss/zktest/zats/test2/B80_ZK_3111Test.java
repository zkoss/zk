/* B80_ZK_3111Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Jul 21 16:08:05 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B80_ZK_3111Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		
		// initial test
		Assertions.assertEquals("Collapse", jq(".z-panel-expand:eq(0)").attr("title"));
		Assertions.assertEquals("Collapse", jq(".z-panel-expand:eq(1)").attr("title"));
		
		// a11y will be different
		MatcherAssert.assertThat(jq(".z-groupbox-title").attr("title"),
				anyOf(equalTo("Groupbox with title, Collapse"), equalTo("null")));
		JQuery groupboxCaptionTitle = jq(".z-caption .sr-only-focusable");
		if (groupboxCaptionTitle.exists())
			Assertions.assertEquals("Collapse", groupboxCaptionTitle.attr("title"));
		
		Assertions.assertEquals("Maximize", jq(".z-panel-maximize:eq(0)").attr("title"));
		Assertions.assertEquals("Maximize", jq(".z-panel-maximize:eq(1)").attr("title"));
		Assertions.assertEquals("Maximize", jq(".z-window-maximize:eq(0)").attr("title"));
		Assertions.assertEquals("Maximize", jq(".z-window-maximize:eq(1)").attr("title"));
		
		Assertions.assertEquals("Close", jq(".z-panel-close:eq(0)").attr("title"));
		Assertions.assertEquals("Close", jq(".z-panel-close:eq(1)").attr("title"));
		Assertions.assertEquals("Close", jq(".z-window-close:eq(0)").attr("title"));
		Assertions.assertEquals("Close", jq(".z-window-close:eq(1)").attr("title"));
		
		// click test
		click(jq(".z-panel-expand:eq(0)"));
		waitResponse();
		Assertions.assertEquals("Expand", jq(".z-panel-expand:eq(0)").attr("title"));
		
		click(jq(".z-panel-expand:eq(1)"));
		waitResponse();
		Assertions.assertEquals("Expand", jq(".z-panel-expand:eq(1)").attr("title"));
		
		click(jq(".z-groupbox-title"));
		waitResponse();
		MatcherAssert.assertThat(jq(".z-groupbox-title").attr("title"),
				anyOf(equalTo("Groupbox with title, Expand"), equalTo("null")));
		
		if (groupboxCaptionTitle.exists()) {
			click(jq(".z-groupbox .z-caption"));
			waitResponse();
			Assertions.assertEquals("Expand", groupboxCaptionTitle.attr("title"));
		}
		
		click(jq(".z-panel-maximize:eq(0)"));
		waitResponse();
		Assertions.assertEquals("Restore", jq(".z-panel-maximize:eq(0)").attr("title"));
		
		click(jq(".z-panel-maximize:eq(1)"));
		waitResponse();
		Assertions.assertEquals("Restore", jq(".z-panel-maximize:eq(1)").attr("title"));
		
		click(jq(".z-window-maximize:eq(0)"));
		waitResponse();
		Assertions.assertEquals("Restore", jq(".z-window-maximize:eq(0)").attr("title"));
		
		click(jq(".z-window-maximize:eq(1)"));
		waitResponse();
		Assertions.assertEquals("Restore", jq(".z-window-maximize:eq(1)").attr("title"));
		
		// change locale test
		type(jq("@textbox"), "zh");
		waitResponse();
		click(jq("@button"));
		waitResponse();
		assertNoJSError();
		Assertions.assertFalse(hasError());
	}
}
