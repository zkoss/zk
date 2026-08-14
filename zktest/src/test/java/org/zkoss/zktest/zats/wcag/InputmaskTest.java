/* InputmaskTest.java

	Purpose:

	Description:

	History:
		Thu May 14 16:06:11 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InputmaskTest extends WcagTestCase {
	@Test
	public void test() {
		connect();
		verifyA11y();

		click(jq("$phone"));
		waitResponse();
		verifyA11y();
	}

	// WCAG 2.1 SC 1.3.5 Identify Input Purpose — autocomplete tokens reach the rendered input.
	@Test
	public void testIdentifyInputPurpose() {
		connect();

		Assertions.assertEquals("tel",
				jq("$phone").attr("autocomplete"),
				"ca:autocomplete=\"tel\" must surface on the rendered input");
		Assertions.assertEquals("postal-code",
				jq("$postal").attr("autocomplete"),
				"ca:autocomplete=\"postal-code\" must surface on the rendered input");
	}

	// WCAG 2.1 SC 4.1.3 Status Messages — Inputmask must wire aria-describedby to its hint span
	// so the mask format is announced to assistive tech (SC 3.3.2 Labels or Instructions).
	@Test
	public void testHintIsDescribed() {
		connect();

		String describedBy = jq("$phone").attr("aria-describedby");
		Assertions.assertNotNull(describedBy, "phone input must declare aria-describedby");
		Assertions.assertTrue(describedBy.endsWith("-hint"),
				"aria-describedby should point at the hint span (got: " + describedBy + ")");
		Assertions.assertTrue(jq("#" + describedBy).exists(),
				"the hint element referenced by aria-describedby must be in the DOM");
	}
}
