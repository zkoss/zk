/* B110_ZK_6185Test.java

        Purpose:

        Description:

        History:
                Tue Oct 13 10:28:40 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.Widget;

public class B110_ZK_6185Test extends WebDriverTestCase {

	private static final String DEFAULT = "z-checkbox-";
	private static final String TRISTATE = "z-checkbox-tristate-";
	private static final String SWITCH = "z-checkbox-switch-";

	private void clickReal(String id) {
		click(widget("$" + id).$n("real"));
		waitResponse();
	}

	/** Exactly one state class, and it agrees with the native input, the widget and za11y. */
	private void assertLook(String id, String prefix, String state) {
		Widget wgt = widget("$" + id);
		for (String s : new String[] {"on", "off", "indeterminate"})
			assertEquals(s.equals(state), jq(wgt).hasClass(prefix + s), id + " class " + prefix + s);
		boolean indeterminate = "indeterminate".equals(state);
		assertEquals(indeterminate, wgt.$n("real").is("indeterminate"), id + " native indeterminate");
		assertEquals(indeterminate, wgt.is("indeterminate"), id + " widget isIndeterminate()");
		if (!indeterminate)
			assertEquals("on".equals(state), wgt.$n("real").is("checked"), id + " native checked");
		if (Boolean.parseBoolean(getEval("!!window.za11y")))
			assertEquals(indeterminate ? "mixed" : String.valueOf("on".equals(state)),
					jq(wgt.$n("real")).attr("aria-checked"), id + " aria-checked");
	}

	private void assertServerState(String expected) {
		click(jq("$log"));
		waitResponse();
		assertEquals(expected, getZKLog());
		closeZKLog();
	}

	@Test
	public void testClickClearsIndeterminateLook() {
		connect();
		waitResponse();
		assertLook("cb", DEFAULT, "indeterminate");

		clickReal("cb");
		assertLook("cb", DEFAULT, "on");
		assertServerState("CHECKED,UNCHECKED");
	}

	@Test
	public void testServerSetIndeterminateShowsIndeterminateLook() {
		connect();
		waitResponse();
		clickReal("cb");
		clickReal("cb");
		assertLook("cb", DEFAULT, "off");

		// its onCheck calls cb.setChecked(false); cb.setIndeterminate(true)
		clickReal("other");
		assertLook("cb", DEFAULT, "indeterminate");
		assertServerState("INDETERMINATE,UNCHECKED");
	}

	@Test
	public void testTristateMoldCycle() {
		connect();
		waitResponse();
		assertLook("tri", TRISTATE, "off");
		assertLook("triMixed", TRISTATE, "indeterminate");

		String[][] cycle = {{"indeterminate", "INDETERMINATE"}, {"on", "CHECKED"}, {"off", "UNCHECKED"}};
		for (String[] step : cycle) {
			clickReal("tri");
			assertLook("tri", TRISTATE, step[0]);
			assertServerState("INDETERMINATE," + step[1]);
		}
	}

	@Test
	public void testServerClearsRenderedIndeterminate() {
		connect();
		waitResponse();
		assertLook("cb", DEFAULT, "indeterminate");

		click(jq("$clear"));
		waitResponse();
		assertLook("cb", DEFAULT, "off");
		assertServerState("UNCHECKED,UNCHECKED");
	}

	@Test
	public void testServerClearsRuntimeIndeterminate() {
		connect();
		waitResponse();
		clickReal("cb");
		clickReal("cb");
		clickReal("other");
		assertLook("cb", DEFAULT, "indeterminate");

		click(jq("$clear"));
		waitResponse();
		assertLook("cb", DEFAULT, "off");
		assertServerState("UNCHECKED,UNCHECKED");
	}

	@Test
	public void testSwitchMoldAriaCheckedFollowsToggle() {
		connect();
		waitResponse();
		Widget sw = widget("$sw");
		for (boolean checked : new boolean[] {true, false}) {
			click(sw.$n("mold"));
			waitResponse();
			assertEquals(checked, jq(sw).hasClass(SWITCH + "on"), "sw class " + SWITCH + "on");
			if (Boolean.parseBoolean(getEval("!!window.za11y")))
				assertEquals(String.valueOf(checked), jq(sw.$n("mold")).attr("aria-checked"), "sw mold aria-checked");
		}
	}

	@Test
	public void testClientSetCheckedClearsIndeterminate() {
		connect();
		waitResponse();
		assertLook("cb", DEFAULT, "indeterminate");

		eval("zk.Widget.$('$cb').setChecked(true)");
		assertLook("cb", DEFAULT, "on");
	}
}
