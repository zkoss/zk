/* F110_ZK_4305_DaterangeConstraintStringTest.java

		Purpose:

		Description:

		History:
				Wed Sep  2 20:55:34 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Daterangebox exposes its raw constraint string under the name ZK already
 * uses for that contract, {@code getConstraintString()} (the spelling on
 * {@code InputElement}), and round-trips the string verbatim.
 *
 * <p>The probe reads through zscript, so before the rename the BeanShell call
 * resolves to no method, the handler aborts on the first statement and every
 * label keeps its {@code PENDING} placeholder.
 */
public class F110_ZK_4305_DaterangeConstraintStringTest extends WebDriverTestCase {

	@Test
	public void testRawConstraintStringRoundTrips() {
		connect("/test2/F110-ZK-4305-constraint-string.zul");
		waitResponse();

		click(jq("$btnRead"));
		waitResponse();

		assertEquals("no past", jq("$lblNoPast").text(),
				"a ZUML-declared constraint reads back verbatim");
		assertEquals("between 20260101 and 20261231", jq("$lblBetween").text(),
				"a multi-token constraint is not re-serialized");
		assertEquals("null", jq("$lblNone").text(),
				"no constraint declared means no raw string");
		assertEquals("no future", jq("$lblAfterSet").text(),
				"setConstraint(String) at runtime updates the raw string");
	}

	@Test
	public void testPlainSimpleConstraintReadsBack() {
		connect("/test2/F110-ZK-4305-constraint-string.zul");
		waitResponse();

		click(jq("$btnSimple"));
		waitResponse();

		assertEquals("no empty", jq("$lblSimple").text(),
				"any SimpleConstraint yields its raw value, as on InputElement");
	}
}
