package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class B96_ZK_5180Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();

		final Widget bandbox = jq("@bandbox").toWidget();
		// 1. Click the magnifying glass icon. A popup should appear.
		click(bandbox.$n("btn"));
		waitResponse();
		assertOpen(bandbox);

		final Widget datebox = jq("@datebox").toWidget();
		// 2. Click the calendar icon. Another popup containing a calendar should appear.
		click(datebox.$n("btn"));
		waitResponse();
		assertOpen(bandbox);
		assertOpen(datebox);

		final Widget calendar = jq("@calendar").toWidget();
		// 3. Click the first day of the first week of the current calendar page. The calendar shall disappear while the first popup (bandpopup) stays open.
		click(calendar.$n("w0-p0"));
		waitResponse();
		assertOpen(bandbox);
		assertClosed(datebox);

		// 4. Click the calendar icon. The calendar should reappear.
		click(datebox.$n("btn"));
		waitResponse();
		assertOpen(bandbox);
		assertOpen(datebox);

		// 5. Click the "previous month" button. The calendar should stay open.
		click(calendar.$n("left"));
		waitResponse();
		assertOpen(bandbox);
		assertOpen(datebox);

		// 6. Click the calendar "title" ("month year"). The calendar should stay open.
		click(calendar.$n("title"));
		waitResponse();
		assertOpen(bandbox);
		assertOpen(datebox);

		// 7. Click the smallest year displayed. The calendar should stay open.
		click(calendar.$n("y0"));
		waitResponse();
		assertOpen(bandbox);
		assertOpen(datebox);

		// 8. Click "Jan" (January). The calendar should stay open.
		click(calendar.$n("m0"));
		waitResponse();
		assertOpen(bandbox);
		assertOpen(datebox);

		// 9. Click the first day of the first week of the current calendar page. The calendar shall disappear while the first popup (bandpopup) stays open.
		click(calendar.$n("w0-p0"));
		waitResponse();
		assertOpen(bandbox);
		assertClosed(datebox);
	}

	void assertOpen(Widget widget) {
		assertTrue(widget.is("open"));
	}

	void assertClosed(Widget widget) {
		assertFalse(widget.is("open"));
	}
}
