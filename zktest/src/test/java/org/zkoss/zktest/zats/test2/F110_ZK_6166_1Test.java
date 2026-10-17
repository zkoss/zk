/* F110_ZK_6166_1Test.java

	Purpose:

	Description:

	History:
		Sat Oct 17 14:40:18 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F110_ZK_6166_1Test extends WebDriverTestCase {
	private static final String[] COUNTS = {"tr", "tl", "br", "bl"};
	private static final String[] DOTS = {"dtr", "dtl", "dbr"};

	private double num(String js) {
		return Double.parseDouble(getEval(js));
	}

	private String badge(String id) {
		return "zk.Widget.$('$" + id + "').$n()";
	}

	private void assertOnCorner(String id) {
		String b = badge(id);
		double cornerX = num("(function(){var b=" + b + ",c=b.querySelector('.z-button').getBoundingClientRect(),"
				+ "i=b.querySelector('.z-badge-indicator').getBoundingClientRect();"
				+ "return i.left + i.width / 2 - (/_right/.test(b.className) ? c.right : c.left);})()");
		double cornerY = num("(function(){var b=" + b + ",c=b.querySelector('.z-button').getBoundingClientRect(),"
				+ "i=b.querySelector('.z-badge-indicator').getBoundingClientRect();"
				+ "return i.top + i.height / 2 - (/z-badge-top/.test(b.className) ? c.top : c.bottom);})()");
		assertEquals(0, cornerX, 0.5, id + ": indicator must be centred on the corner horizontally");
		assertEquals(0, cornerY, 0.5, id + ": indicator must be centred on the corner vertically");
		double spare = num("(function(){var b=" + b + ";return b.getBoundingClientRect().width"
				+ " - b.querySelector('.z-button').getBoundingClientRect().width;})()");
		assertTrue(spare >= 0, id + ": the badge must not be narrower than its button, short by " + -spare + "px");
	}

	private void assertNoOverlap(String... ids) {
		for (int i = 1; i < ids.length; i++) {
			double gap = num("(function(){return " + badge(ids[i]) + ".querySelector('.z-button').getBoundingClientRect().left - "
					+ badge(ids[i - 1]) + ".querySelector('.z-button').getBoundingClientRect().right;})()");
			assertTrue(gap >= 0, ids[i] + "'s button overlaps " + ids[i - 1] + "'s by " + -gap + "px");
		}
	}

	@Test
	public void testCountBadgesInANarrowFlexRow() {
		connect();
		waitResponse();
		for (String id : COUNTS)
			assertOnCorner(id);
		assertNoOverlap(COUNTS);
	}

	@Test
	public void testDotBadgesInANarrowFlexRow() {
		connect();
		waitResponse();
		for (String id : DOTS)
			assertOnCorner(id);
		assertNoOverlap(DOTS);
	}
}
