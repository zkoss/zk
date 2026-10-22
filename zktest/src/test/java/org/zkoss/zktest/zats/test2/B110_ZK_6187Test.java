/* B110_ZK_6187Test.java

        Purpose:

        Description:

        History:
                Mon Oct 12 18:26:44 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6187Test extends WebDriverTestCase {
	private static final String BUTTONS = ".z-messagebox-window .z-messagebox-buttons .z-button";
	private static final String KIND_PREFIX = "z-messagebox-button-";

	@Test
	public void testEachButtonCarriesItsKindClass() {
		connect();
		waitResponse();

		// Messagebox orders the buttons OK, CANCEL, YES, NO, RETRY, ABORT, IGNORE whatever the mask order.
		assertKinds("$okCancel", "ok", "cancel");
		assertKinds("$yesNoCancel", "cancel", "yes", "no");
		assertKinds("$abortRetryIgnore", "retry", "abort", "ignore");
	}

	@Test
	public void testKindFollowsTheButtonNotItsLabel() {
		connect();
		waitResponse();

		click(jq("$customLabels"));
		waitResponse();
		assertEquals("Save|Discard", getEval("jq('" + BUTTONS + "').map(function () {return jq(this).text().trim();}).get().join('|')"));
		click(jq(BUTTONS).eq(1));
		waitResponse();
		assertKinds("$customLabels", "ok", "cancel");
	}

	@Test
	public void testClientBuiltButtonsCarryTheirKindClass() {
		connect();
		waitResponse();

		// jq.alert keeps the key order; a known key's kind is the key in lower case, a custom key has none ("").
		assertKinds("$alertKinds", false, "ok", "cancel", "yes", "no", "retry", "abort", "ignore");
		assertKinds("$alertReload", false, "reload", "cancel");
		assertKinds("$alertCustomKey", false, "", "", "cancel");
	}

	@Test
	public void testClientDefaultButtonKindIsNotItsLocalizedLabel() {
		connect();
		waitResponse();

		click(jq("$clientsAlert"));
		waitResponse();
		assertEquals("Tamam", jq(BUTTONS).text().trim());
		click(jq(BUTTONS).eq(0));
		waitResponse();
		assertKinds("$clientsAlert", false, "ok");
	}

	private void assertKinds(String opener, String... expected) {
		assertKinds(opener, true, expected);
	}

	private void assertKinds(String opener, boolean serverBuilt, String... expected) {
		click(jq(opener));
		waitResponse();

		String[] buttons = getEval("jq('" + BUTTONS + "').map(function () {return this.className;}).get().join('|')")
				.split("\\|");
		List<String> kinds = new ArrayList<>();
		for (String className : buttons) {
			List<String> classes = Arrays.asList(className.trim().split("\\s+"));
			// jq.alert buttons never had z-messagebox-button, and IceBlue sizes that class.
			assertEquals(serverBuilt, classes.contains("z-messagebox-button"), "z-messagebox-button: " + className);
			assertTrue(classes.contains("z-button"), "lost z-button: " + className);
			List<String> own = new ArrayList<>();
			for (String c : classes) {
				if (c.startsWith(KIND_PREFIX))
					own.add(c.substring(KIND_PREFIX.length()));
			}
			assertTrue(own.size() <= 1, "a button must carry at most one kind class: " + className);
			kinds.add(own.isEmpty() ? "" : own.get(0));
		}
		assertEquals(Arrays.asList(expected), kinds, opener);

		click(jq(BUTTONS).eq(0));
		waitResponse();
		assertFalse(jq(".z-messagebox-window").exists(), "the messagebox must close before the next one opens");
	}
}
