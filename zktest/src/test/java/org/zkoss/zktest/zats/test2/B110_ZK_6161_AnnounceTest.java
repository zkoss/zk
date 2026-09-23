/* B110_ZK_6161_AnnounceTest.java

		Purpose:

		Description:

		History:
				Thu Sep  3 12:15:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.zkoss.test.webdriver.WebDriverTestCase;

@Tag("WcagTestOnly")
public class B110_ZK_6161_AnnounceTest extends WebDriverTestCase {

	private static final String RANGE = "2026-01-01 – 2026-01-05";

	private static final String END_ONLY = "2026-01-05";

	private static final String DR = "zk.Widget.$(jq('$dr')[0])";

	private static final String REGION = "document.getElementById(" + DR + ".uuid + '-status')";


	private static final String TYPE_BEGIN =
			"var inp = jq('$dr .z-daterangebox-begin')[0];"
			+ "inp.value = '%s';"
			+ "inp.dispatchEvent(new Event('change', {bubbles: true}));";

	// fromUser=false keeps onChange out of the way, so only the live region is observed.
	private static final String APPLY_RANGE =
			DR + ".applyRange(new Date(2026, 0, 1), new Date(2026, 0, 5), false);";

	@Test
	public void testPageLoadIsNotAnnounced() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();

		String seeded = "zk.Widget.$(jq('$seeded')[0]).uuid";
		assertTrue(Boolean.parseBoolean(getEval("!!" + REGION)), "no live region: za11y is not loaded");
		assertAll(
				() -> assertEquals("", getEval(REGION + ".textContent"),
						"an empty box must not announce on page load"),
				() -> assertEquals("", getEval(
						"document.getElementById(" + seeded + " + '-status').textContent"),
						"a seeded box must not announce its range on page load"));
	}

	@Test
	public void testChangedRangeIsAnnounced() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		observe(js, REGION);

		js.executeScript(APPLY_RANGE);
		assertEquals(List.of(RANGE), announced(js), "a changed range must reach the live region");
	}

	@Test
	public void testSameRangeAgainIsNotAnnounced() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(APPLY_RANGE);
		observe(js, REGION);

		js.executeScript(APPLY_RANGE);
		assertEquals(List.of(), announced(js), "an unchanged range must not be announced again");
	}

	@Test
	public void testEndOnlyRangeIsAnnounced() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		observe(js, REGION);

		js.executeScript(DR + ".setEndValue(new Date(2026, 0, 5));");
		assertEquals(List.of(END_ONLY), announced(js),
				"an end-only range must reach the live region, not the empty string");
	}

	@Test
	public void testFormatChangeIsAnnouncedInTheNewFormat() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(APPLY_RANGE);
		observe(js, REGION);

		js.executeScript(DR + ".setFormat('dd/MM/yyyy');");
		assertEquals(List.of("01/01/2026 – 05/01/2026"), announced(js),
				"a format change must announce the range in the new format");
	}

	@Test
	public void testRejectedInputIsNotAnnounced() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(APPLY_RANGE);
		observe(js, REGION);

		js.executeScript(TYPE_BEGIN.replace("%s", "not-a-date"));
		assertEquals(List.of(), announced(js), "a rejection must not reach the range region");
	}

	@Test
	public void testEditWhileTheOtherSideIsInvalidWaitsForTheClear() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(APPLY_RANGE);
		js.executeScript(TYPE_BEGIN.replace("%s", "not-a-date"));
		observe(js, REGION);

		js.executeScript("var inp = jq('$dr .z-daterangebox-end')[0];"
				+ "inp.value = '2026-01-20';"
				+ "inp.dispatchEvent(new Event('change', {bubbles: true}));");
		assertEquals(List.of(), announced(js),
				"a range the box has not committed must not be announced");

		js.executeScript(TYPE_BEGIN.replace("%s", "2026-01-01"));
		waitResponse();
		assertEquals(List.of("2026-01-01 – 2026-01-20"), announced(js),
				"fixing the other side commits the range, which must then be announced");
	}

	@Test
	public void testServerRejectionAndItsClearAreNotAnnounced() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(APPLY_RANGE);
		observe(js, REGION);

		js.executeScript(DR + ".setErrorMessage('at most 7 nights');");
		js.executeScript(DR + ".clearErrorMessage();");
		assertEquals(List.of(), announced(js), "the range never changed, so nothing may be announced");
	}

	@Test
	public void testRerenderIsNotAnnounced() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(APPLY_RANGE);

		js.executeScript(DR + ".rerender(-1);");
		assertEquals("", getEval(REGION + ".textContent"),
				"the rebuilt region must not announce the range it already had");

		observe(js, REGION);
		js.executeScript(DR + ".setEndValue(new Date(2026, 0, 9));");
		assertEquals(List.of("2026-01-01 – 2026-01-09"), announced(js),
				"the rebuilt region must announce the next change");
	}

	@Test
	public void testTheRegionIsWiredAndUnseen() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();

		assertAll(
				() -> assertEquals("status", getEval(REGION + ".getAttribute('role')"),
						"the region must be a role=status live region"),
				() -> assertEquals("polite", getEval(REGION + ".getAttribute('aria-live')"),
						"polite, so an announcement never interrupts the user"),
				() -> assertEquals("true", getEval(REGION + ".getAttribute('aria-atomic')"),
						"the whole range must be read, not the diff"),
				() -> assertEquals("absolute", getEval("getComputedStyle(" + REGION + ").position"),
						"the region must be out of flow, not laid out inside the box"),
				// display:none or visibility:hidden would leave every mutation test green.
				() -> assertEquals(1.0, Double.parseDouble(getEval(REGION + ".getBoundingClientRect().width")),
						"the region must paint a 1px-wide box"),
				() -> assertEquals(1.0, Double.parseDouble(getEval(REGION + ".getBoundingClientRect().height")),
						"the region must paint a 1px-high box"),
				() -> assertEquals("visible", getEval("getComputedStyle(" + REGION + ").visibility"),
						"a visibility:hidden region is never announced"),
				() -> assertEquals("false", getEval(
						"jq('$dr .z-daterangebox-begin')[0].hasAttribute('aria-describedby')"),
						"the begin input must not be described by the range region"),
				() -> assertEquals("false", getEval(
						"jq('$dr .z-daterangebox-end')[0].hasAttribute('aria-describedby')"),
						"the end input must not be described by the range region"));
	}

	@Test
	public void testServerRejectedRangeIsNotAnnounced() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		observe(js, region("seeded"));

		commit(js, "$seeded .z-daterangebox-end", "2026-01-02");
		waitResponse();

		assertTrue(jq("$seeded").hasClass("z-daterangebox-invalid"),
				"precondition: minNights=2 rejects a 1-night range");
		assertEquals(List.of(), announced(js), "a range the server rejected must not be announced");
	}

	@Test
	public void testServerAcceptedRangeIsAnnouncedOnce() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		observe(js, region("seeded"));

		commit(js, "$seeded .z-daterangebox-end", "2026-01-09");
		waitResponse();

		assertEquals(List.of("2026-01-01 – 2026-01-09"), announced(js),
				"a range the server accepted must be announced, once");
	}

	@Test
	public void testRangeAnAppListenerRejectsIsNotAnnounced() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		observe(js, region("vetoed"));

		commit(js, "$vetoed .z-daterangebox-end", "2026-01-09");
		waitResponse();

		assertTrue(jq("$vetoed").hasClass("z-daterangebox-invalid"),
				"precondition: the listener's WrongValueException marks the box");
		assertEquals(List.of(), announced(js), "a range the application rejected must not be announced");
	}

	@Test
	public void testRangeAnAppListenerRejectsMidAnimationIsNotAnnounced() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		observe(js, region("vetoed"));
		js.executeScript("jq(document.body).animate({opacity: 0.99}, 1500);");

		commit(js, "$vetoed .z-daterangebox-end", "2026-01-09");
		new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> jq("$vetoed").hasClass("z-daterangebox-invalid"));
		waitResponse();

		assertEquals(List.of(), announced(js),
				"the rejection waits out the animation, so the range must wait behind it, not be announced");
	}

	@Test
	public void testChangeAClientListenerStopsIsAnnouncedAtOnce() {
		connect("/test2/B110-ZK-6161-announce.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		observe(js, region("stopped"));

		commit(js, "$stopped .z-daterangebox-begin", "2026-01-01");

		assertEquals(List.of("2026-01-01"), announced(js),
				"no request carries a stopped onChange, so there is no verdict to wait for");
	}

	@Test
	public void testRedrawDoesNotAnnounceTheErrorAgain() {
		connect("/test2/B110-ZK-6161.zul");
		waitResponse();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		commit(js, "$drb .z-daterangebox-begin", "abc");
		waitResponse();
		assertTrue(jq(".z-errorbox").exists(), "precondition: the parse failure raises the bubble");
		js.executeScript("window.__assertive = [];"
				+ "new MutationObserver(function (ms) { ms.forEach(function (m) { m.addedNodes.forEach(function (n) {"
				+ " window.__assertive.push(n.textContent); }); }); })"
				+ ".observe(document.querySelector('#za11y-announcer [aria-live=assertive]'), {childList: true});");

		js.executeScript("zk.Widget.$('$drb').rerender();");
		waitResponse();

		assertFalse(jq(".z-errorbox").exists(), "a redraw is not news, so the bubble must not reopen");
		assertEquals("[]", getEval("JSON.stringify(window.__assertive)"), "nor may the error be announced again");
		focus(jq("$drb .z-daterangebox-begin"));
		waitResponse();
		assertEquals("[\"You must specify a date. Format: yyyy/MM/dd\"]", getEval("JSON.stringify(window.__assertive)"),
				"returning to the input brings the bubble back, announced once");
	}

	private static String region(String id) {
		return "document.getElementById(zk.Widget.$(jq('$" + id + "')[0]).uuid + '-status')";
	}

	private static void commit(JavascriptExecutor js, String input, String text) {
		js.executeScript("var inp = jq('" + input + "')[0];"
				+ "inp.value = '" + text + "';"
				+ "inp.dispatchEvent(new Event('change', {bubbles: true}));");
	}

	// One entry per mutation batch: writes in the same task are coalesced first.
	private static void observe(JavascriptExecutor js, String region) {
		js.executeScript("window.__announced = [];"
				+ "var st = " + region + ";"
				+ "new MutationObserver(function () { window.__announced.push(st.textContent); })"
				+ ".observe(st, {childList: true, characterData: true, subtree: true});");
	}

	@SuppressWarnings("unchecked")
	private static List<Object> announced(JavascriptExecutor js) {
		return (List<Object>) js.executeScript("return window.__announced;");
	}
}
