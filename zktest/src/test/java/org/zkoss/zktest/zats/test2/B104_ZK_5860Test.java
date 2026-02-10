/* B104_ZK_5860Test.java

	Purpose:

	Description:

	History:
		Tue Feb 10 11:59:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeOptions;
import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class B104_ZK_5860Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/enable-tablet-ui-zk.xml");

	@Override
	protected ChromeOptions getWebDriverOptions() {
		return super.getWebDriverOptions()
				.setExperimentalOption("mobileEmulation", Collections
						.singletonMap("deviceName", "iPad"));
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

	private void verifyTimeWheelStaysWiredAfterConstraintChange(String dboxId, String btnId) {
		// First open: triggers ROD render and CalendarTime bind, which creates iScroll pickers.
		click(jq("$" + dboxId + " .z-datebox-button"));
		waitResponse(true);
		assertWheelWired(dboxId, "before constraint change");

		closeWheelPopup();

		// Trigger constraint change. In tablet UI, Datebox.setConstraint calls _pop.rerender();
		// the regenerated popup DOM also contains _tm's wheel HTML. Without the ZK-5860 fix,
		// _tm's iScroll pickers still reference the now-detached UL elements.
		click(jq("$" + btnId));
		waitResponse(true);
		assertWheelWired(dboxId, "after constraint change");

		// Smoke check: re-open the wheel popup and confirm everything still binds cleanly.
		click(jq("$" + dboxId + " .z-datebox-button"));
		waitResponse(true);
		assertWheelWired(dboxId, "after reopen post constraint change");
		closeWheelPopup();
	}

	private void closeWheelPopup() {
		click(jq(".z-datebox-popup:visible .z-calendar-wheel-right"));
		waitResponse(true);
	}

	private void assertWheelWired(String dboxId, String when) {
		// $n() caches DOM lookups, so after a rerender it can hand back the now-detached
		// element. Bypass the cache with document.getElementById to compare the iScroll
		// scroller against the live DOM, which is what the user actually scrolls.
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
