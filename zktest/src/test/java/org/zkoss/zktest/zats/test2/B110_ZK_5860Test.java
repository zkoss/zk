/* B110_ZK_5860Test.java

	Purpose:

	Description:

	History:
		Tue Feb 10 11:59:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B110_ZK_5860Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections
						.singletonMap("deviceName", "iPad Mini"));
	}

	@Test
	public void simpleCase() {
		connect();
		verifyTimeWheelStaysWiredAfterConstraintChange("dbox1", "btn1");
	}

	@Test
	public void bindingCase() {
		connect();
		verifyTimeWheelStaysWiredAfterConstraintChange("dbox2", "btn2");
	}

	@Test
	public void dragTimeWheelAfterConstraintChange() {
		connect();
		click(jq("$dbox3 .z-datebox-button"));
		waitResponse(true);
		closeWheelPopup();
		click(jq("$btn3"));
		waitResponse(true);

		click(jq("$dbox3 .z-datebox-button"));
		waitResponse(true);
		dragWheel("dbox3", "hr", 2);
		dragWheel("dbox3", "mi", 3);
		confirmWheelPopup();
		assertEquals("2024/12/25 12:33", jq("$dbox3 .z-datebox-input").val());
	}

	@Test
	public void constraintChangeWhileOpenKeepsWheels() {
		connect();
		click(jq("$dbox3 .z-datebox-button"));
		waitResponse(true);
		// a real click on btn3 closes the popup before the constraint changes
		eval("zAu.send(new zk.Event(zk.Widget.$('$btn3'), 'onClick', {}, {toServer: true}))");
		waitResponse(true);
		assertEquals("between 20241224 and 20241228", getEval("zk.Widget.$('$dbox3').getConstraint()"));
		assertTrue(jq(".z-datebox-popup:visible").exists(), "the popup should stay open");

		confirmWheelPopup();
		assertEquals("2024/12/25 10:30", jq("$dbox3 .z-datebox-input").val());
	}

	private void verifyTimeWheelStaysWiredAfterConstraintChange(String dboxId, String btnId) {
		click(jq("$" + dboxId + " .z-datebox-button"));
		waitResponse(true);
		assertWheelWired(dboxId, "before constraint change");

		closeWheelPopup();

		click(jq("$" + btnId));
		waitResponse(true);
		assertWheelWired(dboxId, "after constraint change");

		click(jq("$" + dboxId + " .z-datebox-button"));
		waitResponse(true);
		assertWheelWired(dboxId, "after reopen post constraint change");
		closeWheelPopup();
	}

	private void closeWheelPopup() {
		click(jq(".z-datebox-popup:visible .z-calendar-wheel-right"));
		waitResponse(true);
	}

	private void confirmWheelPopup() {
		click(jq(".z-datebox-popup:visible .z-calendar-wheel-left"));
		waitResponse(true);
	}

	private void dragWheel(String dboxId, String wheel, int items) {
		String tm = "zk.Widget.$('$" + dboxId + "')._tm";
		String listId = getEval(tm + ".uuid") + "-" + wheel;
		int step = Integer.parseInt(getEval(
				"String(Math.round(document.getElementById('" + listId + "').offsetHeight / 3))"));
		getActions().setActivePointer(PointerInput.Kind.TOUCH, "finger")
				.moveToElement(toElement(jq("#" + listId)))
				.clickAndHold()
				.moveByOffset(0, -step * items)
				// iScroll adds momentum to a release within 300ms of the last move
				.pause(Duration.ofMillis(500))
				.release()
				.perform();
		new WebDriverWait(driver, Duration.ofSeconds(2))
				.until(d -> "false".equals(getEval("String(!!" + tm + "._pickers." + wheel + ".animating)")));
	}

	private void assertWheelWired(String dboxId, String when) {
		// not $n(): its cache can return the detached node after a rerender
		String wired = getEval(
				"(function(){var w=zk.Widget.$('$" + dboxId + "');"
				+ "var tm=w&&w._tm;"
				+ "var p=tm&&tm._pickers&&tm._pickers.hr;"
				+ "if(!p) return 'no-pickers';"
				+ "var liveDiv=document.getElementById(tm.uuid+'-hr');"
				+ "if(!liveDiv) return 'no-dom';"
				+ "var liveUl=liveDiv.children[0];"
				+ "return String(p.scroller===liveUl);})()");
		assertEquals("true", wired,
				"Time wheel iScroll detached from live DOM " + when + " (ZK-5860 regression)");
	}
}
