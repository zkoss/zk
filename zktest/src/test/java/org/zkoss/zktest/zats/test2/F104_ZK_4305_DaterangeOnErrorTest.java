/* F104_ZK_4305_DaterangeOnErrorTest.java

		Purpose:

		Description:

		History:
				Wed Jun  3 14:43:46 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_4305_DaterangeOnErrorTest extends WebDriverTestCase {

	@Test
	public void test() {
		connect("/test2/F104-ZK-4305-onerror.zul");
		waitResponse();
		// Put an unparseable value into the begin input and fire the change event
		// the widget binds (change.daterangebox). The client cannot parse it, so
		// it fires onError, which the server now delivers as a typed ErrorEvent.
		((JavascriptExecutor) driver).executeScript(
				"var input = jq('$dr .z-daterangebox-begin')[0];"
				+ "input.value = 'not-a-date';"
				+ "jq(input).trigger('change');");
		waitResponse();

		// The onError listener wrote the event type, message and value into cap.
		String cap = jq("$cap").text();
		// The event must arrive as a typed ErrorEvent, not a plain Event.
		assertTrue(cap.startsWith("type=ErrorEvent;"),
				"onError must arrive as ErrorEvent, was: " + cap);
		// value carries the rejected text the user typed (locale-independent).
		assertTrue(cap.endsWith(";val=not-a-date"),
				"ErrorEvent.getValue() must carry the rejected text, was: " + cap);
		// getMessage() must be populated (the localized RANGE_INVALID reason);
		// the exact string is locale-dependent, so only assert it is non-empty.
		assertFalse(cap.contains("msg=;") || cap.contains("msg=null"),
				"ErrorEvent.getMessage() must carry a reason, was: " + cap);
	}
}
