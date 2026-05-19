/* F104_ZK_6096Test.java

		Purpose:

		Description:

		History:
				Tue May 19 16:10:10 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

public class F104_ZK_6096Test extends WebDriverTestCase {

	@Test
	public void testStubonlyDescendants() {
		connect("/test2/F104-ZK-6096.zul");
		click(jq("$probe"));
		waitResponse();

		final Map<String, String> state = readProbe();

		// (1) baseline — no policy, everything is live
		assertEquals("LIVE", state.get("g0lbl"));
		assertEquals("LIVE", state.get("g0btn"));

		assertEquals("LIVE", state.get("g1"));
		assertEquals("LIVE", state.get("g2"));
		assertEquals("LIVE", state.get("g3"));
		assertEquals("LIVE", state.get("g5"));

		// (2) policy on parent — div/label match, button does not
		assertEquals("STUB", state.get("g1div"));
		assertEquals("STUB", state.get("g1lbl"));
		assertEquals("LIVE", state.get("g1btn"));

		// (3) explicit stubonly="false" overrides a policy match
		assertEquals("LIVE", state.get("g2divLive"));
		assertEquals("STUB", state.get("g2divStub"));
		// non-cascading: label child of a policy-stubbed div stays live when
		// the policy only lists "div" (this is the key behavior ZK-6096 adds)
		assertEquals("LIVE", state.get("g2lbl"));
		assertEquals("LIVE", state.get("g2lblStub"));

		// (4) wildcard — every descendant matches, but explicit "false" still wins
		assertEquals("STUB", state.get("g3div"));
		assertEquals("STUB", state.get("g3lbl"));
		assertEquals("LIVE", state.get("g3btn"));

		// (5) alias chain — myDiv extends div, so policy="div" matches myDiv via
		// ComponentDefinition.getExtends() walking up the chain
		assertEquals("STUB", state.get("g5myDiv"));
		// non-cascading: the label child stays live (only "div" is in the policy)
		assertEquals("LIVE", state.get("g5lbl"));

		// (6) combined with stubonly="true" on the SAME comp — the legacy
		// cascade wins; descendants are merged into a single StubsComponent
		// at the anchor, becoming unreachable by id-walk (that's the legacy
		// merge behavior, unchanged by this PR). The anchor itself stays
		// findable — StubsComponent extends StubComponent and the id is
		// preserved by replace(...).
		assertEquals("STUB", state.get("g6"));

		// the probe button itself must stay live for the test to function
		assertEquals("LIVE", state.get("probe"));
	}

	private Map<String, String> readProbe() {
		final String raw = jq("$result").text();
		final Map<String, String> map = new HashMap<>();
		for (String entry : raw.split(",")) {
			final int eq = entry.indexOf('=');
			if (eq > 0)
				map.put(entry.substring(0, eq), entry.substring(eq + 1));
		}
		return map;
	}
}
