/* F110_ZK_4305_DaterangeAnnounceTest.java

		Purpose:

		Description:

		History:
				Thu Sep  3 12:15:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * The polite live region must announce a range even when that range is the one
 * it already holds. Assistive tech compares a live region's new content with
 * what it last read and stays silent when the two match, so a plain overwrite
 * is spoken once and never again.
 */
public class F110_ZK_4305_DaterangeAnnounceTest extends WebDriverTestCase {

	/** What the box announces for 2026-01-01..2026-01-05 under format="yyyy-MM-dd". */
	private static final String RANGE = "2026-01-01 – 2026-01-05";

	/** The begin date on its own, announced while the pair is still incomplete. */
	private static final String BEGIN_ONLY = "2026-01-01";

	/** The end date on its own, which allowEmpty="both" makes a committed state. */
	private static final String END_ONLY = "2026-01-05";

	// Records the region's text once per mutation batch, which is the
	// granularity assistive tech processes a live region at: writes made in the
	// same task are coalesced before it ever reads them.
	private static final String OBSERVE =
			"window.__announced = [];"
			+ "var st = jq('$dr .z-daterangebox-status')[0];"
			+ "new MutationObserver(function () { window.__announced.push(st.textContent); })"
			+ ".observe(st, {childList: true, characterData: true, subtree: true});";

	// Types into the begin input and commits it the way the browser does, so
	// the parse path (and its rejection) runs exactly as a user would drive it.
	private static final String TYPE_BEGIN =
			"var inp = jq('$dr .z-daterangebox-begin')[0];"
			+ "inp.value = '%s';"
			+ "inp.dispatchEvent(new Event('change', {bubbles: true}));";

	// The client-side commit the Apply button performs. fromUser=false keeps
	// onChange out of the way so the assertions see only the live region.
	private static final String APPLY_RANGE =
			"zk.Widget.$(jq('$dr')[0])"
			+ ".applyRange(new Date(2026, 0, 1), new Date(2026, 0, 5), false);";

	/** Re-applying the range already on screen must be announced again. */
	@Test
	public void testRepeatedRangeIsAnnouncedAgain() {
		connect("/test2/F110-ZK-4305-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(OBSERVE);

		// The region starts empty, so the first apply only has to fill it —
		// clearing an already-empty region mutates nothing.
		js.executeScript(APPLY_RANGE);
		sleep(300); // the region is refilled one task after it is cleared
		assertEquals(List.of(RANGE), announced(js),
				"the first apply must reach the live region");

		// The regression: the SAME range again. Overwriting with an identical
		// string leaves the content unchanged, which is never spoken; the
		// region has to be seen going empty and back.
		js.executeScript("window.__announced = [];");
		js.executeScript(APPLY_RANGE);
		sleep(300);
		assertEquals(List.of("", RANGE), announced(js),
				"re-applying the range already shown must still reach the live region");
	}

	/** A begin/end pair arriving in back-to-back updates announces once, as a pair. */
	@Test
	public void testBurstAcrossTasksAnnouncesOnlyTheFinalPair() {
		connect("/test2/F110-ZK-4305-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(OBSERVE);

		// setBeginValue and setEndValue landing in separate tasks, closer
		// together than the refill delay — a server push split over two AU
		// updates. The half range is on its way to being replaced, so it must
		// not be read out on its own.
		js.executeScript(
				"var w = zk.Widget.$(jq('$dr')[0]);"
				+ "w.setBeginValue(new Date(2026, 0, 1));"
				+ "window.setTimeout(function () { w.setEndValue(new Date(2026, 0, 5)); }, 50);");
		sleep(500);
		List<Object> announced = announced(js);
		assertEquals(List.of(RANGE), announced,
				"only the completed pair may be announced — a lone " + BEGIN_ONLY
				+ " means the half range was read out before the pair, was: " + announced);
	}

	/** An end without a begin is a committed state, so it must be announced. */
	@Test
	public void testEndOnlyRangeIsAnnounced() {
		connect("/test2/F110-ZK-4305-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(OBSERVE);

		// allowEmpty defaults to "both", so an end on its own is a value the
		// user can commit — by typing into the end input or by a one-sided
		// binding write. Falling through to "" leaves the edit unspoken.
		js.executeScript("zk.Widget.$(jq('$dr')[0]).setEndValue(new Date(2026, 0, 5));");
		sleep(300);
		assertEquals(List.of(END_ONLY), announced(js),
				"an end-only range must reach the live region, not the empty string");
	}

	/** Changing the format repaints both inputs; the region must follow. */
	@Test
	public void testFormatChangeReannouncesInTheNewFormat() {
		connect("/test2/F110-ZK-4305-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;

		js.executeScript(APPLY_RANGE);
		sleep(300);
		js.executeScript(OBSERVE);

		// setFormat repaints both inputs through _propagateFormatChange. Without a
		// re-announce the region keeps the yyyy-MM-dd text the inputs no longer show,
		// and only a value change would ever refresh it.
		js.executeScript("zk.Widget.$(jq('$dr')[0]).setFormat('dd/MM/yyyy');");
		sleep(300);
		List<Object> announced = announced(js);
		assertEquals(List.of("", "01/01/2026 – 05/01/2026"), announced,
				"a format change must re-announce the range in the new format, was: " + announced);
	}

	/** An unparseable side must announce the reason, not the stale range. */
	@Test
	public void testRejectedInputAnnouncesTheReasonNotTheStaleRange() {
		connect("/test2/F110-ZK-4305-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;

		js.executeScript(APPLY_RANGE);
		sleep(300);
		js.executeScript(OBSERVE);

		// Both inputs point aria-describedby at this region, so leaving the last
		// good range in it makes an unparseable field describe itself as correct.
		js.executeScript(TYPE_BEGIN.replace("%s", "not-a-date"));
		sleep(400);

		List<Object> announced = announced(js);
		assertFalse(announced.isEmpty(),
				"a rejection must reach the live region, but nothing was announced");
		assertFalse(announced.contains(RANGE),
				"the stale range must not survive a rejection, was: " + announced);
		assertEquals(js.executeScript("return msgzul.RANGE_INVALID;"),
				announced.get(announced.size() - 1),
				"the region must end holding the rejection reason, was: " + announced);
	}

	/**
	 * A server-side rejection reaches the region, and clearing it puts the range
	 * back. Both inputs stay parseable throughout, so this is the path retyping
	 * cannot reach: setErrorMessage / clearErrorMessage are what
	 * WrongValueException and Clients.clearWrongValue land on.
	 */
	@Test
	public void testServerRejectionAndItsClearBothReachTheRegion() {
		connect("/test2/F110-ZK-4305-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;

		js.executeScript(APPLY_RANGE);
		sleep(300);
		js.executeScript(OBSERVE);

		js.executeScript("zk.Widget.$(jq('$dr')[0]).setErrorMessage('at most 7 nights');");
		sleep(400);
		assertEquals("at most 7 nights", last(announced(js)),
				"a server rejection must reach the region, was: " + announced(js));

		// The region published that reason, so retracting the error owes it an
		// announce back — otherwise describedby calls a valid field invalid.
		js.executeScript("zk.Widget.$(jq('$dr')[0]).clearErrorMessage();");
		sleep(400);
		assertEquals(RANGE, last(announced(js)),
				"clearing the error must put the range back, was: " + announced(js));
	}

	private static Object last(List<Object> announced) {
		return announced.isEmpty() ? null : announced.get(announced.size() - 1);
	}

	@SuppressWarnings("unchecked")
	private static List<Object> announced(JavascriptExecutor js) {
		return (List<Object>) js.executeScript("return window.__announced;");
	}
}
