/* B110_ZK_6067Test.java

	Purpose:

	Description:

	History:
		Wed Sep 09 15:30:48 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6067Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		waitResponse();

		// axe form-field-multiple-labels: a form field must not have more than one label element
		assertEquals(1, labelCount(".z-checkbox-default"));
		assertEquals(1, labelCount(".z-checkbox-switch"));
		assertEquals(1, labelCount(".z-checkbox-toggle"));

		// the mold is a span now, so its cursor is declared rather than inherited from <label>
		assertEquals("pointer", moldCursor(".z-checkbox-switch"));
		assertEquals("pointer", moldCursor(".z-checkbox-toggle"));
	}

	/** How many label elements the browser associates with the checkbox's real input. */
	private int labelCount(String moldClass) {
		return parseInt(getEval("jq('" + moldClass + "').find('input')[0].labels.length"));
	}

	/** The cursor the mold actually renders with, after the whole cascade. */
	private String moldCursor(String moldClass) {
		return getEval("getComputedStyle(jq('" + moldClass + "').find('.z-checkbox-mold')[0]).cursor");
	}
}
